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

package org.fourthline.cling.workbench.spi;

import org.fourthline.cling.model.meta.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.ServiceLoader;

@Singleton
public class PluginRegistry {

    ServiceLoader<ControlPointAdapter> controlPointLoader;

    @PostConstruct
    public void init() {
        controlPointLoader = ServiceLoader.load(ControlPointAdapter.class);
    }

/*
    public void onUseServiceRequest(@Observes UseService request) {
        ControlPointAdapter controlPointAdapter =
                getControlPointAdapter(request.service);
        if (controlPointAdapter != null) {
            controlPointAdapter.start(request.service);
        } else {
            Workbench.log(
                    Level.WARNING,
                    "PluginRegistry",
                    "No control point plugin available for service type: " + request.service.getServiceType()
            );
        }
    }
*/

    public ControlPointAdapter getControlPointAdapter(Service service) {
        // TODO: Just take the first, we really should provide a drop-down menu
        for (ControlPointAdapter controlPointAdapter : controlPointLoader) {
            if (controlPointAdapter.getServiceType().implementsVersion(service.getServiceType())) {
                return controlPointAdapter;
            }
        }
        return null;
    }

}
