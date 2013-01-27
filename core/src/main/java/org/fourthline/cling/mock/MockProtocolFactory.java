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
package org.fourthline.cling.mock;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.protocol.sync.SendingAction;
import org.fourthline.cling.protocol.sync.SendingEvent;
import org.fourthline.cling.protocol.sync.SendingRenewal;
import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.protocol.sync.SendingUnsubscribe;

import javax.enterprise.inject.Alternative;
import java.net.URL;

/**
 * @author Christian Bauer
 */
@Alternative
public class MockProtocolFactory implements ProtocolFactory {

    @Override
    public UpnpService getUpnpService() {
        return null;
    }

    @Override
    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage message) throws ProtocolCreationException {
        return null;
    }

    @Override
    public ReceivingSync createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException {
        return null;
    }

    @Override
    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
        return null;
    }

    @Override
    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice) {
        return null;
    }

    @Override
    public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
        return null;
    }

    @Override
    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL controlURL) {
        return null;
    }

    @Override
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingEvent createSendingEvent(LocalGENASubscription subscription) {
        return null;
    }
}
