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

package org.fourthline.cling.osgi.device.light.actions;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.osgi.device.light.variables.TargetStateVariable;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bruce Green
 */
public class SetTargetAction implements UPnPAction {

    private Map<String, UPnPStateVariable> variables = new HashMap<String, UPnPStateVariable>();

    final private String NEW_TARGET_VALUE = "NewTargetValue";
    final private String[] IN_ARG_NAMES = new String[]{NEW_TARGET_VALUE};

    public SetTargetAction(TargetStateVariable targetStateVariable) {
        variables.put(NEW_TARGET_VALUE, targetStateVariable);
    }

    @Override
    public String getName() {
        return "SetTarget";
    }

    @Override
    public String getReturnArgumentName() {
        return null;
    }

    @Override
    public String[] getInputArgumentNames() {
        return IN_ARG_NAMES;
    }

    @Override
    public String[] getOutputArgumentNames() {
        return null;
    }

    @Override
    public UPnPStateVariable getStateVariable(String argumentName) {
        return variables.get(argumentName);
    }

    @Override
    public Dictionary invoke(Dictionary args) throws Exception {
        Boolean state = (Boolean) args.get(NEW_TARGET_VALUE);

        if (state == null) {
        } else {
            TargetStateVariable power = (TargetStateVariable) variables.get(NEW_TARGET_VALUE);
            power.setCurrentValue(state);
        }

        return null;
    }

}
