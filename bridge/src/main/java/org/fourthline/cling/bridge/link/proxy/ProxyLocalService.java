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
