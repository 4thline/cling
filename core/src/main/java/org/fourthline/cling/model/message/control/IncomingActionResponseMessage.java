/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;

/**
 * @author Christian Bauer
 */
public class IncomingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage {


    public IncomingActionResponseMessage(StreamResponseMessage source) {
        super(source);
    }

    public IncomingActionResponseMessage(UpnpResponse operation) {
        super(operation);
    }

    public String getActionNamespace() {
        return null; // TODO: We _could_ read this in SOAPActionProcessor and set it when we receive a response but why?
    }

    public boolean isFailedNonRecoverable() {
        int statusCode = getOperation().getStatusCode();
        return getOperation().isFailed()
                && !(statusCode == UpnpResponse.Status.METHOD_NOT_SUPPORTED.getStatusCode() ||
                (statusCode == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode()) && hasBody());
    }

    public boolean isFailedRecoverable() {
        return hasBody() && getOperation().getStatusCode() == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

}
