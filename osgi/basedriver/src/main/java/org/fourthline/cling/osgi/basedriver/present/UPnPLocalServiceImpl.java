/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
