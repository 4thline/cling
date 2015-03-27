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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;
import org.seamless.util.Exceptions;

import javax.swing.*;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
abstract public class AVTransportCallback extends SubscriptionCallback {

    public AVTransportCallback(Service service) {
        super(service);
    }

    @Override
    protected void failed(GENASubscription subscription,
                          UpnpResponse responseStatus,
                          Exception exception,
                          String defaultMsg) {
        AVTransportControlPoint.LOGGER.severe(defaultMsg);
    }

    protected void established(GENASubscription subscription) {
        AVTransportControlPoint.LOGGER.info(
            "Subscription with service established, listening for events."
        );
    }

    protected void ended(GENASubscription subscription, final CancelReason reason, UpnpResponse responseStatus) {
        AVTransportControlPoint.LOGGER.log(
            reason != null ? Level.WARNING : Level.INFO,
            "Subscription with service ended. " + (reason != null ? "Reason: " + reason : "")
        );
        onDisconnect(reason);
    }

    protected void eventReceived(GENASubscription subscription) {
        AVTransportControlPoint.LOGGER.fine(
            "Event received, sequence number: " + subscription.getCurrentSequence()
        );

        final LastChange lastChange;
        try {
            lastChange = new LastChange(
                    new AVTransportLastChangeParser(),
                    subscription.getCurrentValues().get("LastChange").toString()
            );
        } catch (Exception ex) {
            AVTransportControlPoint.LOGGER.warning(
                "Error parsing LastChange event content: " + ex
            );
            AVTransportControlPoint.LOGGER.warning(
                "Cause: " + Exceptions.unwrap(ex)
            );
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (UnsignedIntegerFourBytes instanceId : lastChange.getInstanceIDs()) {

                    AVTransportControlPoint.LOGGER.fine(
                        "Processing LastChange event values for instance: " + instanceId
                    );

                    AVTransportVariable.TransportState transportState =
                            lastChange.getEventedValue(
                                    instanceId,
                                    AVTransportVariable.TransportState.class
                            );

                    if (transportState != null) {
                        AVTransportControlPoint.LOGGER.fine(
                            "AVTransport service state changed to: " + transportState.getValue());
                        onStateChange(
                                new Long(instanceId.getValue()).intValue(),
                                transportState.getValue()
                        );
                    }

                    AVTransportVariable.CurrentPlayMode currentPlayMode =
                            lastChange.getEventedValue(
                                    instanceId,
                                    AVTransportVariable.CurrentPlayMode.class
                            );

                    if (currentPlayMode != null) {
                        AVTransportControlPoint.LOGGER.fine(
                                "AVTransport service CurrentPlayMode change to: " + currentPlayMode.getValue());
                        onPlayModeChange(
                                new Long(instanceId.getValue()).intValue(),
                                currentPlayMode.getValue()
                        );
                    }


                    AVTransportVariable.CurrentTrackURI currentTrackURI =
                            lastChange.getEventedValue(instanceId, AVTransportVariable.CurrentTrackURI.class);
                    if (currentTrackURI != null) {
                        AVTransportControlPoint.LOGGER.fine(
                            "AVTransport service CurrentTrackURI changed to: " + currentTrackURI.getValue()
                        );
                        onCurrentTrackURIChange(
                                new Long(instanceId.getValue()).intValue(),
                                currentTrackURI.getValue() != null ? currentTrackURI.getValue().toString() : ""
                        );
                    }
                }
            }
        });
    }

    protected void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        AVTransportControlPoint.LOGGER.warning(
            "Events missed (" + numberOfMissedEvents + "), consider restarting this control point!"
        );
    }

    abstract protected void onDisconnect(CancelReason reason);

    abstract protected void onStateChange(int instanceId, TransportState state);

    abstract protected void onPlayModeChange(int instanceId, PlayMode playMode);

    abstract protected void onCurrentTrackURIChange(int instanceId, String uri);

}
