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

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.ModelUtil;
import org.seamless.util.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a state variable value from/to strings of comma-separated elements.
 * <p>
 * A concrete implementation of this interface knows how to transform values of the
 * declared type into a string of comma-separated list of elements, and how to read
 * such strings back into individual values.
 * </p>
 * <p>
 * Your action method returns a <code>CSV<...></code> instance as an output argument. It can
 * also accept a concrecte subclass of this type as an input argument, e.g. <code>CSVString</code>.
 * This type extends a regular <code>List</code>, so within your action method you can
 * handle the elements as usual.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class CSV<T> extends ArrayList<T> {

    protected final Datatype.Builtin datatype;

    public CSV() {
        datatype = getBuiltinDatatype();
    }

    public CSV(String s) throws InvalidValueException {
        datatype = getBuiltinDatatype();
        addAll(parseString(s));
    }

    protected List parseString(String s) throws InvalidValueException {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        List values = new ArrayList();
        for (String string : strings) {
            values.add(datatype.getDatatype().valueOf(string));
        }
        return values;
    }

    protected Datatype.Builtin getBuiltinDatatype() throws InvalidValueException {
        Class csvType = Reflections.getTypeArguments(ArrayList.class, getClass()).get(0);
        Datatype.Default defaultType = Datatype.Default.getByJavaType(csvType);
        if (defaultType == null) {
            throw new InvalidValueException("No built-in UPnP datatype for Java type of CSV: " + csvType);
        }
        return defaultType.getBuiltinType();
    }

    @Override
    public String toString() {
        List<String> stringValues = new ArrayList();
        for (T t : this) {
            stringValues.add(datatype.getDatatype().getString(t));
        }
        return ModelUtil.toCommaSeparatedList(stringValues.toArray(new Object[stringValues.size()]));
    }
}
