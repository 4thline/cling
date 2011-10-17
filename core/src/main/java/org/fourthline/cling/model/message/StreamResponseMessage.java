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

import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.seamless.util.MimeType;

/**
 * A TCP (HTTP) stream response message.
 *
 * @author Christian Bauer
 */
public class StreamResponseMessage extends UpnpMessage<UpnpResponse> {

    public StreamResponseMessage(StreamResponseMessage source) {
        super(source);
    }

    public StreamResponseMessage(UpnpResponse.Status status) {
        super(new UpnpResponse(status));
    }

    public StreamResponseMessage(UpnpResponse operation) {
        super(operation);
    }


    public StreamResponseMessage(UpnpResponse operation, String body) {
        super(operation, BodyType.STRING, body);
    }

    public StreamResponseMessage(String body) {
        super(new UpnpResponse(UpnpResponse.Status.OK),BodyType.STRING, body);
    }


    public StreamResponseMessage(UpnpResponse operation, byte[] body) {
        super(operation, BodyType.BYTES, body);
    }

    public StreamResponseMessage(byte[] body) {
        super(new UpnpResponse(UpnpResponse.Status.OK),BodyType.BYTES, body);
    }


    public StreamResponseMessage(String body, ContentTypeHeader contentType) {
        this(body);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }

    public StreamResponseMessage(String body, MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }

    public StreamResponseMessage(byte[] body, ContentTypeHeader contentType) {
        this(body);
        getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, contentType);
    }

    public StreamResponseMessage(byte[] body, MimeType mimeType) {
        this(body, new ContentTypeHeader(mimeType));
    }

}