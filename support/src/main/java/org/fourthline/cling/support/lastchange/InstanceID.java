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

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class InstanceID {

    protected UnsignedIntegerEightBytes id;
    protected List<EventedValue> values = new ArrayList<>();

    public InstanceID(UnsignedIntegerEightBytes id) {
        this(id, new ArrayList<EventedValue>());
    }

    public InstanceID(UnsignedIntegerEightBytes id, List<EventedValue> values) {
        this.id = id;
        this.values = values;
    }

    public UnsignedIntegerEightBytes getId() {
        return id;
    }

    public List<EventedValue> getValues() {
        return values;
    }
}
