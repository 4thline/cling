/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionCancelledException;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.message.control.OutgoingActionRequestMessage;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.protocol.SendingSync;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.transport.RouterException;
import org.seamless.util.Exceptions;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sending control message, transforming a local {@link org.fourthline.cling.model.action.ActionInvocation}.
 * <p>
 * Writes the outgoing message's body with the {@link org.fourthline.cling.transport.spi.SOAPActionProcessor}.
 * This protocol will return <code>null</code> if no response was received from the control target host.
 * In all other cases, even if only the processing of message content failed, this protocol will
 * return an {@link org.fourthline.cling.model.message.control.IncomingActionResponseMessage}. Any error
 * details of a failed response ({@link org.fourthline.cling.model.message.UpnpResponse#isFailed()}) are
 * available with
 * {@link org.fourthline.cling.model.action.ActionInvocation#setFailure(org.fourthline.cling.model.action.ActionException)}.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingAction extends SendingSync<OutgoingActionRequestMessage, IncomingActionResponseMessage> {

    final private static Logger log = Logger.getLogger(SendingAction.class.getName());

    final protected ActionInvocation actionInvocation;

    public SendingAction(UpnpService upnpService, ActionInvocation actionInvocation, URL controlURL) {
        super(upnpService, new OutgoingActionRequestMessage(actionInvocation, controlURL));
        this.actionInvocation = actionInvocation;
    }

    protected IncomingActionResponseMessage executeSync() throws RouterException {
        return invokeRemote(getInputMessage());
    }

    protected IncomingActionResponseMessage invokeRemote(OutgoingActionRequestMessage requestMessage) throws RouterException {
        Device device = actionInvocation.getAction().getService().getDevice();

        log.fine("Sending outgoing action call '" + actionInvocation.getAction().getName() + "' to remote service of: " + device);
        IncomingActionResponseMessage responseMessage = null;
        try {

            StreamResponseMessage streamResponse = sendRemoteRequest(requestMessage);

            if (streamResponse == null) {
                log.fine("No connection or no no response received, returning null");
                actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Connection error or no response received"));
                return null;
            }

            responseMessage = new IncomingActionResponseMessage(streamResponse);

            if (responseMessage.isFailedNonRecoverable()) {
                log.fine("Response was a non-recoverable failure: " + responseMessage);
                throw new ActionException(
                        ErrorCode.ACTION_FAILED, "Non-recoverable remote execution failure: " + responseMessage.getOperation().getResponseDetails()
                );
            } else if (responseMessage.isFailedRecoverable()) {
                handleResponseFailure(responseMessage);
            } else {
                handleResponse(responseMessage);
            }

            return responseMessage;


        } catch (ActionException ex) {
            log.fine("Remote action invocation failed, returning Internal Server Error message: " + ex.getMessage());
            actionInvocation.setFailure(ex);
            if (responseMessage == null || !responseMessage.getOperation().isFailed()) {
                return new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
            } else {
                return responseMessage;
            }
        }
    }

    protected StreamResponseMessage sendRemoteRequest(OutgoingActionRequestMessage requestMessage)
        throws ActionException, RouterException {

        try {
            log.fine("Writing SOAP request body of: " + requestMessage);
            getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(requestMessage, actionInvocation);

            log.fine("Sending SOAP body of message as stream to remote device");
            return getUpnpService().getRouter().send(requestMessage);
        } catch (RouterException ex) {
            Throwable cause = Exceptions.unwrap(ex);
            if (cause instanceof InterruptedException) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Sending action request message was interrupted: " + cause);
                }
                throw new ActionCancelledException((InterruptedException)cause);
            }
            throw ex;
        } catch (UnsupportedDataException ex) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Error writing SOAP body: " + ex);
                log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            }
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error writing request message. " + ex.getMessage());
        }
    }

    protected void handleResponse(IncomingActionResponseMessage responseMsg) throws ActionException {

        try {
            log.fine("Received response for outgoing call, reading SOAP response body: " + responseMsg);
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, actionInvocation);
        } catch (UnsupportedDataException ex) {
            log.fine("Error reading SOAP body: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(
                ErrorCode.ACTION_FAILED,
                "Error reading SOAP response message. " + ex.getMessage(),
                false
            );
        }
    }

    protected void handleResponseFailure(IncomingActionResponseMessage responseMsg) throws ActionException {

        try {
            log.fine("Received response with Internal Server Error, reading SOAP failure message");
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, actionInvocation);
        } catch (UnsupportedDataException ex) {
            log.fine("Error reading SOAP body: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(
                ErrorCode.ACTION_FAILED,
                "Error reading SOAP response failure message. " + ex.getMessage(),
                false
            );
        }
    }

}

/*

- send request
   - UnsupportedDataException: Can't write body

- streamResponseMessage is null: No response received, return null to client

- streamResponseMessage >= 300 && !(405 || 500): Response was HTTP failure, set on anemic response and return

- streamResponseMessage >= 300 && 405: Try request again with different headers
   - UnsupportedDataException: Can't write body
   - (The whole streamResponse conditions apply again but this time, ignore 405)

- streamResponseMessage >= 300 && 500 && lastExecutionFailure != null: Try to read SOAP failure body
   - UnsupportedDataException: Can't read body

- streamResponseMessage < 300: Response was OK, try to read response body
   - UnsupportedDataException: Can't read body


*/