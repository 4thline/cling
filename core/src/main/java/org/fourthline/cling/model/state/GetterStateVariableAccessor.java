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
