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

import org.fourthline.cling.transport.spi.StreamClientConfiguration;
import org.fourthline.cling.model.ServerClientTokens;

/**
 * Settings for the default implementation.
 *
 * @author Christian Bauer
 */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {

    private boolean usePersistentConnections = false;
    private int connectionTimeoutSeconds = 20; // WMP can be very slow to connect
    private int dataReadTimeoutSeconds = 60; // WMP can be very slow sending the initial data after connection

    /**
     * Defaults to <code>false</code>, avoiding obscure bugs in the JDK.
     */
    public boolean isUsePersistentConnections() {
        return usePersistentConnections;
    }

    public void setUsePersistentConnections(boolean usePersistentConnections) {
        this.usePersistentConnections = usePersistentConnections;
    }

    /**
     * Defaults to 20 seconds.
     */
    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    /**
     * Defaults to 60 seconds.
     */
    public int getDataReadTimeoutSeconds() {
        return dataReadTimeoutSeconds;
    }

    public void setDataReadTimeoutSeconds(int dataReadTimeoutSeconds) {
        this.dataReadTimeoutSeconds = dataReadTimeoutSeconds;
    }

    /**
     * Defaults to string value of {@link ServerClientTokens}.
     */
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }

}
