/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
            Integer value = Integer.parseInt(s.trim());
            if (!isValid(value)) {
                throw new InvalidValueException("Not a " + getByteSize() + " byte(s) integer: " + s)
                        ;
            }
            return value;
        } catch (NumberFormatException ex) {
            // TODO: UPNP VIOLATION: Some renderers (like PacketVideo TMM Player) send
            // RelCount and AbsCount as "NOT_IMPLEMENTED" in GetPositionInfoResponse action.
            // The spec says: If not implemented the value shall be Max Integer value.
        	if(s.equals("NOT_IMPLEMENTED")) {
        		return getMaxValue();
        	} else {
            	throw new InvalidValueException("Can't convert string to number: " + s, ex);
        	}
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
