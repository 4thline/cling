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
