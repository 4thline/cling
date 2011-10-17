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
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;

/**
 * @author Christian Bauer
 */
public abstract class GetExternalIP extends ActionCallback {

    public GetExternalIP(Service service) {
        super(new ActionInvocation(service.getAction("GetExternalIPAddress")));
    }

    @Override
    public void success(ActionInvocation invocation) {
        success((String)invocation.getOutput("NewExternalIPAddress").getValue());
    }

    protected abstract void success(String externalIPAddress);
}
