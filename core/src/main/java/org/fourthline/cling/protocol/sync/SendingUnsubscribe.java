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
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingUnsubscribeRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

import java.util.logging.Logger;

/**
 * Disconnecting a GENA event subscription with a remote host.
 * <p>
 * Calls the {@link org.fourthline.cling.model.gena.RemoteGENASubscription#end(org.fourthline.cling.model.gena.CancelReason, org.fourthline.cling.model.message.UpnpResponse)}
 * method if the subscription request was responded to correctly. No {@link org.fourthline.cling.model.gena.CancelReason}
 * will be provided if the unsubscribe procedure completed as expected, otherwise <code>UNSUBSCRIBE_FAILED</code>
 * is used. The response might be <code>null</code> if no response was received from the remote host.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingUnsubscribe extends SendingSync<OutgoingUnsubscribeRequestMessage, StreamResponseMessage> {

    final private static Logger log = Logger.getLogger(SendingUnsubscribe.class.getName());

    final protected RemoteGENASubscription subscription;

    public SendingUnsubscribe(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(
            upnpService,
            new OutgoingUnsubscribeRequestMessage(
                subscription,
                upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())
            )
        );
        this.subscription = subscription;
    }

    protected StreamResponseMessage executeSync() {

        log.fine("Sending unsubscribe request: " + getInputMessage());

        final StreamResponseMessage response = getUpnpService().getRouter().send(getInputMessage());

        // Always remove from the registry and return the response status - even if it's failed
        getUpnpService().getRegistry().removeRemoteSubscription(subscription);

        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                new Runnable() {
                    public void run() {
                        if (response == null) {
                            log.fine("Unsubscribe failed, no response received");
                            subscription.end(CancelReason.UNSUBSCRIBE_FAILED, null);
                        } else if (response.getOperation().isFailed()) {
                            log.fine("Unsubscribe failed, response was: " + response);
                            subscription.end(CancelReason.UNSUBSCRIBE_FAILED, response.getOperation());
                        } else {
                            log.fine("Unsubscribe successful, response was: " + response);
                            subscription.end(null, response.getOperation());
                        }
                    }
                }
        );

        return response;
    }
}