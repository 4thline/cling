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
