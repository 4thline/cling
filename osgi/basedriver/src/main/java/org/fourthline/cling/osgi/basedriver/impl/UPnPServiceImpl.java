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

package org.fourthline.cling.osgi.basedriver.impl;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Bruce Green
 */
public class UPnPServiceImpl implements UPnPService {

    private Service service;
    private UPnPAction[] actions;
    private Hashtable<String, UPnPAction> actionsIndex;
    private UPnPStateVariable[] variables;
    private Hashtable<String, UPnPStateVariable> variablesIndex;

    public UPnPServiceImpl(Service service) {
        this.service = service;

        if (service.getActions() != null) {
            List<UPnPAction> list = new ArrayList<UPnPAction>();
            actionsIndex = new Hashtable<String, UPnPAction>();

            for (Action<?> action : service.getActions()) {
                UPnPAction item = new UPnPActionImpl(action);
                list.add(item);
                actionsIndex.put(item.getName(), item);
            }

            actions = list.toArray(new UPnPAction[list.size()]);
        }

        if (service.getStateVariables() != null) {
            List<UPnPStateVariable> list = new ArrayList<UPnPStateVariable>();
            variablesIndex = new Hashtable<String, UPnPStateVariable>();

            for (StateVariable<?> variable : service.getStateVariables()) {
                UPnPStateVariable item = new UPnPStateVariableImpl(variable);
                list.add(item);
                variablesIndex.put(item.getName(), item);
            }

            variables = list.toArray(new UPnPStateVariable[list.size()]);
        }
    }

    @Override
    public String getId() {
        return service.getServiceId().toString();
    }

    @Override
    public String getType() {
        return service.getServiceType().toString();
    }

    @Override
    public String getVersion() {
        return String.valueOf(service.getServiceType().getVersion());
    }

    @Override
    public UPnPAction getAction(String name) {
        return (actionsIndex != null) ? actionsIndex.get(name) : null;
    }

    @Override
    public UPnPAction[] getActions() {
        return actions;
    }

    @Override
    public UPnPStateVariable[] getStateVariables() {
        return variables;
    }

    @Override
    public UPnPStateVariable getStateVariable(String name) {
        return (variablesIndex != null) ? variablesIndex.get(name) : null;
    }

    public Service<?, ?> getService() {
        return service;
    }
}
