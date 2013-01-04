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

package org.fourthline.cling.demo.osgi.device.light.services;

import org.fourthline.cling.demo.osgi.device.light.model.Dimmer;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.demo.osgi.device.light.actions.GetLoadLevelStatusAction;
import org.fourthline.cling.demo.osgi.device.light.actions.GetLoadLevelTargetAction;
import org.fourthline.cling.demo.osgi.device.light.actions.SetLoadLevelTargetAction;
import org.fourthline.cling.demo.osgi.device.light.variables.LoadLevelStatusStateVariable;
import org.fourthline.cling.demo.osgi.device.light.variables.LoadLevelTargetStateVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruce Green
 */
public class DimmingService implements UPnPService {

    private UPnPStateVariable[] variables;
    private Map<String, UPnPStateVariable> variablesIndex = new HashMap<String, UPnPStateVariable>();
    private UPnPAction[] actions;
    private Map<String, UPnPAction> actionsIndex = new HashMap<String, UPnPAction>();

    public DimmingService(UPnPDevice device, Dimmer dimmer) {
        LoadLevelTargetStateVariable loadLevelTargetStateVariable = new LoadLevelTargetStateVariable(device, this, dimmer);
        LoadLevelStatusStateVariable loadLevelStatusStateVariable = new LoadLevelStatusStateVariable(device, this, dimmer);

        variables = new UPnPStateVariable[]{loadLevelTargetStateVariable, loadLevelStatusStateVariable};
        for (UPnPStateVariable variable : variables) {
            variablesIndex.put(variable.getName(), variable);
        }

        actions = new UPnPAction[]{
                new SetLoadLevelTargetAction(loadLevelTargetStateVariable),
                new GetLoadLevelTargetAction(loadLevelTargetStateVariable),
                new GetLoadLevelStatusAction(loadLevelStatusStateVariable),
        };

        for (UPnPAction action : actions) {
            actionsIndex.put(action.getName(), action);
        }
    }

    @Override
    public String getId() {
        return "urn:upnp-org:serviceId:Dimming";
    }

    @Override
    public String getType() {
        return "urn:schemas-upnp-org:service:Dimming:1";
    }

    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public UPnPAction getAction(String name) {
        return actionsIndex.get(name);
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
        return variablesIndex.get(name);
    }

}
