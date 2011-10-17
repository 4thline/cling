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
