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
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.PlayMode;

import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public abstract class SetPlayMode extends ActionCallback {

    private static Logger log = Logger.getLogger(SetPlayMode.class.getName());

    public SetPlayMode(Service service, PlayMode playMode) {
        this(new UnsignedIntegerFourBytes(0), service, playMode);
    }

    public SetPlayMode(UnsignedIntegerFourBytes instanceId, Service service, PlayMode playMode) {
        super(new ActionInvocation(service.getAction("SetPlayMode")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("NewPlayMode", playMode.toString());
    }

    @Override
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}