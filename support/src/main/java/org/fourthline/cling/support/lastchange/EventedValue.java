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

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.shared.AbstractMap;

import java.util.Map;

public abstract class EventedValue<V> {

    final protected V value;

    public EventedValue(V value) {
        this.value = value;
    }

    public EventedValue(Map.Entry<String,String>[] attributes) {
        try {
            this.value = valueOf(attributes);
        } catch (InvalidValueException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public V getValue() {
        return value;
    }

    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[] {
            new AbstractMap.SimpleEntry<String, String>("val", toString())
        };
    }

    protected V valueOf(Map.Entry<String,String>[] attributes) throws InvalidValueException {
        V v = null;
        for (Map.Entry<String, String> attribute : attributes) {
            if (attribute.getKey().equals("val")) v = valueOf(attribute.getValue());
        }
        return v;
    }

    protected V valueOf(String s) throws InvalidValueException {
        return (V)getDatatype().valueOf(s);
    }

    @Override
    public String toString() {
        return getDatatype().getString(getValue());
    }

    abstract protected Datatype getDatatype();
}
