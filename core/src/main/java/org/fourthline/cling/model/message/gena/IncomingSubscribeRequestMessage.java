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

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.meta.LocalService;

import java.net.URL;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class IncomingSubscribeRequestMessage extends StreamRequestMessage {

    final private LocalService service;

    public IncomingSubscribeRequestMessage(StreamRequestMessage source, LocalService  service) {
        super(source);
        this.service = service;
    }

    public LocalService getService() {
        return service;
    }

    public List<URL> getCallbackURLs() {
        CallbackHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class);
        return header != null ? header.getValue() : null;
    }

    public boolean hasNotificationHeader() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.NT, NTEventHeader.class) != null;
    }

    public Integer getRequestedTimeoutSeconds() {
        TimeoutHeader timeoutHeader = getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class);
        return timeoutHeader != null ? timeoutHeader.getValue() : null;
    }

    public String getSubscriptionId() {
        SubscriptionIdHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class);
        return header != null ? header.getValue() : null;
    }
}
