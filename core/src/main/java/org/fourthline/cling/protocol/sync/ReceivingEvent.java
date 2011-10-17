/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventResponseMessage;
import org.fourthline.cling.model.resource.ServiceEventCallbackResource;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.transport.spi.UnsupportedDataException;

import java.util.logging.Logger;

/**
 * Handles incoming GENA event messages.
 * <p>
 * Attempts to find an outgoing (remote) subscription matching the callback and subscription identifier.
 * Once found, the GENA event message payload will be transformed and the
 * {@link org.fourthline.cling.model.gena.RemoteGENASubscription#receive(org.fourthline.cling.model.types.UnsignedIntegerFourBytes,
 * java.util.Collection)} method will be called asynchronously using the executor
 * returned by {@link org.fourthline.cling.UpnpServiceConfiguration#getRegistryListenerExecutor()}.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingEvent extends ReceivingSync<StreamRequestMessage, OutgoingEventResponseMessage> {

    final private static Logger log = Logger.getLogger(ReceivingEvent.class.getName());

    public ReceivingEvent(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    protected OutgoingEventResponseMessage executeSync() {

        if (!getInputMessage().isContentTypeTextUDA()) {
            log.warning("Received without or with invalid Content-Type: " + getInputMessage());
            // We continue despite the invalid UPnP message because we can still hope to convert the content
            // return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }

        ServiceEventCallbackResource resource =
                getUpnpService().getRegistry().getResource(
                        ServiceEventCallbackResource.class,
                        getInputMessage().getUri()
                );

        if (resource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.NOT_FOUND));
        }

        final IncomingEventRequestMessage requestMessage =
                new IncomingEventRequestMessage(getInputMessage(), resource.getModel());

        // Error conditions UDA 1.0 section 4.2.1
        if (requestMessage.getSubscrptionId() == null) {
            log.fine("Subscription ID missing in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        if (!requestMessage.hasValidNotificationHeaders()) {
            log.fine("Missing NT and/or NTS headers in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.BAD_REQUEST));
        }

        if (!requestMessage.hasValidNotificationHeaders()) {
            log.fine("Invalid NT and/or NTS headers in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        if (requestMessage.getSequence() == null) {
            log.fine("Sequence missing in event request: " + getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        try {

            getUpnpService().getConfiguration().getGenaEventProcessor().readBody(requestMessage);

        } catch (UnsupportedDataException ex) {
            log.fine("Can't read request body, " + ex);
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
        }

        try {
            // Prevent registration of outgoing subscriptions while this event is being processed, and
            // block if there is an ongoing subscription procedure (most likely this is the initial
            // event for this subscription)
            getUpnpService().getRegistry().lockRemoteSubscriptions();

            final RemoteGENASubscription subscription =
                    getUpnpService().getRegistry().getRemoteSubscription(requestMessage.getSubscrptionId());

            if (subscription == null) {
                log.severe("Invalid subscription ID, no active subscription: " + requestMessage);
                return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
            }

            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                    new Runnable() {
                        public void run() {
                            log.fine("Calling active subscription with event state variable values");
                            subscription.receive(
                                    requestMessage.getSequence(),
                                    requestMessage.getStateVariableValues()
                            );
                        }
                    }
            );
        } finally {
            getUpnpService().getRegistry().unlockRemoteSubscriptions();
        }

        return new OutgoingEventResponseMessage();

    }
}
