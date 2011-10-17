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

import java.lang.reflect.ParameterizedType;

/**
 * @author Christian Bauer
 */
public abstract class AbstractDatatype<V> implements Datatype<V> {

    private Builtin builtin;

    protected Class<V> getValueType() {
        return (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public boolean isHandlingJavaType(Class type) {
        return getValueType().isAssignableFrom(type);
    }

    @Override
    public V valueOf(String s) throws InvalidValueException {
        return null;
    }

    public Builtin getBuiltin() {
        return builtin;
    }

    public void setBuiltin(Builtin builtin) {
        this.builtin = builtin;
    }

    public String getString(V value) throws InvalidValueException {
        if (value == null) return "";
        if (!isValid(value)) {
            throw new InvalidValueException("Value is not valid: " + value);
        }
        return value.toString();
    }

    public boolean isValid(V value) {
        return value == null || getValueType().isAssignableFrom(value.getClass());
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

    public String getDisplayString() {
        if (this instanceof CustomDatatype) {
            return ((CustomDatatype)this).getName();
        } else if (getBuiltin() != null) {
            return getBuiltin().getDescriptorName();
        } else {
            return getValueType().getSimpleName();
        }
    }

}
