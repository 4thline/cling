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

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Integrity rule for a state variable, restricting its values to a range with steps.
 * <p/>
 * TODO: The question here is: Are they crazy enough to use this for !integer (e.g. floating point) numbers?
 *
 * @author Christian Bauer
 */
public class StateVariableAllowedValueRange implements Validatable {

    final private static Logger log = Logger.getLogger(StateVariableAllowedValueRange.class.getName());

    final private long minimum;
    final private long maximum;
    final private long step;

    public StateVariableAllowedValueRange(long minimum, long maximum) {
        this(minimum, maximum, 1);
    }

    public StateVariableAllowedValueRange(long minimum, long maximum, long step) {
        if (minimum > maximum) {
            log.warning("UPnP specification violation, allowed value range minimum '" + minimum
                                + "' is greater than maximum '" + maximum + "', switching values.");
            this.minimum = maximum;
            this.maximum = minimum;
        } else {
            this.minimum = minimum;
            this.maximum = maximum;
        }
        this.step = step;
    }

    public long getMinimum() {
        return minimum;
    }

    public long getMaximum() {
        return maximum;
    }

    public long getStep() {
        return step;
    }

    public boolean isInRange(long value) {
        return value >= getMinimum() && value <= getMaximum() && (value % step) == 0;
    }

    public List<ValidationError> validate() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Range Min: " + getMinimum() + " Max: " + getMaximum() + " Step: " + getStep();
    }
}