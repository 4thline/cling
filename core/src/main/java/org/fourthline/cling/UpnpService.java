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

package org.fourthline.cling;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;

/**
 * Primary interface of the Cling Core UPnP stack.
 * <p>
 * An implementation can either start immediately when constructed or offer an additional
 * method that starts the UPnP stack on-demand. Implementations are not required to be
 * restartable after shutdown.
 * </p>
 * <p>
 * Implementations are always thread-safe and can be shared and called concurrently.
 * </p>
 *
 * @author Christian Bauer
 */
public interface UpnpService {

    public UpnpServiceConfiguration getConfiguration();

    public ControlPoint getControlPoint();

    public ProtocolFactory getProtocolFactory();

    public Registry getRegistry();

    public Router getRouter();

    /**
     * Stopping the UPnP stack.
     * <p>
     * Clients are required to stop the UPnP stack properly. Notifications for
     * disappearing devices will be multicasted, existing event subscriptions canceled.
     * </p>
     */
    public void shutdown();

    static public class Start {

    }

    static public class Shutdown {

    }

}
