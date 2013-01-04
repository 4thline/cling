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

package org.fourthline.cling.bridge;

import org.jboss.resteasy.spi.NoLogWebApplicationException;

import javax.ws.rs.core.Response;

/**
 * They couldn't just standardize this? Oh and the logging in resteasy, ridiculous.
 *
 * @author Christian Bauer
 */
public class BridgeWebApplicationException extends NoLogWebApplicationException {

    public BridgeWebApplicationException(Response.Status status) {
        super(status);
    }

    public BridgeWebApplicationException(Response.Status status, String message) {
        super(Response.status(status).entity(message).type("text/plain").build());
    }
}
