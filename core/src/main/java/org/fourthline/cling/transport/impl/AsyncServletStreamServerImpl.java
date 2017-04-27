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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    protected String hostAddress;

    public AsyncServletStreamServerImpl(AsyncServletStreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public AsyncServletStreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    synchronized public void init(InetAddress bindAddress, final Router router) throws InitializationException {
        try {
            if (log.isLoggable(Level.FINE))
                log.fine("Setting executor service on servlet container adapter");
            getConfiguration().getServletContainerAdapter().setExecutorService(
                router.getConfiguration().getStreamServerExecutorService()
            );

            if (log.isLoggable(Level.FINE))
                log.fine("Adding connector: " + bindAddress + ":" + getConfiguration().getListenPort());
            hostAddress = bindAddress.getHostAddress();
            localPort = getConfiguration().getServletContainerAdapter().addConnector(
                hostAddress,
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
        getConfiguration().getServletContainerAdapter().removeConnector(hostAddress, localPort);
    }

    public void run() {
        getConfiguration().getServletContainerAdapter().startIfNotRunning();
    }

    private int mCounter = 0;

    protected Servlet createServlet(final Router router) {
        return new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            	final long startTime = System.currentTimeMillis();
            	final int counter = mCounter++;
                if (log.isLoggable(Level.FINE))
                	log.fine(String.format("HttpServlet.service(): id: %3d, request URI: %s", counter, req.getRequestURI()));

                AsyncContext async = req.startAsync();
                async.setTimeout(getConfiguration().getAsyncTimeoutSeconds()*1000);

                async.addListener(new AsyncListener() {

                    @Override
                    public void onTimeout(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isLoggable(Level.FINE))
                            log.fine(String.format("AsyncListener.onTimeout(): id: %3d, duration: %,4d, request: %s", counter, duration, arg0.getSuppliedRequest()));
                    }


                    @Override
                    public void onStartAsync(AsyncEvent arg0) throws IOException {
                        if (log.isLoggable(Level.FINE))
                            log.fine(String.format("AsyncListener.onStartAsync(): id: %3d, request: %s", counter, arg0.getSuppliedRequest()));
                    }


                    @Override
                    public void onError(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isLoggable(Level.FINE))
                            log.fine(String.format("AsyncListener.onError(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                    }


                    @Override
                    public void onComplete(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isLoggable(Level.FINE))
                            log.fine(String.format("AsyncListener.onComplete(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                    }

                });

                AsyncServletUpnpStream stream =
                    new AsyncServletUpnpStream(router.getProtocolFactory(), async, req) {
                        @Override
                        protected Connection createConnection() {
                            return new AsyncServletConnection(getRequest());
                        }
                    };

                router.received(stream);
            }
        };
    }

    /**
     * Override this method if you can check, at a low level, if the client connection is still open
     * for the given request. This will likely require access to proprietary APIs of your servlet
     * container to obtain the socket/channel for the given request.
     *
     * @return By default <code>true</code>.
     */
    protected boolean isConnectionOpen(HttpServletRequest request) {
        return true;
    }

    protected class AsyncServletConnection implements Connection {

        protected HttpServletRequest request;

        public AsyncServletConnection(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        @Override
        public boolean isOpen() {
            return AsyncServletStreamServerImpl.this.isConnectionOpen(getRequest());
        }

        @Override
        public InetAddress getRemoteAddress() {
            try {
                return InetAddress.getByName(getRequest().getRemoteAddr());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public InetAddress getLocalAddress() {
            try {
                return InetAddress.getByName(getRequest().getLocalAddr());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
