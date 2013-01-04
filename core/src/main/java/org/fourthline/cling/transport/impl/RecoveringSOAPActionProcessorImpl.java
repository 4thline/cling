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

package org.fourthline.cling.transport.impl;

import java.util.logging.Logger;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.xml.XmlPullParserUtils;

import javax.enterprise.inject.Alternative;

/**
 * Implementation based on the <em>Xml Pull Parser</em> XML processing API.
 * <p>
 * This processor extends {@link PullSOAPActionProcessorImpl}, it will always
 * first try to read messages regularly with the superclass' methods before
 * trying to recover from a failure.
 * </p>
 * <p>
 * When the superclass can't read the message, this processor will try to
 * recover from broken XML by for example, detecting wrongly encoded XML entities,
 * and working around other vendor-specific bugs caused by incompatible UPnP
 * stacks in the wild.
 * </p>
 * <p>
 * Additionally any {@link UnsupportedDataException} thrown while reading an
 * XML message will be passed on to the
 * {@link #handleInvalidMessage(org.fourthline.cling.model.action.ActionInvocation, org.fourthline.cling.model.UnsupportedDataException, org.fourthline.cling.model.UnsupportedDataException)}
 * method for you to handle. The default implementation will simply throw the
 * original exception from the first processing attempt.
 * </p>
 *
 * @author Michael Pujos
 */
@Alternative
public class RecoveringSOAPActionProcessorImpl extends PullSOAPActionProcessorImpl {

    private static Logger log = Logger.getLogger(SOAPActionProcessor.class.getName());

    public void readBody(ActionRequestMessage requestMessage, ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(requestMessage, actionInvocation);
        } catch (UnsupportedDataException ex) {

            // Can't recover from this
            if (!requestMessage.isBodyNonEmptyString())
                throw ex;

            log.warning("Trying to recover from invalid SOAP XML request: " + ex);
            String body = getMessageBody(requestMessage);

            // TODO: UPNP VIOLATION: TwonkyMobile sends unencoded '&' in SetAVTransportURI action calls:
            // <CurrentURI>http://192.168.1.14:56923/content/12a470d854dbc6887e4103e3140783fd.wav?profile_id=0&convert=wav</CurrentURI>
            String fixedBody = XmlPullParserUtils.fixXMLEntities(body);

            try {
                // Try again, if this fails, we are done...
                requestMessage.setBody(fixedBody);
                super.readBody(requestMessage, actionInvocation);
            } catch (UnsupportedDataException ex2) {
                handleInvalidMessage(actionInvocation, ex, ex2);
            }
        }
    }

    public void readBody(ActionResponseMessage responseMsg, ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(responseMsg, actionInvocation);
        } catch (UnsupportedDataException ex) {

            // Can't recover from this
            if (!responseMsg.isBodyNonEmptyString())
                throw ex;

            log.warning("Trying to recover from invalid SOAP XML response: " + ex);
            String body = getMessageBody(responseMsg);

            // TODO: UPNP VIOLATION: TwonkyMobile doesn't properly encode '&'
            String fixedBody = XmlPullParserUtils.fixXMLEntities(body);

            // TODO: UPNP VIOLATION: YAMAHA NP-S2000 does not terminate XML with </s:Envelope>
            // (at least for action GetPositionInfo)
            if (fixedBody.endsWith("</s:Envelop")) {
                fixedBody += "e>";
            }

            try {
                // Try again, if this fails, we are done...
                responseMsg.setBody(fixedBody);
                super.readBody(responseMsg, actionInvocation);
            } catch (UnsupportedDataException ex2) {
                handleInvalidMessage(actionInvocation, ex, ex2);
            }
        }
    }

    /**
     * Handle processing errors while reading of XML messages.
     *
     * <p>
     * Typically you want to log this problem or create an error report, and in any
     * case, throw an {@link UnsupportedDataException} to notify the caller of the
     * processor of this failure.
     * </p>
     * <p>
     * You can access the invalid XML with
     * {@link org.fourthline.cling.model.UnsupportedDataException#getData()}.
     * </p>
     *
     * @param originalException   The original exception throw by the first parsing attempt
     * @param recoveringException The exception thrown after trying to fix the XML.
     */
    protected void handleInvalidMessage(ActionInvocation actionInvocation,
                                        UnsupportedDataException originalException,
                                        UnsupportedDataException recoveringException) throws UnsupportedDataException {
        throw originalException;
    }
}
