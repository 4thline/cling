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

package org.fourthline.cling.transport.impl.apache;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.UpnpStream;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation based on <a href="http://hc.apache.org/">Apache HTTP Components 4.2</a>.
 * <p>
 * This implementation <em>DOES NOT WORK</em> on Android. Read the Cling manual for
 * alternatives on Android.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {

    final private static Logger log = Logger.getLogger(StreamServer.class.getName());

    final protected StreamServerConfigurationImpl configuration;

    protected Router router;
    protected ServerSocket serverSocket;
    protected HttpParams globalParams = new BasicHttpParams();
    private volatile boolean stopped = false;

    public StreamServerImpl(StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    public StreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    synchronized public void init(InetAddress bindAddress, Router router) throws InitializationException {

        try {

            this.router = router;

            this.serverSocket =
                    new ServerSocket(
                            configuration.getListenPort(),
                            configuration.getTcpConnectionBacklog(),
                            bindAddress
                    );

            log.info("Created socket (for receiving TCP streams) on: " + serverSocket.getLocalSocketAddress());

            this.globalParams
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, configuration.getDataWaitTimeoutSeconds() * 1000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, configuration.getBufferSizeKilobytes() * 1024)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, configuration.isStaleConnectionCheck())
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, configuration.isTcpNoDelay());

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize "+getClass().getSimpleName()+": " + ex.toString(), ex);
        }

    }

    synchronized public int getPort() {
        return this.serverSocket.getLocalPort();
    }

    synchronized public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            log.fine("Exception closing streaming server socket: " + ex);
        }
    }

    public void run() {

        log.fine("Entering blocking receiving loop, listening for HTTP stream requests on: " + serverSocket.getLocalSocketAddress());
        while (!stopped) {

            try {

                // Block until we have a connection
                final Socket clientSocket = serverSocket.accept();

                // We have to force this fantastic library to accept HTTP methods which are not in the holy RFCs.
                final DefaultHttpServerConnection httpServerConnection = new DefaultHttpServerConnection() {
                    @Override
                    protected HttpRequestFactory createHttpRequestFactory() {
                        return new UpnpHttpRequestFactory();
                    }
                };

                log.fine("Incoming connection from: " + clientSocket.getInetAddress());
                httpServerConnection.bind(clientSocket, globalParams);

                // Wrap the processing of the request in a UpnpStream
                UpnpStream connectionStream =
                        new HttpServerConnectionUpnpStream(
                                router.getProtocolFactory(),
                                httpServerConnection,
                                globalParams
                        ) {
                            @Override
                            protected Connection createConnection() {
                                return new ApacheServerConnection(
                                    clientSocket, httpServerConnection
                                );

                            }
                        };

                router.received(connectionStream);

            } catch (InterruptedIOException ex) {
                log.fine("I/O has been interrupted, stopping receiving loop, bytes transfered: " + ex.bytesTransferred);
                break;
            } catch (SocketException ex) {
                if (!stopped) {
                    // That's not good, could be anything
                    log.fine("Exception using server socket: " + ex.getMessage());
                } else {
                    // Well, it's just been stopped so that's totally fine and expected
                }
                break;
            } catch (IOException ex) {
                log.fine("Exception initializing receiving loop: " + ex.getMessage());
                break;
            }
        }

        try {
            log.fine("Receiving loop stopped");
            if (!serverSocket.isClosed()) {
                log.fine("Closing streaming server socket");
                serverSocket.close();
            }
        } catch (Exception ex) {
            log.info("Exception closing streaming server socket: " + ex.getMessage());
        }

    }

    /**
     * Writes a space character to the output stream of the socket.
     * <p>
     * This space character might confuse the HTTP client. The Cling transports for Jetty Client and
     * Apache HttpClient have been tested to work with space characters. Unfortunately, Sun JDK's
     * HttpURLConnection does not gracefully handle any garbage in the HTTP request!
     * </p>
     */
    protected boolean isConnectionOpen(Socket socket) {
        return isConnectionOpen(socket, " ".getBytes());
    }

    protected boolean isConnectionOpen(Socket socket, byte[] heartbeat) {
        if (log.isLoggable(Level.FINE))
            log.fine("Checking if client connection is still open on: " + socket.getRemoteSocketAddress());
        try {
            socket.getOutputStream().write(heartbeat);
            socket.getOutputStream().flush();
            return true;
        } catch (IOException ex) {
            if (log.isLoggable(Level.FINE))
                log.fine("Client connection has been closed: " + socket.getRemoteSocketAddress());
            return false;
        }
    }

    protected class ApacheServerConnection implements Connection {

        protected Socket socket;
        protected DefaultHttpServerConnection connection;

        public ApacheServerConnection(Socket socket, DefaultHttpServerConnection connection) {
            this.socket = socket;
            this.connection = connection;
        }

        @Override
        public boolean isOpen() {
            return isConnectionOpen(socket);
        }

        @Override
        public InetAddress getRemoteAddress() {
            return connection.getRemoteAddress();
        }

        @Override
        public InetAddress getLocalAddress() {
            return connection.getLocalAddress();
        }
    }

}
