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
     * disappearing devices will be multicast'ed, existing event subscriptions cancelled.
     * </p>
     */
    public void shutdown();

    static public class Start {

    }

    static public class Shutdown {

    }

}
