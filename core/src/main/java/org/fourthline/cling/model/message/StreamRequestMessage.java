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

package org.fourthline.cling.model.message;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;

/**
 * A TCP (HTTP) stream request message.
 *
 * @author Christian Bauer
 */
public class StreamRequestMessage extends UpnpMessage<UpnpRequest> {

    protected InetAddress remoteAddress;
    protected InetAddress localAddress;

    public StreamRequestMessage(StreamRequestMessage source) {
        super(source);
        this.remoteAddress = source.getRemoteAddress();
    }

    public StreamRequestMessage(UpnpRequest operation) {
        super(operation);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri) {
        super(new UpnpRequest(method, uri));
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url) {
        super(new UpnpRequest(method, url));
    }

    public StreamRequestMessage(UpnpRequest operation, String body) {
        super(operation, BodyType.STRING, body);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri, String body) {
        super(new UpnpRequest(method, uri), BodyType.STRING, body);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url, String body) {
        super(new UpnpRequest(method, url), BodyType.STRING, body);
    }


    public StreamRequestMessage(UpnpRequest operation, byte[] body) {
        super(operation, BodyType.BYTES, body);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URI uri, byte[] body) {
        super(new UpnpRequest(method, uri), BodyType.BYTES, body);
    }

    public StreamRequestMessage(UpnpRequest.Method method, URL url, byte[] body) {
        super(new UpnpRequest(method, url), BodyType.BYTES, body);
    }

    public URI getUri() {
        return getOperation().getURI();
    }
    
    public void setUri(URI uri) {
        getOperation().setUri(uri);
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(InetAddress localAddress) {
        this.localAddress = localAddress;
    }
}