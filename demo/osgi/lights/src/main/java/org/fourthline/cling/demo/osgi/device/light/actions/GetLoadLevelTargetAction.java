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

package org.fourthline.cling.demo.osgi.device.light.actions;

import org.fourthline.cling.demo.osgi.device.light.variables.LoadLevelTargetStateVariable;
import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * @author Bruce Green
 */
public class GetLoadLevelTargetAction implements UPnPAction {

    final private static String RET_LOAD_LEVEL_TARGET_VALUE = "retLoadLevelTarget";
    final private static String[] OUT_ARG_NAMES = {RET_LOAD_LEVEL_TARGET_VALUE};
    private LoadLevelTargetStateVariable loadLevelTargetStateVariable;

    public GetLoadLevelTargetAction(LoadLevelTargetStateVariable loadLevelTargetStateVariable) {
        this.loadLevelTargetStateVariable = loadLevelTargetStateVariable;
    }

    @Override
    public String getName() {
        return "GetLoadLevelTarget";
    }

    @Override
    public String getReturnArgumentName() {
        return null;
    }

    @Override
    public String[] getInputArgumentNames() {
        return null;
    }

    @Override
    public String[] getOutputArgumentNames() {
        return OUT_ARG_NAMES;
    }

    @Override
    public UPnPStateVariable getStateVariable(String argumentName) {
        return argumentName.equals(RET_LOAD_LEVEL_TARGET_VALUE) ? loadLevelTargetStateVariable : null;
    }

    @Override
    public Dictionary invoke(Dictionary args) throws Exception {
        Boolean state = (Boolean) loadLevelTargetStateVariable.getCurrentValue();

        args = new Hashtable();
        args.put(RET_LOAD_LEVEL_TARGET_VALUE, state);

        return args;
    }

}
