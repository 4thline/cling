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

import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.osgi.basedriver.util.UPnPTypeUtil;

import java.util.logging.Logger;

/**
 * TODO: This class is unused?
 *
 * @author Bruce Green
 */
public class UPnPActionArgumentImpl extends UPnPStateVariableImpl {

    final private static Logger log = Logger.getLogger(UPnPActionArgumentImpl.class.getName());

    private ActionArgument<?> argument;

    public UPnPActionArgumentImpl(ActionArgument<?> argument) {
        super(argument.getAction().getService().getStateVariable(argument.getRelatedStateVariableName()));
        this.argument = argument;
    }

    @Override
    public String getName() {
        return argument.getName();
    }

    @Override
    public Class getJavaDataType() {
        String type = argument.getDatatype().getBuiltin().getDescriptorName();
        Class clazz = UPnPTypeUtil.getUPnPClass(type);
        if (clazz == null) {
            log.warning(String.format("Cannot covert UPnP type %s to UPnP Java type", type));
        }
        return clazz != null ? clazz : argument.getDatatype().getClass();
    }

    @Override
    public String getUPnPDataType() {
        return argument.getDatatype().getDisplayString();
    }
}
