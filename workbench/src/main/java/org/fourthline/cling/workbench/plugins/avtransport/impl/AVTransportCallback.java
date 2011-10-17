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
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.logging.LogMessage;
import org.seamless.util.Exceptions;

import javax.swing.SwingUtilities;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
abstract public class AVTransportCallback extends SubscriptionCallback {

    final private static Logger log = Logger.getLogger(AVTransportCallback.class.getName());

    public AVTransportCallback(Service service) {
        super(service);
    }

    @Override
    protected void failed(GENASubscription subscription,
                          UpnpResponse responseStatus,
                          Exception exception,
                          String defaultMsg) {
        log.severe(defaultMsg);
    }

    protected void established(GENASubscription subscription) {
        Workbench.log(new LogMessage(
                Level.INFO,
                "AVTransport ControlPoint",
                "Subscription with service established, listening for events."
        ));
    }

    protected void ended(GENASubscription subscription, final CancelReason reason, UpnpResponse responseStatus) {
        Workbench.log(new LogMessage(
                reason != null ? Level.WARNING : Level.INFO,
                "AVTransport ControlPoint",
                "Subscription with service ended. " + (reason != null ? "Reason: " + reason : "")
        ));
        onDisconnect(reason);
    }

    protected void eventReceived(GENASubscription subscription) {
        log.finer("Event received, sequence number: " + subscription.getCurrentSequence());

        final LastChange lastChange;
        try {
            lastChange = new LastChange(
                    new AVTransportLastChangeParser(),
                    subscription.getCurrentValues().get("LastChange").toString()
            );
        } catch (Exception ex) {
            log.warning("Error parsing LastChange event content: " + ex);
            log.warning("Cause: " + Exceptions.unwrap(ex));
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (UnsignedIntegerFourBytes instanceId : lastChange.getInstanceIDs()) {

                    log.finer("Processing LastChange event values for instance: " + instanceId);

                    AVTransportVariable.TransportState transportState =
                            lastChange.getEventedValue(
                                    instanceId,
                                    AVTransportVariable.TransportState.class
                            );

                    if (transportState != null) {
                        log.finer("AVTransport service state changed to: " + transportState.getValue());
                        onStateChange(
                                new Long(instanceId.getValue()).intValue(),
                                transportState.getValue()
                        );
                    }

                    AVTransportVariable.CurrentTrackURI currentTrackURI =
                            lastChange.getEventedValue(instanceId, AVTransportVariable.CurrentTrackURI.class);
                    if (currentTrackURI != null) {
                        log.fine("AVTransport service CurrentTrackURI changed to: " + currentTrackURI.getValue());
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
        log.warning("Events missed (" + numberOfMissedEvents + "), consider restarting this control point!");
    }

    abstract protected void onDisconnect(CancelReason reason);

    abstract protected void onStateChange(int instanceId, TransportState state);

    abstract protected void onCurrentTrackURIChange(int instanceId, String uri);

}
