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

package org.fourthline.cling.transport.impl.apache;

import org.fourthline.cling.model.ServerClientTokens;
import org.fourthline.cling.transport.spi.StreamClientConfiguration;

/**
 * Settings for the Apache HTTP Components implementation.
 *
 * @author Christian Bauer
 */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {

    private int maxTotalConnections = 1024;
    private int connectionTimeoutSeconds = 5;
    private int dataReadTimeoutSeconds = 30; // WMP can be very slow sending the initial data after connection
    private String contentCharset = "UTF-8"; // UDA spec says it's always UTF-8 entity content

    /**
     * Defaults to 1024.
     */
    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    /**
     * Defaults to 5 seconds;
     */
    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }
    /**
     * Defaults to 30 seconds (WMP can be very slow sending the initial data after connection);
     */
    public int getDataReadTimeoutSeconds() {
        return dataReadTimeoutSeconds;
    }

    public void setDataReadTimeoutSeconds(int dataReadTimeoutSeconds) {
        this.dataReadTimeoutSeconds = dataReadTimeoutSeconds;
    }

    /**
     * @return Character set of textual content, defaults to "UTF-8".
     */
    public String getContentCharset() {
        return contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    /**
     * Defaults to string value of {@link ServerClientTokens}.
     */
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }

    /**
     * If -1, the default value of HttpClient will be used (8192 in httpclient 4.1)
     */
    public int getSocketBufferSize() {
    	return -1; 
    }

	public boolean getStaleCheckingEnabled() {
		return false;
	}

    /**
     * If -1, the default value of HttpClient will be used (3 in httpclient 4.1)
     */
	public int getRequestRetryCount() {
		// the default that is used by DefaultHttpClient if unspecified
		return -1;
	}

}
