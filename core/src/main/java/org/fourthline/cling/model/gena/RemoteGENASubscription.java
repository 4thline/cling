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

package org.fourthline.cling.model.gena;

import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.UnsupportedDataException;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An outgoing subscription to a remote service.
 * <p>
 * Once established, calls its {@link #eventReceived()} method whenever an event has
 * been received from the remote service.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class RemoteGENASubscription extends GENASubscription<RemoteService> {

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    protected RemoteGENASubscription(RemoteService service,
                                     int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
    }

    synchronized public URL getEventSubscriptionURL() {
        return getService().getDevice().normalizeURI(
                getService().getEventSubscriptionURI()
        );
    }

    synchronized public List<URL> getEventCallbackURLs(List<NetworkAddress> activeStreamServers, Namespace namespace) {
        List<URL> callbackURLs = new ArrayList<>();
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            callbackURLs.add(
                    new Location(
                            activeStreamServer,
                            namespace.getEventCallbackPathString(getService())
                    ).getURL());
        }
        return callbackURLs;
    }

    /* The following four methods should always be called in an independent thread, not within the
       message receiving thread. Otherwise the user who implements the abstract delegate methods can
       block the network communication.
     */

    synchronized public void establish() {
        established();
    }

    synchronized public void fail(UpnpResponse responseStatus) {
        failed(responseStatus);
    }

    synchronized public void end(CancelReason reason, UpnpResponse response) {
        ended(reason, response);
    }

    synchronized public void receive(UnsignedIntegerFourBytes sequence, Collection<StateVariableValue> newValues) {

        if (this.currentSequence != null) {

            // TODO: Handle rollover to 1!
            if (this.currentSequence.getValue().equals(this.currentSequence.getBits().getMaxValue()) && sequence.getValue() == 1) {
                System.err.println("TODO: HANDLE ROLLOVER");
                return;
            }

            if (this.currentSequence.getValue() >= sequence.getValue()) {
                return;
            }

            int difference;
            long expectedValue = currentSequence.getValue() + 1;
            if ((difference = (int) (sequence.getValue() - expectedValue)) != 0) {
                eventsMissed(difference);
            }

        }

        this.currentSequence = sequence;

        for (StateVariableValue newValue : newValues) {
            currentValues.put(newValue.getStateVariable().getName(), newValue);
        }

        eventReceived();
    }
    
    public abstract void invalidMessage(UnsupportedDataException ex);

    public abstract void failed(UpnpResponse responseStatus);

    public abstract void ended(CancelReason reason, UpnpResponse responseStatus);

    public abstract void eventsMissed(int numberOfMissedEvents);

    @Override
    public String toString() {
        return "(SID: " + getSubscriptionId() + ") " + getService();
    }
}
