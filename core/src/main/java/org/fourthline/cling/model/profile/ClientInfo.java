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

package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.UserAgentHeader;

import java.net.InetAddress;

/**
 * Encapsulates information about a remote control point, the client.
 *
 * <p>
 * The {@link #getExtraResponseHeaders()} method offers modifiable HTTP headers which will
 * be added to the responses and returned to the client.
 * </p>
 *
 * @author Christian Bauer
 */
public class ClientInfo {

    final protected InetAddress remoteAddress;
    final protected InetAddress localAddress;
    final protected UpnpHeaders requestHeaders;
    final protected UpnpHeaders extraResponseHeaders = new UpnpHeaders();

    public ClientInfo() {
        this(null);
    }

    public ClientInfo(StreamRequestMessage requestMessage) {
        this(requestMessage != null ? requestMessage.getRemoteAddress() : null,
            requestMessage != null ? requestMessage.getLocalAddress() : null,
            requestMessage != null ? requestMessage.getHeaders() : new UpnpHeaders());
    }

    public ClientInfo(InetAddress remoteAddress, InetAddress localAddress, UpnpHeaders requestHeaders) {
        this.remoteAddress = remoteAddress;
        this.localAddress= localAddress;
        this.requestHeaders = requestHeaders;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public UpnpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public UpnpHeaders getExtraResponseHeaders() {
        return extraResponseHeaders;
    }

    public String getRequestUserAgent() {
        if (getRequestHeaders() == null)
            return null;
        UserAgentHeader header = getRequestHeaders().getFirstHeader(
            UpnpHeader.Type.USER_AGENT, UserAgentHeader.class
        );
        return header != null ? header.getValue() : null;
    }

    public void setResponseUserAgent(UserAgentHeader userAgentHeader) {
        getExtraResponseHeaders().add(
            UpnpHeader.Type.USER_AGENT,
            userAgentHeader
        );
    }

    public void setResponseUserAgent(String userAgent) {
        setResponseUserAgent(new UserAgentHeader(userAgent));
    }

    // TODO: Remove this once we know how ClientProfile will look like
    public boolean isWMPRequest() {
   		String userAgent = getRequestHeaders().getFirstHeader("User-Agent");
        return userAgent != null
            && userAgent.contains("Windows-Media-Player")
            && !userAgent.contains("J-River");
    }

    public boolean isXbox360Request() {
        String userAgent = getRequestHeaders().getFirstHeader("User-Agent");
        String server = getRequestHeaders().getFirstHeader("Server");
        return (userAgent != null && (userAgent.contains("Xbox") || userAgent.contains("Xenon")))
            || (server != null && server.contains("Xbox"));
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Address: " + getRemoteAddress();
    }
}
