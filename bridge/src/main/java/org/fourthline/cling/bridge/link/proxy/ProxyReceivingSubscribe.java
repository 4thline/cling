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

package org.fourthline.cling.bridge.link.proxy;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.OutgoingSubscribeResponseMessage;
import org.fourthline.cling.protocol.sync.ReceivingSubscribe;

import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class ProxyReceivingSubscribe extends ReceivingSubscribe {

    private static Logger log = Logger.getLogger(ProxyReceivingSubscribe.class.getName());

    private final ProxyLocalService proxyService;

    public ProxyReceivingSubscribe(UpnpService upnpService, StreamRequestMessage inputMessage, ProxyLocalService proxyService) {
        super(upnpService, inputMessage);

        this.proxyService = proxyService;
    }

    public ProxyLocalService getProxyService() {
        return proxyService;
    }

    @Override
    protected OutgoingSubscribeResponseMessage executeSync() {
        // TODO
        log.warning("Subscription request on proxy service, not implemented!");
        return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.NOT_IMPLEMENTED);
    }
}
