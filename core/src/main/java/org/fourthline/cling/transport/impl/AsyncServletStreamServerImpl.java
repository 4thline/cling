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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;

import javax.servlet.AsyncContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation based on Servlet 3.0 API.
 *
 * @author Christian Bauer
 */
public class AsyncServletStreamServerImpl implements StreamServer<AsyncServletStreamServerConfigurationImpl> {

    final private static Logger log = Logger.getLogger(StreamServer.class.getName());

    final protected AsyncServletStreamServerConfigurationImpl configuration;
    protected int localPort;

    public AsyncServletStreamServerImpl(AsyncServletStreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public AsyncServletStreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    synchronized public void init(InetAddress bindAddress, final Router router) throws InitializationException {
        try {
            log.info("Adding connector: " + bindAddress + ":" + getConfiguration().getListenPort());
            localPort = getConfiguration().getServletContainerAdapter().addConnector(
                bindAddress.getHostAddress(),
                getConfiguration().getListenPort()
            );

            String contextPath = router.getConfiguration().getNamespace().getBasePath().getPath();
            getConfiguration().getServletContainerAdapter().registerServlet(contextPath, createServlet(router));

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }

    synchronized public int getPort() {
        return this.localPort;
    }

    synchronized public void stop() {
        getConfiguration().getServletContainerAdapter().stopIfRunning();
    }

    public void run() {
        getConfiguration().getServletContainerAdapter().startIfNotRunning();
    }

    protected Servlet createServlet(final Router router) {
        return new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                if (log.isLoggable(Level.FINE))
                    log.fine(
                        "Handling Servlet request asynchronously: " + req
                    );

                AsyncContext async = req.startAsync();
                async.setTimeout(getConfiguration().getAsyncTimeoutMillis());

                AsyncServletUpnpStream stream =
                    new AsyncServletUpnpStream(router.getProtocolFactory(), async, req);

                async.addListener(stream);

                router.received(stream);
            }
        };
    }
}
