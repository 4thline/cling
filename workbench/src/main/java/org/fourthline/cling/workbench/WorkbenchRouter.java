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

package org.fourthline.cling.workbench;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterImpl;
import org.fourthline.cling.transport.spi.InitializationException;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

/**
 * When network transport initialization fails, throw exception and
 * exit application instead of logging only warnings.
 *
 * @author Christian Bauer
 */
@Alternative
@Specializes
public class WorkbenchRouter extends RouterImpl {

    public WorkbenchRouter() {
    }

    @Inject
    public WorkbenchRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory) {
        super(configuration, protocolFactory);
    }

    @Override
    public void handleStartFailure(InitializationException ex) {
        throw ex;
    }
}
