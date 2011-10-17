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

import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.model.types.InvalidValueException;

/**
 *
 * @author Christian Bauer
 * @author Mario Franco
 */
public class RangeHeader extends UpnpHeader<BytesRange> {

    public RangeHeader() {
    }

    public RangeHeader(BytesRange value) {
        setValue(value);
    }

    public RangeHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(BytesRange.valueOf(s));
        } catch (InvalidValueException invalidValueException) {
            throw new InvalidHeaderException("Invalid Range Header: " + invalidValueException.getMessage());
        }
    }

    public String getString() {
        return getValue().getString();
    }
}
