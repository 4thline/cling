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

import org.fourthline.cling.model.types.UDAServiceType;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class UDAServiceTypeHeader extends ServiceTypeHeader {

    public UDAServiceTypeHeader() {
    }

    public UDAServiceTypeHeader(URI uri) {
        super(uri);
    }

    public UDAServiceTypeHeader(UDAServiceType value) {
        super(value);
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(UDAServiceType.valueOf(s));
        } catch (Exception ex) {
            throw new InvalidHeaderException("Invalid UDA service type header value, " + ex.getMessage());
        }
    }

}
