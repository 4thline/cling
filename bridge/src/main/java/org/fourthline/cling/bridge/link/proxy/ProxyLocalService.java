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

package org.fourthline.cling.bridge.link.proxy;

import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class ProxyLocalService extends LocalService {

    final private URL eventSubscriptionURL;

    public ProxyLocalService(ServiceType serviceType, ServiceId serviceId,
                             Map<Action, ActionExecutor> actions,
                             Map<StateVariable, StateVariableAccessor> accessors,
                             URL eventSubscriptionURL) throws ValidationException {
        super(serviceType, serviceId, actions, accessors, Collections.EMPTY_SET, false);
        this.eventSubscriptionURL = eventSubscriptionURL;
    }

    // TODO: Finish the proxy stuff for GENA? Or not?
    public URL getEventSubscriptionURL() {
        return eventSubscriptionURL;
    }

    @Override
    public ServiceManager getManager() {
        throw new UnsupportedOperationException("No local service implementation manager available for proxy");
    }
}
