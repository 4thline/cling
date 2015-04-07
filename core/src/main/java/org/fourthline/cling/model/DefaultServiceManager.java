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

package org.fourthline.cling.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.state.StateVariableValue;
import org.seamless.util.Exceptions;
import org.seamless.util.Reflections;

/**
 * Default implementation, creates and manages a single instance of a plain Java bean.
 * <p>
 * Creates instance of the defined service class when it is first needed (acts as a factory),
 * manages the instance in a field (it's shared), and synchronizes (locks) all
 * multi-threaded access. A locking attempt will timeout after 500 milliseconds with
 * a runtime exception if another operation is already in progress. Override
 * {@link #getLockTimeoutMillis()} to customize this behavior, e.g. if your service
 * bean is slow and requires more time for typical action executions or state
 * variable reading.
 * </p>
 *
 * @author Christian Bauer
 */
public class DefaultServiceManager<T> implements ServiceManager<T> {

    private static Logger log = Logger.getLogger(DefaultServiceManager.class.getName());

    final protected LocalService<T> service;
    final protected Class<T> serviceClass;
    final protected ReentrantLock lock = new ReentrantLock(true);

    // Locking!
    protected T serviceImpl;
    protected PropertyChangeSupport propertyChangeSupport;

    protected DefaultServiceManager(LocalService<T> service) {
        this(service, null);
    }

    public DefaultServiceManager(LocalService<T> service, Class<T> serviceClass) {
        this.service = service;
        this.serviceClass = serviceClass;
    }

    // The monitor entry and exit methods

    protected void lock() {
        try {
            if (lock.tryLock(getLockTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                if (log.isLoggable(Level.FINEST))
                    log.finest("Acquired lock");
            } else {
                throw new RuntimeException("Failed to acquire lock in milliseconds: " + getLockTimeoutMillis());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock:" + e);
        }
    }

    protected void unlock() {
        if (log.isLoggable(Level.FINEST))
            log.finest("Releasing lock");
        lock.unlock();
    }

    protected int getLockTimeoutMillis() {
        return 500;
    }

    public LocalService<T> getService() {
        return service;
    }

    public T getImplementation() {
        lock();
        try {
            if (serviceImpl == null) {
                init();
            }
            return serviceImpl;
        } finally {
            unlock();
        }
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        lock();
        try {
            if (propertyChangeSupport == null) {
                init();
            }
            return propertyChangeSupport;
        } finally {
            unlock();
        }
    }

    public void execute(Command<T> cmd) throws Exception {
        lock();
        try {
            cmd.execute(this);
        } finally {
            unlock();
        }
    }

    @Override
    public Collection<StateVariableValue> getCurrentState() throws Exception {
        lock();
        try {
            Collection<StateVariableValue> values = readInitialEventedStateVariableValues();
            if (values != null) {
                log.fine("Obtained initial state variable values for event, skipping individual state variable accessors");
                return values;
            }
            values = new ArrayList<>();
            for (StateVariable stateVariable : getService().getStateVariables()) {
                if (stateVariable.getEventDetails().isSendEvents()) {
                    StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                    if (accessor == null)
                        throw new IllegalStateException("No accessor for evented state variable");
                    values.add(accessor.read(stateVariable, getImplementation()));
                }
            }
            return values;
        } finally {
            unlock();
        }
    }

    protected Collection<StateVariableValue> getCurrentState(String[] variableNames) throws Exception {
        lock();
        try {
            Collection<StateVariableValue> values = new ArrayList<>();
            for (String variableName : variableNames) {
                variableName = variableName.trim();

                StateVariable stateVariable = getService().getStateVariable(variableName);
                if (stateVariable == null || !stateVariable.getEventDetails().isSendEvents()) {
                    log.fine("Ignoring unknown or non-evented state variable: " + variableName);
                    continue;
                }

                StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                if (accessor == null) {
                    log.warning("Ignoring evented state variable without accessor: " + variableName);
                    continue;
                }
                values.add(accessor.read(stateVariable, getImplementation()));
            }
            return values;
        } finally {
            unlock();
        }
    }

    protected void init() {
        log.fine("No service implementation instance available, initializing...");
        try {
            // The actual instance we ware going to use and hold a reference to (1:1 instance for manager)
            serviceImpl = createServiceInstance();

            // How the implementation instance will tell us about property changes
            propertyChangeSupport = createPropertyChangeSupport(serviceImpl);
            propertyChangeSupport.addPropertyChangeListener(createPropertyChangeListener(serviceImpl));

        } catch (Exception ex) {
            throw new RuntimeException("Could not initialize implementation: " + ex, ex);
        }
    }

    protected T createServiceInstance() throws Exception {
        if (serviceClass == null) {
            throw new IllegalStateException("Subclass has to provide service class or override createServiceInstance()");
        }
        try {
            // Use this constructor if possible
            return serviceClass.getConstructor(LocalService.class).newInstance(getService());
        } catch (NoSuchMethodException ex) {
            log.fine("Creating new service implementation instance with no-arg constructor: " + serviceClass.getName());
            return serviceClass.newInstance();
        }
    }

    protected PropertyChangeSupport createPropertyChangeSupport(T serviceImpl) throws Exception {
        Method m;
        if ((m = Reflections.getGetterMethod(serviceImpl.getClass(), "propertyChangeSupport")) != null &&
            PropertyChangeSupport.class.isAssignableFrom(m.getReturnType())) {
            log.fine("Service implementation instance offers PropertyChangeSupport, using that: " + serviceImpl.getClass().getName());
            return (PropertyChangeSupport) m.invoke(serviceImpl);
        }
        log.fine("Creating new PropertyChangeSupport for service implementation: " + serviceImpl.getClass().getName());
        return new PropertyChangeSupport(serviceImpl);
    }

    protected PropertyChangeListener createPropertyChangeListener(T serviceImpl) throws Exception {
        return new DefaultPropertyChangeListener();
    }

    protected Collection<StateVariableValue> readInitialEventedStateVariableValues() throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Implementation: " + serviceImpl;
    }

    protected class DefaultPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent e) {
            log.finer("Property change event on local service: " + e.getPropertyName());

            // Prevent recursion
            if (e.getPropertyName().equals(EVENTED_STATE_VARIABLES)) return;

            String[] variableNames = ModelUtil.fromCommaSeparatedList(e.getPropertyName());
            log.fine("Changed variable names: " + Arrays.toString(variableNames));

            try {
                Collection<StateVariableValue> currentValues = getCurrentState(variableNames);

                if (!currentValues.isEmpty()) {
                    getPropertyChangeSupport().firePropertyChange(
                        EVENTED_STATE_VARIABLES,
                        null,
                        currentValues
                    );
                }

            } catch (Exception ex) {
                // TODO: Is it OK to only log this error? It means we keep running although we couldn't send events?
                log.log(
                    Level.SEVERE,
                    "Error reading state of service after state variable update event: " + Exceptions.unwrap(ex),
                    ex
                );
            }
        }
    }
}
