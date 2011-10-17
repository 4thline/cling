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

import org.fourthline.cling.model.VariableValue;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.InvalidValueException;

/**
 * Represents the value of a state variable.
 *
 * @author Christian Bauer
 */
public class StateVariableValue<S extends Service> extends VariableValue {

    private StateVariable<S> stateVariable;

    public StateVariableValue(StateVariable<S> stateVariable, Object value) throws InvalidValueException {
        super(stateVariable.getTypeDetails().getDatatype(), value);
        this.stateVariable = stateVariable;
    }

    public StateVariable<S> getStateVariable() {
        return stateVariable;
    }

}