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

import org.fourthline.cling.model.types.SoapActionType;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class SoapActionHeader extends UpnpHeader<SoapActionType> {

    public SoapActionHeader() {
    }

    public SoapActionHeader(URI uri) {
        setValue(SoapActionType.valueOf(uri.toString()));
    }

    public SoapActionHeader(SoapActionType value) {
        setValue(value);
    }

    public SoapActionHeader(String s) throws InvalidHeaderException {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        try {
            if (!s.startsWith("\"") && s.endsWith("\"")) {
                throw new InvalidHeaderException("Invalid SOAP action header, must be enclosed in doublequotes:" + s);
            }

            SoapActionType t = SoapActionType.valueOf(s.substring(1, s.length()-1));
            setValue(t);
        } catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid SOAP action header value, " + ex.getMessage());
        }
    }

    public String getString() {
        return "\"" + getValue().toString() + "\"";
    }
}