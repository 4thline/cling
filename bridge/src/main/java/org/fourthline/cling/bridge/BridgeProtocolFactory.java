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

package org.fourthline.cling.bridge;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.link.proxy.ProxyLocalService;
import org.fourthline.cling.bridge.link.proxy.ProxyReceivingSubscribe;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.protocol.sync.ReceivingSubscribe;

import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class BridgeProtocolFactory extends ProtocolFactoryImpl {

    private static Logger log = Logger.getLogger(ReceivingSubscribe.class.getName());

    public BridgeProtocolFactory(UpnpService upnpService) {
        super(upnpService);
    }

    @Override
    public ReceivingSync createReceivingSync(StreamRequestMessage message) throws ProtocolCreationException {

        if (getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(message.getUri())) {

            ServiceEventSubscriptionResource resource =
                    getUpnpService().getRegistry().getResource(
                            ServiceEventSubscriptionResource.class,
                            message.getUri()
                    );

            if (resource == null || !(resource.getModel() instanceof ProxyLocalService))
                return super.createReceivingSync(message);

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {
                log.fine("Receiving SUBSCRIBE message on proxy: " + resource.getModel());
                return new ProxyReceivingSubscribe(getUpnpService(), message, (ProxyLocalService)resource.getModel());
            }
        }
        return super.createReceivingSync(message);
    }
}
