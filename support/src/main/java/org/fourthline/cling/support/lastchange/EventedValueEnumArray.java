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

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public abstract class EventedValueEnumArray<E extends Enum> extends EventedValue<E[]> {

    public EventedValueEnumArray(E[] e) {
        super(e);
    }

    public EventedValueEnumArray(Map.Entry<String, String>[] attributes) {
        super(attributes);
    }

    @Override
    protected E[] valueOf(String s) throws InvalidValueException {
        return enumValueOf(ModelUtil.fromCommaSeparatedList(s));
    }

    protected abstract E[] enumValueOf(String[] names);

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(getValue());
    }

    @Override
    protected Datatype getDatatype() {
        return null;
    }
}
