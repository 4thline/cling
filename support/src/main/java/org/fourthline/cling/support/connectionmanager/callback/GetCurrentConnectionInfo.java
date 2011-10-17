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
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public abstract class GetCurrentConnectionInfo extends ActionCallback {

    public GetCurrentConnectionInfo(Service service, int connectionID) {
        this(service, null, connectionID);
    }

    protected GetCurrentConnectionInfo(Service service, ControlPoint controlPoint, int connectionID) {
        super(new ActionInvocation(service.getAction("GetCurrentConnectionInfo")), controlPoint);
		getActionInvocation().setInput("ConnectionID", connectionID);
	}

    @Override
    public void success(ActionInvocation invocation) {

        try {
            ConnectionInfo info = new ConnectionInfo(
                    (Integer)invocation.getInput("ConnectionID").getValue(),
                    (Integer)invocation.getOutput("RcsID").getValue(),
                    (Integer)invocation.getOutput("AVTransportID").getValue(),
                    new ProtocolInfo(invocation.getOutput("ProtocolInfo").toString()),
                    new ServiceReference(invocation.getOutput("PeerConnectionManager").toString()),
                    (Integer)invocation.getOutput("PeerConnectionID").getValue(),
                    ConnectionInfo.Direction.valueOf(invocation.getOutput("Direction").toString()),
                    ConnectionInfo.Status.valueOf(invocation.getOutput("Status").toString())
            );

            received(invocation, info);

        } catch (Exception ex) {
            invocation.setFailure(
                    new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ConnectionInfo response: " + ex, ex)
            );
            failure(invocation, null);
        }
    }

    public abstract void received(ActionInvocation invocation, ConnectionInfo connectionInfo);

}
