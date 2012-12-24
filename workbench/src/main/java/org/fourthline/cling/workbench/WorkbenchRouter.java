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

package org.fourthline.cling.workbench;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.SwitchableRouterImpl;
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
public class WorkbenchRouter extends SwitchableRouterImpl {

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
