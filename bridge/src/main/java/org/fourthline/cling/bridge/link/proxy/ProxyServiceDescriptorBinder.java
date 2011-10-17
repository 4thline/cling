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

import org.fourthline.cling.binding.staging.MutableService;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.seamless.util.URIUtil;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class ProxyServiceDescriptorBinder extends UDA10ServiceDescriptorBinderImpl {

    final private static Logger log = Logger.getLogger(ProxyServiceDescriptorBinder.class.getName());

    final private BridgeUpnpServiceConfiguration configuration;
    final private Endpoint endpoint;

    public ProxyServiceDescriptorBinder(BridgeUpnpServiceConfiguration configuration, Endpoint endpoint) {
        this.configuration = configuration;
        this.endpoint = endpoint;
    }

    public BridgeUpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    protected LocalService buildInstance(Service undescribedService, MutableService descriptor) throws ValidationException {
        if (!(undescribedService instanceof LocalService)) {
            throw new IllegalArgumentException("Proxy can only be created for local service, not: " + undescribedService);
        }

        log.fine("Creating proxy local service: " + undescribedService.getServiceId());

        // These URIs are absolute, they arrived like this in the descriptor (see rewriting in ProxyDiscovery)
        URL controlURL = URIUtil.createAbsoluteURL(getEndpoint().getCallback(), descriptor.controlURI);
        log.fine("Using control URL: " + controlURL);
        URL eventSubscriptionURL = URIUtil.createAbsoluteURL(getEndpoint().getCallback(), descriptor.eventSubscriptionURI);
        log.fine("Using event subscription URL: " + eventSubscriptionURL);

        Map<Action, ActionExecutor> actionExecutors =
                getActionExecutors(descriptor.createActions(), controlURL);

        Map<StateVariable, StateVariableAccessor> stateVariableAccessors =
                getStateVariableAccessors(descriptor.createStateVariables());

        return new ProxyLocalService(
                descriptor.serviceType,
                descriptor.serviceId,
                actionExecutors,
                stateVariableAccessors,
                eventSubscriptionURL
        );
    }

    protected Map<Action, ActionExecutor> getActionExecutors(Action[] actions, URL controlURL) {
        log.fine("Creating proxy action executors with control URL: " + controlURL);
        Map<Action, ActionExecutor> executors = new HashMap();

        for (Action action : actions) {
            executors.put(action, new ProxyActionExecutor(getConfiguration(), controlURL, getEndpoint().getCredentials()));
        }
        return executors;
    }

    protected Map<StateVariable, StateVariableAccessor> getStateVariableAccessors(StateVariable[] stateVariables) {
        Map<StateVariable, StateVariableAccessor> accessors = new HashMap();
        for (StateVariable stateVariable : stateVariables) {
            accessors.put(
                    stateVariable,
                    new StateVariableAccessor() {
                        @Override
                        public Class<?> getReturnType() {
                            throw new UnsupportedOperationException("Can't read state variable value of proxied service");
                        }

                        @Override
                        public Object read(Object serviceImpl) throws Exception {
                            throw new UnsupportedOperationException("Can't read state variable value of proxied service");
                        }
                    }
            );
        }
        return accessors;
    }

}
