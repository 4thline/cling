/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.profile.RemoteClientInfo;

/**
 * An action invocation by a remote control point.
 *
 * @author Christian Bauer
 */
public class RemoteActionInvocation extends ActionInvocation {

    final protected RemoteClientInfo remoteClientInfo;

    public RemoteActionInvocation(Action action,
                                  ActionArgumentValue[] input,
                                  ActionArgumentValue[] output,
                                  RemoteClientInfo remoteClientInfo) {
        super(action, input, output, null);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteActionInvocation(Action action,
                                  RemoteClientInfo remoteClientInfo) {
        super(action);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteActionInvocation(ActionException failure,
                            RemoteClientInfo remoteClientInfo) {
        super(failure);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteClientInfo getRemoteClientInfo() {
        return remoteClientInfo;
    }

}
