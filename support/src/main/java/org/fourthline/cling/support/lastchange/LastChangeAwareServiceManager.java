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

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles the "initial" event state for GENA subscriptions to services using LastChange.
 * <p>
 * When a GENA subscription is made on your AVTransport/RenderingControl service, you have to
 * read the initial state of the service. Ususally Cling would do this for you and simply
 * access all the state variables of your service behind the scenes. But the
 * AVTransport/RenderingControl service doesn't use regular UPnP state variable eventing
 * internally, they rely on the awful "LastChange" mechanism for their "logical" instances.
 * </p>
 * <p>
 * Use this {@link org.fourthline.cling.model.ServiceManager} instead of the default one for
 * these services.
 * </p>
 *
 * @author Christian Bauer
 */
public class LastChangeAwareServiceManager<T extends LastChangeDelegator> extends DefaultServiceManager<T> {

    final protected LastChangeParser lastChangeParser;

    public LastChangeAwareServiceManager(LocalService<T> localService,
                                         LastChangeParser lastChangeParser) {
        this(localService, null, lastChangeParser);
    }

    public LastChangeAwareServiceManager(LocalService<T> localService,
                                         Class<T> serviceClass,
                                         LastChangeParser lastChangeParser) {
        super(localService, serviceClass);
        this.lastChangeParser = lastChangeParser;
    }

    protected LastChangeParser getLastChangeParser() {
        return lastChangeParser;
    }

    /**
     * Call this method to propagate all accumulated "LastChange" values to GENA subscribers.
     */
    public void fireLastChange() {

        // We need to obtain locks in the right order to avoid deadlocks:
        // 1. The lock() of the DefaultServiceManager
        // 2. The monitor/synchronized of the LastChange.fire() method

    	lock();
    	try {
            getImplementation().getLastChange().fire(getPropertyChangeSupport());
    	} finally {
    		unlock();
    	}
    }

    @Override
    protected Collection<StateVariableValue> readInitialEventedStateVariableValues() throws Exception {

        // We don't use the service's internal LastChange but a fresh new one just for
        // this initial event. Modifying the internal one would trigger event notification's
        // to other subscribers!
        LastChange lc = new LastChange(getLastChangeParser());

        // Get the current "logical" instances of the service
        UnsignedIntegerFourBytes[] ids = getImplementation().getCurrentInstanceIds();
        if (ids.length > 0) {
            for (UnsignedIntegerFourBytes instanceId : ids) {
                // Iterate through all "logical" instances and ask them what their state is
                getImplementation().appendCurrentState(lc, instanceId);
            }
        } else {
            // Use the default "logical" instance with ID 0
            getImplementation().appendCurrentState(lc, new UnsignedIntegerFourBytes(0));
        }

        // Sum it all up and return it in the initial event to the GENA subscriber
        StateVariable variable = getService().getStateVariable("LastChange");
        Collection<StateVariableValue> values = new ArrayList<>();
        values.add(new StateVariableValue(variable, lc.toString()));
        return values;
    }

}
