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

import org.fourthline.cling.transport.spi.StreamServerConfiguration;

/**
 * Settings for the Apache HTTP Components implementation.
 *
 * @author Christian Bauer
 */
public class StreamServerConfigurationImpl implements StreamServerConfiguration {

    private int listenPort = 0;
    private int dataWaitTimeoutSeconds = 5;
    private int bufferSizeKilobytes = 8;
    // TODO: This seems to be only relevant for HTTP clients, no?
    private boolean staleConnectionCheck = true;
    private boolean tcpNoDelay = true;
    private int tcpConnectionBacklog = 0;

    /**
     * Defaults to port '0', ephemeral.
     */
    public StreamServerConfigurationImpl() {
    }

    public StreamServerConfigurationImpl(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * Defines the socket timeout (SO_TIMEOUT) in seconds, which is the timeout for waiting
     * for data. Defaults to 5 seconds.
     */
    public int getDataWaitTimeoutSeconds() {
        return dataWaitTimeoutSeconds;
    }

    public void setDataWaitTimeoutSeconds(int dataWaitTimeoutSeconds) {
        this.dataWaitTimeoutSeconds = dataWaitTimeoutSeconds;
    }

    /**
     * Determines the size of the internal socket buffer used to buffer data while
     * receiving/transmitting HTTP messages. Defaults to 8 kilobytes.
     */
    public int getBufferSizeKilobytes() {
        return bufferSizeKilobytes;
    }

    public void setBufferSizeKilobytes(int bufferSizeKilobytes) {
        this.bufferSizeKilobytes = bufferSizeKilobytes;
    }

    /**
     * Determines whether stale connection check is to be used. Disabling stale connection
     * check may result in slight performance improvement at the risk of getting an I/O
     * error when executing a request over a connection that has been closed at the server
     * side. Defaults to <code>true</code>.
     */
    public boolean isStaleConnectionCheck() {
        return staleConnectionCheck;
    }

    public void setStaleConnectionCheck(boolean staleConnectionCheck) {
        this.staleConnectionCheck = staleConnectionCheck;
    }

    /**
     * Determines whether Nagle's algorithm is to be used.  Defaults to <code>true</code>.
     */
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    /**
     * This is the maximum number of queued incoming connections to allow on the listening socket.
     * Queued TCP connections exceeding this limit may be rejected by the TCP implementation.
     * @return The number of queued connections, defaults to system default.
     */
    public int getTcpConnectionBacklog() {
        return tcpConnectionBacklog;
    }

    public void setTcpConnectionBacklog(int tcpConnectionBacklog) {
        this.tcpConnectionBacklog = tcpConnectionBacklog;
    }



}
