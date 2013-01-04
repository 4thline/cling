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

package org.fourthline.cling.osgi.basedriver.present;

import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;

import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
class UPnPStateVariableAccessor extends StateVariableAccessor {

    final private static Logger log = Logger.getLogger(UPnPStateVariableAccessor.class.getName());

    private UPnPStateVariable variable;

    public UPnPStateVariableAccessor(UPnPStateVariable variable) {
        this.variable = variable;
    }

    @Override
    public Class<?> getReturnType() {
        log.entering(this.getClass().getName(), "getReturnType", new Object[]{});
        return variable.getJavaDataType();
    }

    @Override
    public Object read(Object serviceImpl) throws Exception {
        log.entering(this.getClass().getName(), "read", new Object[]{serviceImpl});
        return null;
    }
}
