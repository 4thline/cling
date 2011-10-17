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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO: UDA 1.1 says it should be RfC 3986 compatible.
 *
 * <p>See http://blog.jclark.com/2008/11/what-allowed-in-uri.html</p>
 *
 * @author Christian Bauer
 */
public class LocationHeader extends UpnpHeader<URL> {

    public LocationHeader() {
    }

    public LocationHeader(URL value) {
        setValue(value);
    }

    public LocationHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        try {
            URL url = new URL(s);
            setValue(url);
        } catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Invalid URI: " + ex.getMessage());
        }
    }

    public String getString() {
        return getValue().toString();
    }
}
