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

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.EXTHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

/**
 * @author Christian Bauer
 */
public class OutgoingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage {

    private String actionNamespace;

    public OutgoingActionResponseMessage(Action action) {
        this(UpnpResponse.Status.OK, action);
    }

    public OutgoingActionResponseMessage(UpnpResponse.Status status) {
        this(status, null);
    }

    public OutgoingActionResponseMessage(UpnpResponse.Status status, Action action) {
        super(new UpnpResponse(status));

        if (action != null) {
            if (action instanceof QueryStateVariableAction) {
                this.actionNamespace = Constants.NS_UPNP_CONTROL_10;
            } else {
                this.actionNamespace = action.getService().getServiceType().toString();
            }
        }

        addHeaders();
    }

    protected void addHeaders() {
        getHeaders().add(
                UpnpHeader.Type.CONTENT_TYPE,
                new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
        );
        getHeaders().add(
                UpnpHeader.Type.SERVER,
                new ServerHeader()
        );
        getHeaders().add(
                UpnpHeader.Type.EXT,
                new EXTHeader()
        );
    }

    public String getActionNamespace() {
        return actionNamespace;
    }


}
