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
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.types.NotificationSubtype;

/**
 * @author Christian Bauer
 */
public abstract class OutgoingNotificationRequest extends OutgoingDatagramMessage<UpnpRequest> {

    private NotificationSubtype type;

    protected OutgoingNotificationRequest(Location location, LocalDevice device, NotificationSubtype type) {
        super(
                new UpnpRequest(UpnpRequest.Method.NOTIFY),
                ModelUtil.getInetAddressByName(Constants.IPV4_UPNP_MULTICAST_GROUP),
                Constants.UPNP_MULTICAST_PORT
        );

        this.type = type;

        getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(device.getIdentity().getMaxAgeSeconds()));
        getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(location.getURL()));

        getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(type));
    }

    public NotificationSubtype getType() {
        return type;
    }

}
