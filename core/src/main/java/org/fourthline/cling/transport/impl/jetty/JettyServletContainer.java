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

package org.fourthline.cling.transport.impl.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fourthline.cling.transport.spi.ServletContainerAdapter;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A basic singleton <code>org.eclipse.jetty.server.Server</code>.
 * <p>
 * This {@link org.fourthline.cling.transport.spi.ServletContainerAdapter} starts
 * a Jetty 8 instance on its own and stops it. Only one single context and servlet
 * is registered, to handle UPnP requests.
 * </p>
 * <p>
 * This implementation works on Android, dependencies are <code>jetty-server</code>
 * and <code>jetty-servlet</code> Maven modules.
 * </p>
 *
 * @author Christian Bauer
 */
public class JettyServletContainer extends Server implements ServletContainerAdapter {

    final private static Logger log = Logger.getLogger(JettyServletContainer.class.getName());
    
    // Singleton with its own QueuedThreadPool
    public static final JettyServletContainer INSTANCE = new JettyServletContainer();
    private JettyServletContainer() {
        super();
        setGracefulShutdown(1000); // Let's wait a second for ongoing transfers to complete
    }

    @Override
    synchronized public int addConnector(String host, int port) throws IOException {
        SocketConnector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);
        addConnector(connector);

        // Open immediately so we can get the assigned local port
        connector.open();
        return connector.getLocalPort();
    }

    @Override
    synchronized public void registerServlet(String contextPath, Servlet servlet) {
        if (getHandler() != null) {
            return;
        }
        log.info("Registering UPnP servlet under context path: " + contextPath);
        ServletContextHandler servletHandler =
            new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        if (contextPath != null && contextPath.length() > 0)
            servletHandler.setContextPath(contextPath);
        ServletHolder s = new ServletHolder(servlet);
        servletHandler.addServlet(s, "/*");
        setHandler(servletHandler);
    }

    @Override
    synchronized public void startIfNotRunning() {
        if (!isStarted() && !isStarting()) {
            log.info("Starting Jetty server... ");
            try {
                start();
            } catch (Exception ex) {
                log.severe("Couldn't start Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    synchronized public void stopIfRunning() {
        if (!isStopped() && !isStopping()) {
            log.info("Stopping Jetty server...");
            try {
                stop();
            } catch (Exception ex) {
                log.severe("Couldn't stop Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

}
