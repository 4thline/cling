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

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

/**
 * The metadata of a service, with actions and state variables.
 *
 * @author Christian Bauer
 */
public abstract class Service<D extends Device, S extends Service> {

	final private static Logger log = Logger.getLogger(Service.class.getName());

    final private ServiceType serviceType;
    final private ServiceId serviceId;


    final private Map<String, Action> actions = new HashMap<>();
    final private Map<String, StateVariable> stateVariables = new HashMap<>();

    // Package mutable state
    private D device;

    public Service(ServiceType serviceType, ServiceId serviceId) throws ValidationException {
        this(serviceType, serviceId, null, null);
    }

    public Service(ServiceType serviceType, ServiceId serviceId,
                   Action<S>[] actions, StateVariable<S>[] stateVariables) throws ValidationException {

        this.serviceType = serviceType;
        this.serviceId = serviceId;

        if (actions != null) {
            for (Action action : actions) {
                this.actions.put(action.getName(), action);
                action.setService(this);
            }
        }

        if (stateVariables != null) {
            for (StateVariable stateVariable : stateVariables) {
                this.stateVariables.put(stateVariable.getName(), stateVariable);
                stateVariable.setService(this);
            }
        }

    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public boolean hasActions() {
        return getActions() != null && getActions().length > 0;
    }

    public Action<S>[] getActions() {
        return actions == null ? null : actions.values().toArray(new Action[actions.values().size()]);
    }

    public boolean hasStateVariables() {
        // TODO: Spec says always has to have at least one...
        return getStateVariables() != null && getStateVariables().length > 0;
    }

    public StateVariable<S>[] getStateVariables() {
        return stateVariables == null ? null : stateVariables.values().toArray(new StateVariable[stateVariables.values().size()]);
    }

    public D getDevice() {
        return device;
    }

    void setDevice(D device) {
        if (this.device != null)
            throw new IllegalStateException("Final value has been set already, model is immutable");
        this.device = device;
    }

    public Action<S> getAction(String name) {
        return actions == null ? null : actions.get(name);
    }

    public StateVariable<S> getStateVariable(String name) {
        // Some magic necessary for the deprecated 'query state variable' action stuff
        if (QueryStateVariableAction.VIRTUAL_STATEVARIABLE_INPUT.equals(name)) {
            return new StateVariable(
                    QueryStateVariableAction.VIRTUAL_STATEVARIABLE_INPUT,
                    new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype())
            );
        }
        if (QueryStateVariableAction.VIRTUAL_STATEVARIABLE_OUTPUT.equals(name)) {
            return new StateVariable(
                    QueryStateVariableAction.VIRTUAL_STATEVARIABLE_OUTPUT,
                    new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype())
            );
        }
        return stateVariables == null ? null : stateVariables.get(name);
    }

    public StateVariable<S> getRelatedStateVariable(ActionArgument argument) {
        return getStateVariable(argument.getRelatedStateVariableName());
    }

    public Datatype<S> getDatatype(ActionArgument argument) {
        return getRelatedStateVariable(argument).getTypeDetails().getDatatype();
    }

    public ServiceReference getReference() {
        return new ServiceReference(getDevice().getIdentity().getUdn(), getServiceId());
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getServiceType() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "serviceType",
                    "Service type/info is required"
            ));
        }

        if (getServiceId() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "serviceId",
                    "Service ID is required"
            ));
        }

        // TODO: If the service has no evented variables, it should not have an event subscription URL, which means
        // the url element in the device descriptor must be present, but empty!!!!

        /* TODO: This doesn't fit into our meta model, we don't know if a service has state variables until
         we completely hydrate it from a service descriptor
        if (getStateVariables().length == 0) {
            errors.add(new ValidationError(
                    getClass(),
                    "stateVariables",
                    "Service must have at least one state variable"
            ));
        }
        */
        if (hasStateVariables()) {
            for (StateVariable stateVariable : getStateVariables()) {
                errors.addAll(stateVariable.validate());
            }
        }

        if (hasActions()) {
            for (Action action : getActions()) {

                // Instead of bailing out here, we try to continue if an action is invalid
                // errors.addAll(action.validate());

                List<ValidationError> actionErrors = action.validate();
            	if(actionErrors.size() > 0) {
                    actions.remove(action.getName()); // Remove it
                    log.warning("Discarding invalid action of service '" + getServiceId() + "': " + action.getName());
                    for (ValidationError actionError : actionErrors) {
                        log.warning("Invalid action '" + action.getName() + "': " + actionError);
                    }
            	}
            }
        }

        return errors;
    }

    public abstract Action getQueryStateVariableAction();

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") ServiceId: " + getServiceId();
    }
}