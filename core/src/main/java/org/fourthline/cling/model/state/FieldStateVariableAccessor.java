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

package org.fourthline.cling.model.state;

import org.seamless.util.Reflections;

import java.lang.reflect.Field;

/**
 * Reads the value of a state variable using reflection and a field.
 *
 * @author Christian Bauer
 */
public class FieldStateVariableAccessor extends StateVariableAccessor {

    protected Field field;

    public FieldStateVariableAccessor(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    @Override
    public Class<?> getReturnType() {
        return getField().getType();
    }

    @Override
    public Object read(Object serviceImpl) throws Exception {
        return Reflections.get(field, serviceImpl);
    }

    @Override
    public String toString() {
        return super.toString() + " Field: " + getField();
    }
}
