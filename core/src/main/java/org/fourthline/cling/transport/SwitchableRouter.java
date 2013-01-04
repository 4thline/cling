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

package org.fourthline.cling.transport;

import org.fourthline.cling.transport.spi.InitializationException;

/**
 * Switchable network transport layer interface.
 * <p>
 * This router can be turned on and off, it will shutdown all listening
 * threads and close all listening sockets when it is disabled, and
 * rebind when it is enabled.
 * </p>
 * While disabled, only mock responses (mostly <code>null</code>) will be returned
 * from this network transport layer, and all operations are NOOPs.
 * </p>
 *
 * @author Christian Bauer
 */
public interface SwitchableRouter extends Router {

    boolean isEnabled();

    /**
     * @return <code>true</code> if the router was enabled. <code>false</code> if it's already running.
     */
    boolean enable();

    /**
     * @return <code>true</code> if the router was disabled. <code>false</code> if it wasn't running.
     */
    boolean disable();

    /**
     * Called by the {@link #enable()} method before it returns.
     *
     * @param ex The cause of the failure.
     */
    void handleStartFailure(InitializationException ex);
}
