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

package org.fourthline.cling.osgi.basedriver.present;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.util.Map;
import java.util.Set;

/**
 * @author Bruce Green
 */
public class UPnPLocalServiceImpl<T> extends LocalService<T> {

    public UPnPLocalServiceImpl(ServiceType serviceType, ServiceId serviceId,
                                Action[] actions, StateVariable[] stateVariables)
            throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);
    }


    public UPnPLocalServiceImpl(ServiceType serviceType, ServiceId serviceId,
                                Map<Action, ActionExecutor> actionExecutors,
                                Map<StateVariable, StateVariableAccessor> stateVariableAccessors,
                                Set<Class> stringConvertibleTypes,
                                boolean supportsQueryStateVariables) throws ValidationException {
        super(serviceType, serviceId, actionExecutors, stateVariableAccessors,
              stringConvertibleTypes, supportsQueryStateVariables);
    }
}
