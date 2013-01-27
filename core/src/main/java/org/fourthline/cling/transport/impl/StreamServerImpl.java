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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * Implementation based on the built-in SUN JDK 6.0 HTTP Server.
 * <p>
 * See <a href="http://download.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/index.html?com/sun/net/httpserver/HttpServer.html">the
 * documentation of the SUN JDK 6.0 HTTP Server</a>.
 * </p>
 * <p>
 * This implementation <em>DOES NOT WORK</em> on Android. Read the Cling manual for
 * alternatives for Android.
 * </p>
 * <p>
 * This implementation does not support connection alive checking, as we can't send
 * heartbeats to the client. We don't have access to the raw socket with the Sun API.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {

    private static Logger log = Logger.getLogger(StreamServer.class.getName());

    final protected StreamServerConfigurationImpl configuration;
    protected HttpServer server;

    public StreamServerImpl(StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    synchronized public void init(InetAddress bindAddress, Router router) throws InitializationException {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, configuration.getListenPort());

            server = HttpServer.create(socketAddress, configuration.getTcpConnectionBacklog());
            server.createContext("/", new RequestHttpHandler(router));

            log.info("Created server (for receiving TCP streams) on: " + server.getAddress());

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }

    synchronized public int getPort() {
        return server.getAddress().getPort();
    }

    public StreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    synchronized public void run() {
        log.fine("Starting StreamServer...");
        // Starts a new thread but inherits the properties of the calling thread
        server.start();
    }

    synchronized public void stop() {
        log.fine("Stopping StreamServer...");
        if (server != null) server.stop(1);
    }

    protected class RequestHttpHandler implements HttpHandler {

        private final Router router;

        public RequestHttpHandler(Router router) {
            this.router = router;
        }

        // This is executed in the request receiving thread!
        public void handle(final HttpExchange httpExchange) throws IOException {
            // And we pass control to the service, which will (hopefully) start a new thread immediately so we can
            // continue the receiving thread ASAP
            log.fine("Received HTTP exchange: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
            router.received(
                new HttpExchangeUpnpStream(router.getProtocolFactory(), httpExchange) {
                    @Override
                    protected Connection createConnection() {
                        return new HttpServerConnection(httpExchange);
                    }
                }
            );
        }
    }

    /**
     * Logs a warning and returns <code>true</code>, we can't access the socket using the awful JDK webserver API.
     * <p>
     * Override this method if you know how to do it.
     * </p>
     */
    protected boolean isConnectionOpen(HttpExchange exchange) {
        log.warning("Can't check client connection, socket access impossible on JDK webserver!");
        return true;
    }

    protected class HttpServerConnection implements Connection {

        protected HttpExchange exchange;

        public HttpServerConnection(HttpExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public boolean isOpen() {
            return isConnectionOpen(exchange);
        }

        @Override
        public InetAddress getRemoteAddress() {
            return exchange.getRemoteAddress() != null
                ? exchange.getRemoteAddress().getAddress()
                : null;
        }

        @Override
        public InetAddress getLocalAddress() {
            return exchange.getLocalAddress() != null
                ? exchange.getLocalAddress().getAddress()
                : null;
        }
    }
}
