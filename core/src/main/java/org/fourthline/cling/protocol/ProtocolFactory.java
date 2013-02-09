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

package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.protocol.sync.SendingAction;
import org.fourthline.cling.protocol.sync.SendingEvent;
import org.fourthline.cling.protocol.sync.SendingRenewal;
import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.protocol.sync.SendingUnsubscribe;

import java.net.URL;

/**
 * Factory for UPnP protocols, the core implementation of the UPnP specification.
 * <p>
 * This factory creates an executable protocol either based on the received UPnP messsage, or
 * on local device/search/service metadata). A protocol is an aspect of the UPnP specification,
 * you can override individual protocols to customize the behavior of the UPnP stack.
 * </p>
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 * 
 * @author Christian Bauer
 */
public interface ProtocolFactory {

    public UpnpService getUpnpService();

    /**
     * Creates a {@link org.fourthline.cling.protocol.async.ReceivingNotification},
     * {@link org.fourthline.cling.protocol.async.ReceivingSearch},
     * or {@link org.fourthline.cling.protocol.async.ReceivingSearchResponse} protocol.
     *
     * @param message The incoming message, either {@link org.fourthline.cling.model.message.UpnpRequest} or
     *                {@link org.fourthline.cling.model.message.UpnpResponse}.
     * @return        The appropriate protocol that handles the messages or <code>null</code> if the message should be dropped.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException;

    /**
     * Creates a {@link org.fourthline.cling.protocol.sync.ReceivingRetrieval},
     * {@link org.fourthline.cling.protocol.sync.ReceivingAction},
     * {@link org.fourthline.cling.protocol.sync.ReceivingSubscribe},
     * {@link org.fourthline.cling.protocol.sync.ReceivingUnsubscribe}, or
     * {@link org.fourthline.cling.protocol.sync.ReceivingEvent} protocol.
     *
     * @param requestMessage The incoming message, examime {@link org.fourthline.cling.model.message.UpnpRequest.Method}
     *                       to determine the protocol.
     * @return        The appropriate protocol that handles the messages.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
    public ReceivingSync createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException;

    /**
     * Called by the {@link org.fourthline.cling.registry.Registry}, creates a protocol for announcing local devices.
     */
    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice);

    /**
     * Called by the {@link org.fourthline.cling.registry.Registry}, creates a protocol for announcing local devices.
     */
    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice);

    /**
     * Called by the {@link org.fourthline.cling.controlpoint.ControlPoint}, creates a protocol for a multicast search.
     */
    public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds);

    /**
     * Called by the {@link org.fourthline.cling.controlpoint.ControlPoint}, creates a protocol for executing an action.
     */
    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL controlURL);

    /**
     * Called by the {@link org.fourthline.cling.controlpoint.ControlPoint}, creates a protocol for GENA subscription.
     */
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) throws ProtocolCreationException;

    /**
     * Called by the {@link org.fourthline.cling.controlpoint.ControlPoint}, creates a protocol for GENA renewal.
     */
    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription);

    /**
     * Called by the {@link org.fourthline.cling.controlpoint.ControlPoint}, creates a protocol for GENA unsubscription.
     */
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription);

    /**
     * Called by the {@link org.fourthline.cling.model.gena.GENASubscription}, creates a protocol for sending GENA events.
     */
    public SendingEvent createSendingEvent(LocalGENASubscription subscription);
}
