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

package org.fourthline.cling.workbench.plugins.igd;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.workbench.plugins.igd.impl.WANIPConnectionPresenter;
import org.fourthline.cling.workbench.spi.AbstractControlPointAdapter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class WANIPConnectionControlPoint extends AbstractControlPointAdapter {

    final public static Logger LOGGER = Logger.getLogger("WANIPConnection ControlPoint");

    @Inject
    protected Instance<WANIPConnectionPresenter> presenterInstance;

    @Override
    protected ServiceType[] getSupportedServiceTypes() {
        return new ServiceType[]{new UDAServiceType("WANIPConnection", 1)};
    }

    @Override
    protected void onUseServiceRequest(Service service) {
        presenterInstance.get().init(service);
    }
}
