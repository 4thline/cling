/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.SeekMode;

import java.util.logging.Logger;

/**
 *
 * @author Christian Bauer
 */
public abstract class Seek extends ActionCallback {

    private static Logger log = Logger.getLogger(Seek.class.getName());

    public Seek(Service service, String relativeTimeTarget) {
        this(new UnsignedIntegerFourBytes(0), service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service service, String relativeTimeTarget) {
        this(instanceId, service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(Service service, SeekMode mode, String target) {
        this(new UnsignedIntegerFourBytes(0), service, mode, target);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service service, SeekMode mode, String target) {
        super(new ActionInvocation(service.getAction("Seek")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Unit", mode.name());
        getActionInvocation().setInput("Target", target);
    }

    @Override
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}