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
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class EventedValueUnsignedIntegerFourBytes extends EventedValue<UnsignedIntegerFourBytes> {

    public EventedValueUnsignedIntegerFourBytes(UnsignedIntegerFourBytes value) {
        super(value);
    }

    public EventedValueUnsignedIntegerFourBytes(Map.Entry<String, String>[] attributes) {
        super(attributes);
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI4.getDatatype();
    }
}
