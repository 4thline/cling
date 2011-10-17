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

package org.fourthline.cling.model.types;

import org.seamless.util.io.HexBin;

/**
 * @author Christian Bauer
 */
public class BinHexDatatype extends AbstractDatatype<byte[]> {

    public BinHexDatatype() {
    }

    public Class<byte[]> getValueType() {
        return byte[].class;
    }

    public byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
            return HexBin.stringToBytes(s);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getString(byte[] value) throws InvalidValueException {
        if (value == null) return "";
        try {
            return HexBin.bytesToString(value);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

}