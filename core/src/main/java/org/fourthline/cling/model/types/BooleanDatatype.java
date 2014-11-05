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

import java.util.Locale;

/**
 * @author Christian Bauer
 */
public class BooleanDatatype extends AbstractDatatype<Boolean> {

    public BooleanDatatype() {
    }

    @Override
    public boolean isHandlingJavaType(Class type) {
        return type == Boolean.TYPE || Boolean.class.isAssignableFrom(type);
    }

    public Boolean valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        if (s.equals("1") || s.toUpperCase(Locale.ROOT).equals("YES") || s.toUpperCase(Locale.ROOT).equals("TRUE")) {
            return true;
        } else if (s.equals("0") || s.toUpperCase(Locale.ROOT).equals("NO") || s.toUpperCase(Locale.ROOT).equals("FALSE")) {
            return false;
        } else {
            throw new InvalidValueException("Invalid boolean value string: " + s);
        }
    }

    public String getString(Boolean value) throws InvalidValueException {
        if (value == null) return "";
        return value ? "1" : "0";
    }

}
