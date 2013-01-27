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
     * <p>
     * Check if the remote client's connection is still open.
     * </p>
     * <p>
     * How connection checking is actually performed is transport-implementation dependent. Usually,
     * the {@link org.fourthline.cling.transport.spi.StreamServer} will send meaningless heartbeat
     * data to the client on its (open) socket. If that fails, the client's connection has been
     * closed. Note that some HTTP clients can <em>NOT</em> handle such garbage data in HTTP
     * responses, hence calling this method might cause compatibility issues.
     * </p>
     * @return <code>true</code> if the remote client's connection was closed.
     */
    public boolean isRequestCancelled() {
        return !getConnection().isOpen();
    }

    /**
     * @throws InterruptedException if {@link #isRequestCancelled()} returns <code>true</code>.
     */
    public void throwIfRequestCancelled() throws InterruptedException{
        if(isRequestCancelled())
             throw new InterruptedException("Client's request cancelled");
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
