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

import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;

/**
 * @author Christian Bauer
 */
public class OutgoingSearchResponseRootDevice extends OutgoingSearchResponse {

    public OutgoingSearchResponseRootDevice(IncomingDatagramMessage request,
                                            Location location,
                                            LocalDevice device) {
        super(request, location, device);

        getHeaders().add(UpnpHeader.Type.ST, new RootDeviceHeader());
        getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(device.getIdentity().getUdn()));
    }
}
