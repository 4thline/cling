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

package org.fourthline.cling.workbench.plugins.contentdirectory;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.workbench.plugins.contentdirectory.impl.ContentDirectoryPresenter;
import org.fourthline.cling.workbench.spi.AbstractControlPointAdapter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class ContentDirectoryControlPoint extends AbstractControlPointAdapter {

    final public static Logger LOGGER = Logger.getLogger("ContentDirectory ControlPoint");

    @Inject
    protected Instance<ContentDirectoryPresenter> contentDirectoryPresenterInstance;

    @Override
    protected ServiceType[] getSupportedServiceTypes() {
        return new ServiceType[]{new UDAServiceType("ContentDirectory", 1)};
    }

    @Override
    protected void onUseServiceRequest(Service service) {
        contentDirectoryPresenterInstance.get().init(service);
    }
}
