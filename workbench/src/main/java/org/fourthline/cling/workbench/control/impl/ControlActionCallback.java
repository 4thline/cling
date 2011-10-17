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

package org.fourthline.cling.workbench.control.impl;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.workbench.Workbench;

import java.util.logging.Level;


abstract public class ControlActionCallback extends ActionCallback {

    public ControlActionCallback(ActionInvocation actionInvocation) {
        super(actionInvocation);
    }

    @Override
    public void success(final ActionInvocation invocation) {
        onSuccess(invocation.getOutput());
        Workbench.log(
                "Action Invocation",
                "Completed invocation: " + invocation.getAction().getName()
        );
    }

    @Override
    public void failure(ActionInvocation invocation,
                        UpnpResponse operation,
                        String defaultMsg) {
        Workbench.log(
                Level.SEVERE,
                "Action Invocation",
                defaultMsg
        );
    }

    abstract protected void onSuccess(ActionArgumentValue[] values);

}
