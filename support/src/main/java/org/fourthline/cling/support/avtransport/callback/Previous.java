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

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;

import java.util.logging.Logger;

/**
 *
 * @author Christian Bauer
 */
public abstract class Previous extends ActionCallback {

    private static Logger log = Logger.getLogger(Previous.class.getName());

    protected Previous(ActionInvocation actionInvocation, ControlPoint controlPoint) {
        super(actionInvocation, controlPoint);
    }

    protected Previous(ActionInvocation actionInvocation) {
        super(actionInvocation);
    }

    public Previous(Service service) {
        this(new UnsignedIntegerEightBytes(0), service);
    }

    public Previous(UnsignedIntegerEightBytes instanceId, Service service) {
        super(new ActionInvocation(service.getAction("Previous")));
        getActionInvocation().setInput("InstanceID", instanceId);
    }

    @Override
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}