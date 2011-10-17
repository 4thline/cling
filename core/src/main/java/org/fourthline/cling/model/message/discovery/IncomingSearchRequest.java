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

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.MANHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;

/**
 * @author Christian Bauer
 */
public class IncomingSearchRequest extends IncomingDatagramMessage<UpnpRequest> {

    public IncomingSearchRequest(IncomingDatagramMessage<UpnpRequest> source) {
        super(source);
    }

    public UpnpHeader getSearchTarget() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.ST);
    }

    public Integer getMX() {
        MXHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MX, MXHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    /**
     * @return <code>true</code> if this message has a MAN with
     *         value {@link org.fourthline.cling.model.types.NotificationSubtype#DISCOVER}.
     */
    public boolean isMANSSDPDiscover() {
        MANHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MAN, MANHeader.class);
        return header != null && header.getValue().equals(NotificationSubtype.DISCOVER.getHeaderString());
    }

}
