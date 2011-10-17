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

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public abstract class SetAVTransportURI extends ActionCallback {

    private static Logger log = Logger.getLogger(SetAVTransportURI.class.getName());

    public SetAVTransportURI(Service service, String uri) {
        this(new UnsignedIntegerFourBytes(0), service, uri, null);
    }

    public SetAVTransportURI(Service service, String uri, String metadata) {
        this(new UnsignedIntegerFourBytes(0), service, uri, metadata);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service service, String uri) {
        this(instanceId, service, uri, null);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service service, String uri, String metadata) {
        super(new ActionInvocation(service.getAction("SetAVTransportURI")));
        log.fine("Creating SetAVTransportURI action for URI: " + uri);
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("CurrentURI", uri);
        getActionInvocation().setInput("CurrentURIMetaData", metadata);
    }

    @Override
    public void success(ActionInvocation invocation) {
        log.fine("Execution successful");
    }
}