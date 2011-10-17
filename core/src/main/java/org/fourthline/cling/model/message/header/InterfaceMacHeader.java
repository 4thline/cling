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

import org.seamless.util.io.HexBin;

/**
 * Custom header for Cling, used to transfer the MAC ethernet address for Wake-on-LAN.
 *
 * @author Christian Bauer
 */
public class InterfaceMacHeader extends UpnpHeader<byte[]> {

    public InterfaceMacHeader() {
    }

    public InterfaceMacHeader(byte[] value) {
        setValue(value);
    }

    public InterfaceMacHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        byte[] bytes = HexBin.stringToBytes(s, ":");
        setValue(bytes);
        if (bytes.length != 6) {
            throw new InvalidHeaderException("Invalid MAC address: " + s);
        }
    }

    public String getString() {
        return HexBin.bytesToString(getValue(), ":");
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getString() + "'";
    }
}