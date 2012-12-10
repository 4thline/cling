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

import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The metadata of a service discovered on a remote device.
 * <p>
 * Includes the URI's for getting the service's descriptor, calling its
 * actions, and subscribing to events.
 * </p>
 * 
 * @author Christian Bauer
 */
public class RemoteService extends Service<RemoteDevice, RemoteService> {

    final private URI descriptorURI;
    final private URI controlURI;
    final private URI eventSubscriptionURI;

    public RemoteService(ServiceType serviceType, ServiceId serviceId,
                         URI descriptorURI, URI controlURI, URI eventSubscriptionURI) throws ValidationException {
        this(serviceType, serviceId, descriptorURI, controlURI, eventSubscriptionURI, null, null);
    }

    public RemoteService(ServiceType serviceType, ServiceId serviceId,
                         URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
                         Action<RemoteService>[] actions, StateVariable<RemoteService>[] stateVariables) throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);

        this.descriptorURI = descriptorURI;
        this.controlURI = controlURI;
        this.eventSubscriptionURI = eventSubscriptionURI;

        List<ValidationError> errors = validateThis();
        if (errors.size() > 0) {
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
    }

    @Override
    public Action getQueryStateVariableAction() {
        return new QueryStateVariableAction(this);
    }

    public URI getDescriptorURI() {
        return descriptorURI;
    }

    public URI getControlURI() {
        return controlURI;
    }

    public URI getEventSubscriptionURI() {
        return eventSubscriptionURI;
    }

    public List<ValidationError> validateThis() {
        List<ValidationError> errors = new ArrayList();

        if (getDescriptorURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "descriptorURI",
                    "Descriptor location (SCPDURL) is required"
            ));
        }

        if (getControlURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "controlURI",
                    "Control URL is required"
            ));
        }

        if (getEventSubscriptionURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "eventSubscriptionURI",
                    "Event subscription URL is required"
            ));
        }

        return errors;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Descriptor: " + getDescriptorURI();
    }

}