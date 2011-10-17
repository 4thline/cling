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

package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class MutableService {

    public ServiceType serviceType;
    public ServiceId serviceId;
    public URI descriptorURI;
    public URI controlURI;
    public URI eventSubscriptionURI;

    public List<MutableAction> actions = new ArrayList();
    public List<MutableStateVariable> stateVariables = new ArrayList();

    public Service build(Device prototype) throws ValidationException {
        return prototype.newInstance(
                serviceType, serviceId,
                descriptorURI, controlURI, eventSubscriptionURI,
                createActions(),
                createStateVariables()
        );
    }

    public Action[] createActions() {
        Action[] array = new Action[actions.size()];
        int i = 0;
        for (MutableAction action : actions) {
            array[i++] = action.build();
        }
        return array;
    }

    public StateVariable[] createStateVariables() {
        StateVariable[] array = new StateVariable[stateVariables.size()];
        int i = 0;
        for (MutableStateVariable stateVariable : stateVariables) {
            array[i++] = stateVariable.build();
        }
        return array;
    }

}
