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

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDN;

/**
 * @author Christian Bauer
 */
public class USNRootDeviceHeader extends UpnpHeader<UDN> {

    public static final String ROOT_DEVICE_SUFFIX = "::upnp:rootdevice";

    public USNRootDeviceHeader() {
    }

    public USNRootDeviceHeader(UDN udn) {
        setValue(udn);
    }

    public void setString(String s) throws InvalidHeaderException {
        if (!s.startsWith(UDN.PREFIX) || !s.endsWith(ROOT_DEVICE_SUFFIX)) {
            throw new InvalidHeaderException(
                    "Invalid root device USN header value, must start with '" +
                            UDN.PREFIX + "' and end with '" +
                            ROOT_DEVICE_SUFFIX + "' but is '" + s + "'"
            );
        }
        UDN udn = new UDN(s.substring(UDN.PREFIX.length(), s.length() - ROOT_DEVICE_SUFFIX.length()));
        setValue(udn);
    }

    public String getString() {
        return getValue().toString() + ROOT_DEVICE_SUFFIX;
    }

}
