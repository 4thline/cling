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

import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

/**
 * @author Christian Bauer
 */
public class IncomingSubscribeResponseMessage extends StreamResponseMessage {

    public IncomingSubscribeResponseMessage(StreamResponseMessage source) {
        super(source);
    }

    /**
     * @return <code>true</code> if this message has an SID and TIMEOUT header value.
     */
    public boolean isValidHeaders() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class) != null &&
                getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class) != null;
    }

    public String getSubscriptionId() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue();
    }

    public int getSubscriptionDurationSeconds() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue();
    }
}
