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
public class UDNHeader extends UpnpHeader<UDN> {

    public UDNHeader() {
    }

    public UDNHeader(UDN udn) {
        setValue(udn);
    }

    public void setString(String s) throws InvalidHeaderException {
        if (!s.startsWith(UDN.PREFIX)) {
            throw new InvalidHeaderException("Invalid UDA header value, must start with '"+UDN.PREFIX+"': " + s);
        }

        if (s.contains("::urn")) {
            throw new InvalidHeaderException("Invalid UDA header value, must not contain '::urn': " + s);
        }

        UDN udn = new UDN( s.substring(UDN.PREFIX.length()) );
        setValue(udn);
    }

    public String getString() {
        return getValue().toString();
    }
}

