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

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.MANHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.ModelUtil;

/**
 * @author Christian Bauer
 */
public class OutgoingSearchRequest extends OutgoingDatagramMessage<UpnpRequest> {

    private UpnpHeader searchTarget;

    public OutgoingSearchRequest(UpnpHeader searchTarget, int mxSeconds) {
        super(
                new UpnpRequest(UpnpRequest.Method.MSEARCH),
                ModelUtil.getInetAddressByName(Constants.IPV4_UPNP_MULTICAST_GROUP),
                Constants.UPNP_MULTICAST_PORT
        );

        this.searchTarget = searchTarget;

        getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        getHeaders().add(UpnpHeader.Type.MX, new MXHeader(mxSeconds));
        getHeaders().add(UpnpHeader.Type.ST, searchTarget);
        getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
    }

    public UpnpHeader getSearchTarget() {
        return searchTarget;
    }
}