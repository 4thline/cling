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

package org.fourthline.cling.osgi.basedriver.present;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.osgi.basedriver.util.OSGiDataConverter;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
class UPnPActionExecutor implements ActionExecutor {

    final private static Logger log = Logger.getLogger(UPnPActionExecutor.class.getName());

    private UPnPAction action;

    public UPnPActionExecutor(UPnPAction action) {
        this.action = action;
    }

    @Override
    public void execute(ActionInvocation<LocalService> actionInvocation) {
        log.entering(this.getClass().getName(), "execute", new Object[]{actionInvocation});

        ActionArgumentValue<LocalService>[] inputs = actionInvocation.getInput();

        Dictionary<String, Object> args = new Hashtable<String, Object>();
        for (ActionArgumentValue<LocalService> input : inputs) {
            ActionArgument<?> argument = input.getArgument();

            args.put(argument.getName(), OSGiDataConverter.toOSGiValue(input.getDatatype(), input.getValue()));
        }

        try {
            Dictionary out = action.invoke(args);

            if (out != null) {
                for (String key : (List<String>) Collections.list(out.keys())) {

                    Object value = out.get(key);
                    if (value != null) {
                        UPnPStateVariable variable = action.getStateVariable(key);
                        value = OSGiDataConverter.toClingValue(variable.getUPnPDataType(), value);

                        try {
                            //System.out.printf("*** key: %s  value: %s [%s]\n", key, value, value);
                            actionInvocation.setOutput(key, value);
                        } catch (InvalidValueException e) {
                            log.severe(String.format("Error executing action %s variable %s.", action.getName(), key));
                            log.severe(e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.severe(String.format("Error executing action (%s).", action.getName()));
            log.severe(e.getMessage());
        }
    }
}
