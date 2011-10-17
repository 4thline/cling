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

/**
 * Although the UDA 1.0 spec doesn't say it, we assume that "int" is a 4 byte regular Java integer.
 *
 * @author Christian Bauer
 */
public class IntegerDatatype extends AbstractDatatype<Integer> {

    private int byteSize;

    public IntegerDatatype(int byteSize) {
        this.byteSize = byteSize;
    }

    @Override
    public boolean isHandlingJavaType(Class type) {
        return type == Integer.TYPE || Integer.class.isAssignableFrom(type);
    }

    public Integer valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
            Integer value = Integer.parseInt(s);
            if (!isValid(value)) {
                throw new InvalidValueException("Not a " + getByteSize() + " byte(s) integer: " + s)
                        ;
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidValueException("Can't convert string to number: " + s, ex);
        }
    }

    public boolean isValid(Integer value) {
        return value == null || (value >= getMinValue() && value <= getMaxValue());
    }

    public int getMinValue() {
        switch(getByteSize()) {
            case 1:
                return Byte.MIN_VALUE;
            case 2:
                return Short.MIN_VALUE;
            case 4:
                return Integer.MIN_VALUE;
        }
        throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
    }

    public int getMaxValue() {
        switch(getByteSize()) {
            case 1:
                return Byte.MAX_VALUE;
            case 2:
                return Short.MAX_VALUE;
            case 4:
                return Integer.MAX_VALUE;
        }
        throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
    }

    public int getByteSize() {
        return byteSize;
    }

}
