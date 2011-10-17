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
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.osgi.basedriver.Activator;
import org.fourthline.cling.osgi.basedriver.util.OSGiDataConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
public class UPnPActionImpl implements UPnPAction {

    final private static Logger log = Logger.getLogger(UPnPActionImpl.class.getName());

    private Action<?> action;

    public UPnPActionImpl(Action<?> action) {
        this.action = action;
    }

    @Override
    public String getName() {
        return action.getName();
    }

    @Override
    public String getReturnArgumentName() {
        String name = null;

        for (ActionArgument<?> argument : action.getArguments()) {
            if (argument.isReturnValue()) {
                name = argument.getName();
                break;
            }
        }

        return name;
    }

    @Override
    public String[] getInputArgumentNames() {
        List<String> list = new ArrayList<String>();
        for (ActionArgument<?> argument : action.getInputArguments()) {
            list.add(argument.getName());
        }

        return list.size() != 0 ? (String[]) list.toArray(new String[list.size()]) : null;
    }

    @Override
    public String[] getOutputArgumentNames() {
        List<String> list = new ArrayList<String>();
        for (ActionArgument<?> argument : action.getOutputArguments()) {
            list.add(argument.getName());
        }

        return list.size() != 0 ? (String[]) list.toArray(new String[list.size()]) : null;
    }

    @Override
    public UPnPStateVariable getStateVariable(String argumentName) {
        StateVariable variable = null;

        ActionArgument<?> argument = action.getInputArgument(argumentName);
        if (argument == null) {
            argument = action.getOutputArgument(argumentName);
        }
        if (argument != null) {
            String name = argument.getRelatedStateVariableName();
            variable = action.getService().getStateVariable(name);
        }

        return (argument != null) ? new UPnPStateVariableImpl(variable) : null;
    }

    @Override
    public Dictionary invoke(Dictionary args) throws Exception {
        Dictionary<Object, Object> output = null;

        List<ActionArgumentValue<?>> input = new ArrayList<ActionArgumentValue<?>>();

        if (args != null) {
            for (String key : (ArrayList<String>) Collections.list(args.keys())) {
                ActionArgument<?> argument = action.getInputArgument(key);

                Object value = args.get(key);
                //System.out.printf("key: %s value: %s\n", key, value);

                if (!value.getClass().equals(
                        argument.getDatatype().getBuiltin().getDeclaringClass())
                        ) {
                    value = OSGiDataConverter.toClingValue(
                            argument.getDatatype().getBuiltin().getDescriptorName(),
                            value
                    );
                    //System.out.printf("key: %s value: %s\n", key, value);
                }

                input.add(new ActionArgumentValue(argument, value));
            }
        }

        ControlPoint controlPoint = Activator.getPlugin().getUpnpService().getControlPoint();
        ActionInvocation<?> actionInvocation =
                new ActionInvocation(action, input.toArray(new ActionArgumentValue<?>[input.size()]));

        new ActionCallback.Default(actionInvocation, controlPoint).run();

        if (actionInvocation.getFailure() == null) {
            ActionArgumentValue<?>[] arguments = actionInvocation.getOutput();
            if (arguments != null && arguments.length != 0) {
                output = new Hashtable<Object, Object>();
                for (ActionArgumentValue<?> argument : arguments) {
                    String name = argument.getArgument().getName();
                    Object value = argument.getValue();

                    if (value == null) {
                        log.severe(String.format(
                                "Received null value for variable %s to OSGi type %s.",
                                name,
                                argument.getDatatype().getDisplayString()
                        ));
                        // throw an exception
                    } else {
                        //System.out.printf("name: %s  value: %s (%s)\n", name, value, value.getClass().getName());

                        value = OSGiDataConverter.toOSGiValue(argument.getDatatype(), value);

                        if (value == null) {
                            log.severe(String.format(
                                    "Cannot convert variable %s to OSGi type %s.",
                                    name,
                                    argument.getDatatype().getDisplayString()
                            ));
                            // throw an exception
                        }
                        output.put(name, value);
                    }
                }
            }
        }

        return output;
    }
}
