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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.StreamServerConfiguration;

/**
 * Settings for the default implementation.
 *
 * @author Christian Bauer
 */
public class StreamServerConfigurationImpl implements StreamServerConfiguration {

    private int listenPort;
    private int tcpConnectionBacklog;

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
     * @return Maximum number of queued incoming connections to allow on the listening socket,
     *         default is system default.
     */
    public int getTcpConnectionBacklog() {
        return tcpConnectionBacklog;
    }

    public void setTcpConnectionBacklog(int tcpConnectionBacklog) {
        this.tcpConnectionBacklog = tcpConnectionBacklog;
    }

}