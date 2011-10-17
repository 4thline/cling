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

import org.fourthline.cling.transport.spi.DatagramIOConfiguration;

/**
 * Settings for the default implementation.
 *
 * @author Christian Bauer
 */
public class DatagramIOConfigurationImpl implements DatagramIOConfiguration {

    private int timeToLive = 4;
    private int maxDatagramBytes = 640;

    /**
     * Defaults to TTL of '4' and maximum datagram size of 640 bytes (512 per UDA 1.0, 128 byte header).
     */
    public DatagramIOConfigurationImpl() {
    }

    public DatagramIOConfigurationImpl(int timeToLive, int maxDatagramBytes) {
        this.timeToLive = timeToLive;
        this.maxDatagramBytes = maxDatagramBytes;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public int getMaxDatagramBytes() {
        return maxDatagramBytes;
    }

    public void setMaxDatagramBytes(int maxDatagramBytes) {
        this.maxDatagramBytes = maxDatagramBytes;
    }
}