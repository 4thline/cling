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

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.header.SoapActionHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.SoapActionType;

/**
 * @author Christian Bauer
 */
public class IncomingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage {

    final private Action action;
    final private String actionNamespace;

    public IncomingActionRequestMessage(StreamRequestMessage source,
                                        LocalService service) throws ActionException {
        super(source);

        SoapActionHeader soapActionHeader = getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION, SoapActionHeader.class);
        if (soapActionHeader == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Missing SOAP action header");
        }

        SoapActionType actionType = soapActionHeader.getValue();

        this.action = service.getAction(actionType.getActionName());
        if (this.action == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't implement action: " + actionType.getActionName());
        }

        if (!QueryStateVariableAction.ACTION_NAME.equals(actionType.getActionName())) {
            if (!service.getServiceType().implementsVersion(actionType.getServiceType())) {
                throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't support the requested service version");
            }
        }

        this.actionNamespace = actionType.getTypeString();
    }

    public Action getAction() {
        return action;
    }

    public String getActionNamespace() {
        return actionNamespace;
    }

}
