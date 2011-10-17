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

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.ProtocolInfos;

/**
 * @author Christian Bauer
 */
public abstract class GetProtocolInfo extends ActionCallback {

    public GetProtocolInfo(Service service) {
        this(service, null);
    }

    protected GetProtocolInfo(Service service, ControlPoint controlPoint) {
        super(new ActionInvocation(service.getAction("GetProtocolInfo")), controlPoint);
    }

    @Override
    public void success(ActionInvocation invocation) {
        try {
            ActionArgumentValue sink = invocation.getOutput("Sink");
            ActionArgumentValue source = invocation.getOutput("Source");

            received(
                    invocation,
                    sink != null ? new ProtocolInfos(sink.toString()) : null,
                    source != null ? new ProtocolInfos(source.toString()) : null
            );

        } catch (Exception ex) {
            invocation.setFailure(
                    new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex)
            );
            failure(invocation, null);
        }
    }

    public abstract void received(ActionInvocation actionInvocation, ProtocolInfos sinkProtocolInfos, ProtocolInfos sourceProtocolInfos);

}