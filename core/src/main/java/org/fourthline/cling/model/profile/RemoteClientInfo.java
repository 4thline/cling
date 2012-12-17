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

import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.UserAgentHeader;
import org.seamless.http.RequestInfo;

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
public class RemoteClientInfo extends ClientInfo {

    final protected Connection connection;
    final protected UpnpHeaders extraResponseHeaders = new UpnpHeaders();

    public RemoteClientInfo() {
        this(null);
    }

    public RemoteClientInfo(StreamRequestMessage requestMessage) {
        this(requestMessage != null ? requestMessage.getConnection() : null,
            requestMessage != null ? requestMessage.getHeaders() : new UpnpHeaders());
    }

    public RemoteClientInfo(Connection connection, UpnpHeaders requestHeaders) {
        super(requestHeaders);
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * @return <code>true</code> if the Thread has been interrupted or the client's connection was closed
     */
    public boolean isRequestCancelled() {
        return Thread.interrupted() || !getConnection().isOpen();
    }

    public void throwIfRequestCancelled() throws InterruptedException{
        if(isRequestCancelled())
             throw new InterruptedException();
    }

    public InetAddress getRemoteAddress() {
        return getConnection().getRemoteAddress();
    }

    public InetAddress getLocalAddress() {
        return getConnection().getLocalAddress();
    }

    public UpnpHeaders getExtraResponseHeaders() {
        return extraResponseHeaders;
    }

    public void setResponseUserAgent(String userAgent) {
        setResponseUserAgent(new UserAgentHeader(userAgent));
    }

    public void setResponseUserAgent(UserAgentHeader userAgentHeader) {
        getExtraResponseHeaders().add(
            UpnpHeader.Type.USER_AGENT,
            userAgentHeader
        );
    }

    // TODO: Remove this once we know how ClientProfile will look like
    public boolean isWMPRequest() {
        return RequestInfo.isWMPRequest(getRequestUserAgent());
    }

    public boolean isXbox360Request() {
        return RequestInfo.isXbox360Request(
            getRequestUserAgent(),
            getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.SERVER)
        );
    }

    public boolean isPS3Request() {
    	return RequestInfo.isPS3Request(
            getRequestUserAgent(),
            getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.EXT_AV_CLIENT_INFO)
        );
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Remote Address: " + getRemoteAddress();
    }
}
