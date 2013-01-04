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

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;

/**
 * @author Christian Bauer
 */
public abstract class ConnectionComplete extends ActionCallback {

    public ConnectionComplete(Service service, int connectionID) {
        this(service, null, connectionID);
    }

    protected ConnectionComplete(Service service, ControlPoint controlPoint, int connectionID) {
        super(new ActionInvocation(service.getAction("ConnectionComplete")), controlPoint);
		getActionInvocation().setInput("ConnectionID", connectionID);
	}

}
