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

package org.fourthline.cling.workbench.plugins.messagebox;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.workbench.spi.ControlPointAdapter;

import java.util.logging.Logger;

/**
 * urn:samsung.com:service:MessageBoxService:1
 *
 * @author Christian Bauer
 */
public class MessageBoxControlPoint implements ControlPointAdapter {

    final public static Logger LOGGER = Logger.getLogger("MessageBox");

    public ServiceType getServiceType() {
        return new ServiceType("samsung.com", "MessageBoxService", 1);
    }

    public void start(Service service) {
/*
        JFrame view = new MessageBoxController(controller, upnpService.getControlPoint(), service).getView();
        Application.center(view);
        view.setVisible(true);
*/
    }
}
