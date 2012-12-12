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
        if (s.equals("1") || s.toUpperCase(Locale.ENGLISH).equals("YES") || s.toUpperCase(Locale.ENGLISH).equals("TRUE")) {
            return true;
        } else if (s.equals("0") || s.toUpperCase(Locale.ENGLISH).equals("NO") || s.toUpperCase(Locale.ENGLISH).equals("FALSE")) {
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
