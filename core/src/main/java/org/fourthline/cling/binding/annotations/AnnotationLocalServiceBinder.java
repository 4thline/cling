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

package org.fourthline.cling.binding.annotations;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.action.QueryStateVariableExecutor;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.FieldStateVariableAccessor;
import org.fourthline.cling.model.state.GetterStateVariableAccessor;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.csv.CSV;
import org.seamless.util.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Reads {@link org.fourthline.cling.model.meta.LocalService} metadata from annotations.
 *
 * @author Christian Bauer
 */
public class AnnotationLocalServiceBinder implements LocalServiceBinder {

    private static Logger log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());

    public LocalService read(Class<?> clazz) throws LocalServiceBindingException {
        log.fine("Reading and binding annotations of service implementation class: " + clazz);

        // Read the service ID and service type from the annotation
        if (clazz.isAnnotationPresent(UpnpService.class)) {

            UpnpService annotation = clazz.getAnnotation(UpnpService.class);
            UpnpServiceId idAnnotation = annotation.serviceId();
            UpnpServiceType typeAnnotation = annotation.serviceType();

            ServiceId serviceId = idAnnotation.namespace().equals(UDAServiceId.DEFAULT_NAMESPACE)
                    ? new UDAServiceId(idAnnotation.value())
                    : new ServiceId(idAnnotation.namespace(), idAnnotation.value());

            ServiceType serviceType = typeAnnotation.namespace().equals(UDAServiceType.DEFAULT_NAMESPACE)
                    ? new UDAServiceType(typeAnnotation.value(), typeAnnotation.version())
                    : new ServiceType(typeAnnotation.namespace(), typeAnnotation.value(), typeAnnotation.version());

            boolean supportsQueryStateVariables = annotation.supportsQueryStateVariables();

            Set<Class> stringConvertibleTypes = readStringConvertibleTypes(annotation.stringConvertibleTypes());

            return read(clazz, serviceId, serviceType, supportsQueryStateVariables, stringConvertibleTypes);
        } else {
            throw new LocalServiceBindingException("Given class is not an @UpnpService");
        }
    }

    public LocalService read(Class<?> clazz, ServiceId id, ServiceType type,
                             boolean supportsQueryStateVariables, Class[] stringConvertibleTypes) throws LocalServiceBindingException {
        return read(clazz, id, type, supportsQueryStateVariables, new HashSet<>(Arrays.asList(stringConvertibleTypes)));
    }

    public LocalService read(Class<?> clazz, ServiceId id, ServiceType type,
                                   boolean supportsQueryStateVariables, Set<Class> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<StateVariable, StateVariableAccessor> stateVariables = readStateVariables(clazz, stringConvertibleTypes);
        Map<Action, ActionExecutor> actions = readActions(clazz, stateVariables, stringConvertibleTypes);

        // Special treatment of the state variable querying action
        if (supportsQueryStateVariables) {
            actions.put(new QueryStateVariableAction(), new QueryStateVariableExecutor());
        }

        try {
            return new LocalService(type, id, actions, stateVariables, stringConvertibleTypes, supportsQueryStateVariables);

        } catch (ValidationException ex) {
            log.severe("Could not validate device model: " + ex.toString());
            for (ValidationError validationError : ex.getErrors()) {
                log.severe(validationError.toString());
            }
            throw new LocalServiceBindingException("Validation of model failed, check the log");
        }
    }

    protected Set<Class> readStringConvertibleTypes(Class[] declaredTypes) throws LocalServiceBindingException {

        for (Class stringConvertibleType : declaredTypes) {
            if (!Modifier.isPublic(stringConvertibleType.getModifiers())) {
                throw new LocalServiceBindingException(
                        "Declared string-convertible type must be public: " + stringConvertibleType
                );
            }
            try {
                stringConvertibleType.getConstructor(String.class);
            } catch (NoSuchMethodException ex) {
                throw new LocalServiceBindingException(
                        "Declared string-convertible type needs a public single-argument String constructor: " + stringConvertibleType
                );
            }
        }
        Set<Class> stringConvertibleTypes = new HashSet(Arrays.asList(declaredTypes));

        // Some defaults
        stringConvertibleTypes.add(URI.class);
        stringConvertibleTypes.add(URL.class);
        stringConvertibleTypes.add(CSV.class);

        return stringConvertibleTypes;
    }

    protected Map<StateVariable, StateVariableAccessor> readStateVariables(Class<?> clazz, Set<Class> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<StateVariable, StateVariableAccessor> map = new HashMap<>();

        // State variables declared on the class
        if (clazz.isAnnotationPresent(UpnpStateVariables.class)) {
            UpnpStateVariables variables = clazz.getAnnotation(UpnpStateVariables.class);
            for (UpnpStateVariable v : variables.value()) {

                if (v.name().length() == 0)
                    throw new LocalServiceBindingException("Class-level @UpnpStateVariable name attribute value required");

                String javaPropertyName = toJavaStateVariableName(v.name());

                Method getter = Reflections.getGetterMethod(clazz, javaPropertyName);
                Field field = Reflections.getField(clazz, javaPropertyName);

                StateVariableAccessor accessor = null;
                if (getter != null && field != null) {
                    accessor = variables.preferFields() ?
                            new FieldStateVariableAccessor(field)
                            : new GetterStateVariableAccessor(getter);
                } else if (field != null) {
                    accessor = new FieldStateVariableAccessor(field);
                } else if (getter != null) {
                    accessor = new GetterStateVariableAccessor(getter);
                } else {
                    log.finer("No field or getter found for state variable, skipping accessor: " + v.name());
                }

                StateVariable stateVar =
                        new AnnotationStateVariableBinder(v, v.name(), accessor, stringConvertibleTypes)
                                .createStateVariable();

                map.put(stateVar, accessor);
            }
        }

        // State variables declared on fields
        for (Field field : Reflections.getFields(clazz, UpnpStateVariable.class)) {

            UpnpStateVariable svAnnotation = field.getAnnotation(UpnpStateVariable.class);

            StateVariableAccessor accessor = new FieldStateVariableAccessor(field);

            StateVariable stateVar = new AnnotationStateVariableBinder(
                    svAnnotation,
                    svAnnotation.name().length() == 0
                            ? toUpnpStateVariableName(field.getName())
                            : svAnnotation.name(),
                    accessor,
                    stringConvertibleTypes
            ).createStateVariable();

            map.put(stateVar, accessor);
        }

        // State variables declared on getters
        for (Method getter : Reflections.getMethods(clazz, UpnpStateVariable.class)) {

            String propertyName = Reflections.getMethodPropertyName(getter.getName());
            if (propertyName == null) {
                throw new LocalServiceBindingException(
                        "Annotated method is not a getter method (: " + getter
                );
            }

            if (getter.getParameterTypes().length > 0)
                throw new LocalServiceBindingException(
                        "Getter method defined as @UpnpStateVariable can not have parameters: " + getter
                );

            UpnpStateVariable svAnnotation = getter.getAnnotation(UpnpStateVariable.class);

            StateVariableAccessor accessor = new GetterStateVariableAccessor(getter);

            StateVariable stateVar = new AnnotationStateVariableBinder(
                    svAnnotation,
                    svAnnotation.name().length() == 0
                            ?
                            toUpnpStateVariableName(propertyName)
                            : svAnnotation.name(),
                    accessor,
                    stringConvertibleTypes
            ).createStateVariable();

            map.put(stateVar, accessor);
        }

        return map;
    }

    protected Map<Action, ActionExecutor> readActions(Class<?> clazz,
                                                      Map<StateVariable, StateVariableAccessor> stateVariables,
                                                      Set<Class> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<Action, ActionExecutor> map = new HashMap<>();

        for (Method method : Reflections.getMethods(clazz, UpnpAction.class)) {
            AnnotationActionBinder actionBinder =
                    new AnnotationActionBinder(method, stateVariables, stringConvertibleTypes);
            Action action = actionBinder.appendAction(map);
            if(isActionExcluded(action)) {
            	map.remove(action);
            }
        }

        return map;
    }

    /**
     * Override this method to exclude action/methods after they have been discovered.
     */
    protected  boolean isActionExcluded(Action action) {
    	return false;
    }
    
    // TODO: I don't like the exceptions much, user has no idea what to do

    static String toUpnpStateVariableName(String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }

    static String toJavaStateVariableName(String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }


    static String toUpnpActionName(String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Action name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }

    static String toJavaActionName(String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }

}
