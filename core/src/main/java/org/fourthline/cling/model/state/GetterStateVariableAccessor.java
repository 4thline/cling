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

import java.lang.reflect.Method;

/**
 * Reads the value of a state variable using reflection and a getter method.
 * 
 * @author Christian Bauer
 */
public class GetterStateVariableAccessor extends StateVariableAccessor {

    private Method getter;

    public GetterStateVariableAccessor(Method getter) {
        this.getter = getter;
    }

    public Method getGetter() {
        return getter;
    }

    @Override
    public Class<?> getReturnType() {
        return getGetter().getReturnType();
    }

    @Override
    public Object read(Object serviceImpl) throws Exception {
        return Reflections.invoke(getGetter(), serviceImpl);
    }

    @Override
    public String toString() {
        return super.toString() + " Method: " + getGetter();
    }

}
