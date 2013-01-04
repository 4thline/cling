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
 * A singleton wrapper of a <code>org.eclipse.jetty.server.Server</code>.
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
public class JettyServletContainer implements ServletContainerAdapter {

    final private static Logger log = Logger.getLogger(JettyServletContainer.class.getName());

    // Singleton
    public static final JettyServletContainer INSTANCE = new JettyServletContainer();
    private JettyServletContainer() {
        resetServer();
    }

    protected Server server;

    @Override
    synchronized public int addConnector(String host, int port) throws IOException {
        SocketConnector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);

        // Open immediately so we can get the assigned local port
        connector.open();

        // Only add if open() succeeded
        server.addConnector(connector);

        return connector.getLocalPort();
    }

    @Override
    synchronized public void registerServlet(String contextPath, Servlet servlet) {
        if (server.getHandler() != null) {
            return;
        }
        log.info("Registering UPnP servlet under context path: " + contextPath);
        ServletContextHandler servletHandler =
            new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        if (contextPath != null && contextPath.length() > 0)
            servletHandler.setContextPath(contextPath);
        ServletHolder s = new ServletHolder(servlet);
        servletHandler.addServlet(s, "/*");
        server.setHandler(servletHandler);
    }

    @Override
    synchronized public void startIfNotRunning() {
        if (!server.isStarted() && !server.isStarting()) {
            log.info("Starting Jetty server... ");
            try {
                server.start();
            } catch (Exception ex) {
                log.severe("Couldn't start Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    synchronized public void stopIfRunning() {
        if (!server.isStopped() && !server.isStopping()) {
            log.info("Stopping Jetty server...");
            try {
                server.stop();
            } catch (Exception ex) {
                log.severe("Couldn't stop Jetty server: " + ex);
                throw new RuntimeException(ex);
            } finally {
                resetServer();
            }
        }
    }

    protected void resetServer() {
        server = new Server(); // Has its own QueuedThreadPool
        server.setGracefulShutdown(1000); // Let's wait a second for ongoing transfers to complete
    }

}
