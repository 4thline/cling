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

import java.util.logging.Logger;

/**
 * A crude solution for unsigned "non-negative" types in UPnP, not usable for any arithmetic.
 *
 * @author Christian Bauer
 */
public abstract class UnsignedVariableInteger {

    final private static Logger log = Logger.getLogger(UnsignedVariableInteger.class.getName());

    public enum Bits {
        EIGHT(0xffL),
        SIXTEEN(0xffffL),
        TWENTYFOUR(0xffffffL),
        THIRTYTWO(0xffffffffL);

        private long maxValue;

        Bits(long maxValue) {
            this.maxValue = maxValue;
        }

        public long getMaxValue() {
            return maxValue;
        }
    }

    protected long value;

    protected UnsignedVariableInteger() {
    }

    public UnsignedVariableInteger(long value) throws NumberFormatException {
        setValue(value);
    }

    public UnsignedVariableInteger(String s) throws NumberFormatException {
        if (s.startsWith("-")) {
            // Don't throw exception, just cut it!
            // TODO: UPNP VIOLATION: Twonky Player returns "-1" as the track number
            log.warning("Invalid negative integer value '" + s + "', assuming value 0!");
            s = "0";
        }
        setValue(Long.parseLong(s.trim()));
    }

    protected UnsignedVariableInteger setValue(long value) {
        isInRange(value);
        this.value = value;
        return this;
    }

    public Long getValue() {
        return value;
    }

    public void isInRange(long value) throws NumberFormatException {
        if (value < getMinValue() || value > getBits().getMaxValue()) {
            throw new NumberFormatException("Value must be between " + getMinValue() + " and " + getBits().getMaxValue() + ": " + value);
        }
    }

    public int getMinValue() {
        return 0;
    }

    public abstract Bits getBits();

    public UnsignedVariableInteger increment(boolean rolloverToOne) {
        if (value + 1 > getBits().getMaxValue()) {
            value = rolloverToOne ? 1 : 0;
        } else {
            value++;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsignedVariableInteger that = (UnsignedVariableInteger) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
