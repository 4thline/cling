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

package org.fourthline.cling.model.message;

import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

import java.io.UnsupportedEncodingException;

/**
 * A non-streaming message, the interface between the transport layer and the protocols.
 * <p>
 * Defaults to UDA version 1.0 and a string body type. Message content is not streamed,
 * it is always read into memory and transported as a string or bytes message body.
 * </p>
 * <p>
 * Subtypes of this class typically implement the integrity rules for individual UPnP
 * messages, for example, what headers a particular message requires.
 * </p>
 * <p>
 * Messages are not thread-safe.
 * </p>
 * 
 * @author Christian Bauer
 */
public abstract class UpnpMessage<O extends UpnpOperation> {

    public static enum BodyType {
        STRING, BYTES
    }

    private int udaMajorVersion = 1;
    private int udaMinorVersion = 0;

    private O operation;
    private UpnpHeaders headers = new UpnpHeaders();
    private Object body;
    private BodyType bodyType = BodyType.STRING;

    protected UpnpMessage(UpnpMessage<O> source) {
        this.operation = source.getOperation();
        this.headers = source.getHeaders();
        this.body = source.getBody();
        this.bodyType = source.getBodyType();
        this.udaMajorVersion = source.getUdaMajorVersion();
        this.udaMinorVersion = source.getUdaMinorVersion();
    }

    protected UpnpMessage(O operation) {
        this.operation = operation;
    }

    protected UpnpMessage(O operation, BodyType bodyType, Object body) {
        this.operation = operation;
        this.bodyType = bodyType;
        this.body = body;
    }

    public int getUdaMajorVersion() {
        return udaMajorVersion;
    }

    public void setUdaMajorVersion(int udaMajorVersion) {
        this.udaMajorVersion = udaMajorVersion;
    }

    public int getUdaMinorVersion() {
        return udaMinorVersion;
    }

    public void setUdaMinorVersion(int udaMinorVersion) {
        this.udaMinorVersion = udaMinorVersion;
    }

    public UpnpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(UpnpHeaders headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(String string) {
        this.bodyType = BodyType.STRING;
        this.body = string;
    }

    public void setBody(BodyType bodyType, Object body) {
        this.bodyType = bodyType;
        this.body = body;
    }

    public void setBodyCharacters(byte[] characterData) throws UnsupportedEncodingException {
        setBody(
                UpnpMessage.BodyType.STRING,
                new String(
                        characterData,
                        getContentTypeCharset() != null
                                ? getContentTypeCharset()
                                : "UTF-8"
                )
        );
    }

    public boolean hasBody() {
        return getBody() != null;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getBodyString() {
        try {
                if(!hasBody()) {
                    return null;
                }
                if(getBodyType().equals(BodyType.STRING)) {
                    String body = ((String) getBody());
                    if(body.charAt(0) == '\ufeff') { /* utf8 BOM */
                        body = body.substring(1);
                    }
                    return body;
                } else {
                    return new String((byte[]) getBody(), "UTF-8");
                }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] getBodyBytes() {
        try {
            if(!hasBody()) {
                return null;
            }
            if(getBodyType().equals(BodyType.STRING)) {
                return getBodyString().getBytes();
            } else {
                return (byte[]) getBody();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public O getOperation() {
        return operation;
    }

    public boolean isContentTypeMissingOrText() {
        ContentTypeHeader contentTypeHeader = getContentTypeHeader();
        // This is against the HTTP specification: If there is no content type we MAY assume that
        // the entity body is bytes. However, to support broken UPnP devices which also violate the
        // UPnP spec and do not send any content type at all, we need to assume no content type
        // means a textual entity body is available.
        if (contentTypeHeader == null) return true;
        if (contentTypeHeader.isText()) return true;
        // Only if there was any content-type header and none was text
        return false;
    }

    public ContentTypeHeader getContentTypeHeader() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
    }

    public boolean isContentTypeText() {
        ContentTypeHeader ct = getContentTypeHeader();
        return ct != null && ct.isText();
    }

    public boolean isContentTypeTextUDA() {
        ContentTypeHeader ct = getContentTypeHeader();
        return ct != null && ct.isUDACompliantXML();
    }

    public String getContentTypeCharset() {
        ContentTypeHeader ct = getContentTypeHeader();
        return ct != null ? ct.getValue().getParameters().get("charset") : null;
    }

    public boolean hasHostHeader() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.HOST) != null;
    }

    public boolean isBodyNonEmptyString() {
        return hasBody()
            && getBodyType().equals(UpnpMessage.BodyType.STRING)
            && getBodyString().length() > 0;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") " + getOperation().toString();
    }
}