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

package org.fourthline.cling.support.renderingcontrol.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.Channel;

import java.util.logging.Logger;

/**
 *
 * @author Christian Bauer
 */
public abstract class GetVolume extends ActionCallback {

    private static Logger log = Logger.getLogger(GetVolume.class.getName());

    public GetVolume(Service service) {
        this(new UnsignedIntegerFourBytes(0), service);
    }

    public GetVolume(UnsignedIntegerFourBytes instanceId, Service service) {
        super(new ActionInvocation(service.getAction("GetVolume")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
    }

    public void success(ActionInvocation invocation) {
        boolean ok = true;
        int currentVolume = 0;
        try {
            currentVolume = Integer.valueOf(invocation.getOutput("CurrentVolume").getValue().toString()); // UnsignedIntegerTwoBytes...
        } catch (Exception ex) {
            invocation.setFailure(
                    new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex)
            );
            failure(invocation, null);
            ok = false;
        }
        if (ok) received(invocation, currentVolume);
    }

    public abstract void received(ActionInvocation actionInvocation, int currentVolume);

}