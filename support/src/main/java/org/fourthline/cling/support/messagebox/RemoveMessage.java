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

package org.fourthline.cling.support.messagebox;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.messagebox.model.Message;

/**
 * ATTENTION: My Samsung TV does not implement this!
 *
 * @author Christian Bauer
 */
public abstract class RemoveMessage extends ActionCallback {

    public RemoveMessage(Service service, Message message) {
        this(service, message.getId());
    }

    public RemoveMessage(Service service, int id) {
        super(new ActionInvocation(service.getAction("RemoveMessage")));
        getActionInvocation().setInput("MessageID", id);
    }

}
