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

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The metadata of a service created on this host, by application code.
 * <p>
 * After instantiation {@link #setManager(org.fourthline.cling.model.ServiceManager)} must
 * be called to bind the service metadata to the service implementation.
 * </p>
 *
 * @author Christian Bauer
 */
public class LocalService<T> extends Service<LocalDevice, LocalService> {

    final protected Map<Action, ActionExecutor> actionExecutors;
    final protected Map<StateVariable, StateVariableAccessor> stateVariableAccessors;
    final protected Set<Class> stringConvertibleTypes;
    final protected boolean supportsQueryStateVariables;

    protected ServiceManager manager;

    public LocalService(ServiceType serviceType, ServiceId serviceId,
                        Action[] actions, StateVariable[] stateVariables) throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);
        this.manager = null;
        this.actionExecutors = new HashMap();
        this.stateVariableAccessors = new HashMap();
        this.stringConvertibleTypes = new HashSet();
        this.supportsQueryStateVariables = true;
    }

    public LocalService(ServiceType serviceType, ServiceId serviceId,
                        Map<Action, ActionExecutor> actionExecutors,
                        Map<StateVariable, StateVariableAccessor> stateVariableAccessors,
                        Set<Class> stringConvertibleTypes,
                        boolean supportsQueryStateVariables) throws ValidationException {

        super(serviceType, serviceId,
                actionExecutors.keySet().toArray(new Action[actionExecutors.size()]),
                stateVariableAccessors.keySet().toArray(new StateVariable[stateVariableAccessors.size()])
        );

        this.supportsQueryStateVariables = supportsQueryStateVariables;
        this.stringConvertibleTypes = stringConvertibleTypes;
        this.stateVariableAccessors = stateVariableAccessors;
        this.actionExecutors = actionExecutors;
    }

    synchronized public void setManager(ServiceManager<T> manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager is final");
        }
        this.manager = manager;
    }

    synchronized public ServiceManager<T> getManager() {
        if (manager == null) {
            throw new IllegalStateException("Unmanaged service, no implementation instance available");
        }
        return manager;
    }

    public boolean isSupportsQueryStateVariables() {
        return supportsQueryStateVariables;
    }

    public Set<Class> getStringConvertibleTypes() {
        return stringConvertibleTypes;
    }

    public boolean isStringConvertibleType(Object o) {
        return o != null && isStringConvertibleType(o.getClass());
    }

    public boolean isStringConvertibleType(Class clazz) {
        return ModelUtil.isStringConvertibleType(getStringConvertibleTypes(), clazz);
    }

    public StateVariableAccessor getAccessor(String stateVariableName) {
        StateVariable sv;
        return (sv = getStateVariable(stateVariableName)) != null ? getAccessor(sv) : null;
    }

    public StateVariableAccessor getAccessor(StateVariable stateVariable) {
        return stateVariableAccessors.get(stateVariable);
    }

    public ActionExecutor getExecutor(String actionName) {
        Action action;
        return (action = getAction(actionName)) != null ? getExecutor(action) : null;
    }

    public ActionExecutor getExecutor(Action action) {
        return actionExecutors.get(action);
    }

    @Override
    public Action getQueryStateVariableAction() {
        return getAction(QueryStateVariableAction.ACTION_NAME);
    }

    @Override
    public String toString() {
        return super.toString()  + ", Manager: " + manager;
    }
}