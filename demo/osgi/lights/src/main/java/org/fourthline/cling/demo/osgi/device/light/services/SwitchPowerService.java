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

import org.fourthline.cling.demo.osgi.device.light.actions.ClickAction;
import org.fourthline.cling.demo.osgi.device.light.actions.GetTargetAction;
import org.fourthline.cling.demo.osgi.device.light.actions.SetTargetAction;
import org.fourthline.cling.demo.osgi.device.light.model.LightSwitch;
import org.fourthline.cling.demo.osgi.device.light.variables.StatusStateVariable;
import org.fourthline.cling.demo.osgi.device.light.variables.TargetStateVariable;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.demo.osgi.device.light.actions.GetStatusAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruce Green
 */
public class SwitchPowerService implements UPnPService {

    private UPnPStateVariable[] variables;
    private Map<String, UPnPStateVariable> variablesIndex = new HashMap<String, UPnPStateVariable>();
    private UPnPAction[] actions;
    private Map<String, UPnPAction> actionsIndex = new HashMap<String, UPnPAction>();

    public SwitchPowerService(UPnPDevice device, LightSwitch lightSwitch) {
        TargetStateVariable targetStateVariable = new TargetStateVariable(device, this, lightSwitch);
        StatusStateVariable statusStateVariable = new StatusStateVariable(device, this, lightSwitch);

        variables = new UPnPStateVariable[]{targetStateVariable, statusStateVariable};
        for (UPnPStateVariable variable : variables) {
            variablesIndex.put(variable.getName(), variable);
        }

        actions = new UPnPAction[]{
                new SetTargetAction(targetStateVariable),
                new GetTargetAction(targetStateVariable),
                new GetStatusAction(statusStateVariable),
                new ClickAction(targetStateVariable)
        };

        for (UPnPAction action : actions) {
            actionsIndex.put(action.getName(), action);
        }
    }

    @Override
    public String getId() {
        return "urn:upnp-org:serviceId:SwitchPower";
    }

    @Override
    public String getType() {
        return "urn:schemas-upnp-org:service:SwitchPower:1";
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
