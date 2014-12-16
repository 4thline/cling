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

package org.fourthline.cling.support.contentdirectory.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.ErrorCode;

/**
 *
 * @author Christian Bauer
 */
public abstract class GetSystemUpdateID extends ActionCallback {

    public GetSystemUpdateID(org.fourthline.cling.model.meta.Service service) {
        super(new ActionInvocation(service.getAction("GetSystemUpdateID")));
    }

    public void success(ActionInvocation invocation) {
        boolean ok = true;
        long id = 0;
        try {
            id = Long.valueOf(invocation.getOutput("Id").getValue().toString()); // UnsignedIntegerFourBytes...
        } catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse GetSystemUpdateID response: " + ex, ex));
            failure(invocation, null);
            ok = false;
        }
        if (ok) received(invocation, id);
    }

    public abstract void received(ActionInvocation invocation, long systemUpdateID);

}
