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

package org.fourthline.cling.transport.spi;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Implement this to provide your own servlet container (instance),
 * <p>
 * It's OK if you don't start or stop your container when this adapter is
 * called. You can treat the {@link #startIfNotRunning()} and
 * {@link #stopIfRunning()} methods as suggestions, they only indicate what
 * the UPnP stack wants to do. If your servlet container handles other
 * services, keep it running all the time.
 * </p>
 * <p>
 * An implementation must be thread-safe, all methods might be called concurrently
 * by several threads.
 * </p>
 *
 * @author Christian Bauer
 */
public interface ServletContainerAdapter {

    /**
     * Might be called several times to set up the connectors.
     *
     * @param host The host address for the socket.
     * @param port The port, might be <code>-1</code> to bind to an ephemeral port.
     * @return The actual registered local port.
     * @throws IOException If the connector couldn't be opened to retrieve the registered local port.
     */
    int addConnector(String host, int port) throws IOException;

    /**
     * Might be called several times register (the same) handler for UPnP requests, should only register it once.
     *
     * @param contextPath The context path prefix for all UPnP requests.
     * @param servlet The servlet handling all UPnP requests.
     */
    void registerServlet(String contextPath, Servlet servlet);

    /**
     * Start your servlet container if it isn't already running, might be called multiple times.
     */
    void startIfNotRunning();

    /**
     * Stop your servlet container if it's still running, might be called multiple times.
     */
    void stopIfRunning();

}
