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

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.Connection;

/**
 * @author Christian Bauer
 */
public abstract class GetStatusInfo extends ActionCallback {

    public GetStatusInfo(Service service) {
        super(new ActionInvocation(service.getAction("GetStatusInfo")));
    }

    @Override
    public void success(ActionInvocation invocation) {

        try {
            Connection.Status status =
                    Connection.Status.valueOf(invocation.getOutput("NewConnectionStatus").getValue().toString());

            Connection.Error lastError =
                    Connection.Error.valueOf(invocation.getOutput("NewLastConnectionError").getValue().toString());

            success(new Connection.StatusInfo(status, (UnsignedIntegerFourBytes) invocation.getOutput("NewUptime").getValue(), lastError));

        } catch (Exception ex) {
            invocation.setFailure(
                    new ActionException(
                            ErrorCode.ARGUMENT_VALUE_INVALID,
                            "Invalid status or last error string: " + ex,
                            ex
                    )
            );
            failure(invocation, null);
        }
    }

    protected abstract void success(Connection.StatusInfo statusInfo);
}
