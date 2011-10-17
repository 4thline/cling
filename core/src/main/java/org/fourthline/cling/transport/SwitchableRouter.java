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
