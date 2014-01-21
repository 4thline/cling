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

import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingSubscribeRequestMessage;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.protocol.SendingSync;
import org.fourthline.cling.transport.RouterException;

import java.util.List;
import java.util.logging.Logger;

/**
 * Establishing a GENA event subscription with a remote host.
 * <p>
 * Calls the {@link org.fourthline.cling.model.gena.RemoteGENASubscription#establish()} method
 * if the subscription request was responded to correctly.
 * </p>
 * <p>
 * The {@link org.fourthline.cling.model.gena.RemoteGENASubscription#fail(org.fourthline.cling.model.message.UpnpResponse)}
 * method will be called if the request failed. No response from the remote host is indicated with
 * a <code>null</code> argument value. Note that this is also the response if the subscription has
 * to be aborted early, when no local stream server for callback URL creation is available. This is
 * the case when the local network transport layer is switched off, subscriptions will fail
 * immediately with no response.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingSubscribe extends SendingSync<OutgoingSubscribeRequestMessage, IncomingSubscribeResponseMessage> {

    final private static Logger log = Logger.getLogger(SendingSubscribe.class.getName());

    final protected RemoteGENASubscription subscription;

    public SendingSubscribe(UpnpService upnpService,
                            RemoteGENASubscription subscription,
                            List<NetworkAddress> activeStreamServers) {
        super(
            upnpService,
            new OutgoingSubscribeRequestMessage(
                subscription,
                subscription.getEventCallbackURLs(
                    activeStreamServers,
                    upnpService.getConfiguration().getNamespace()
                ),
                upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())
            )
        );

        this.subscription = subscription;
    }

    protected IncomingSubscribeResponseMessage executeSync() throws RouterException {

        if (!getInputMessage().hasCallbackURLs()) {
            log.fine("Subscription failed, no active local callback URLs available (network disabled?)");
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                new Runnable() {
                    public void run() {
                        subscription.fail(null);
                    }
                }
            );
            return null;
        }

        log.fine("Sending subscription request: " + getInputMessage());

        try {
            // register this pending Subscription to bloc if the notification is received before the
            // registration result.
            getUpnpService().getRegistry().registerPendingRemoteSubscription(subscription);

            StreamResponseMessage response = null;
            try {
                response = getUpnpService().getRouter().send(getInputMessage());
            } catch (RouterException ex) {
                onSubscriptionFailure();
                return null;
            }

            if (response == null) {
                onSubscriptionFailure();
                return null;
            }

            final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);

            if (response.getOperation().isFailed()) {
                log.fine("Subscription failed, response was: " + responseMessage);
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                    new Runnable() {
                        public void run() {
                            subscription.fail(responseMessage.getOperation());
                        }
                    }
                );
            } else if (!responseMessage.isValidHeaders()) {
                log.severe("Subscription failed, invalid or missing (SID, Timeout) response headers");
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                    new Runnable() {
                        public void run() {
                            subscription.fail(responseMessage.getOperation());
                        }
                    }
                );
            } else {

                log.fine("Subscription established, adding to registry, response was: " + response);
                subscription.setSubscriptionId(responseMessage.getSubscriptionId());
                subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());

                getUpnpService().getRegistry().addRemoteSubscription(subscription);

                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
                    new Runnable() {
                        public void run() {
                            subscription.establish();
                        }
                    }
                );

            }
            return responseMessage;
        } finally {
            getUpnpService().getRegistry().unregisterPendingRemoteSubscription(subscription);
        }
    }

    protected void onSubscriptionFailure() {
        log.fine("Subscription failed");
        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
            new Runnable() {
                public void run() {
                    subscription.fail(null);
                }
            }
        );
    }
}
