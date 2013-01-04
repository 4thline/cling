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

package org.fourthline.cling.transport.impl.apache;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

/**
 * This is how you add new HTTP methods to Apache HTTP Components.
 * <p>
 * Yes, Oleg dude, make stuff private! Good idea!
 *</p>
 * @author Christian Bauer
 */
public class UpnpHttpRequestFactory extends DefaultHttpRequestFactory {

    private static final String[] BASIC = {
            "SUBSCRIBE",
            "UNSUBSCRIBE"
    };

    private static final String[] WITH_ENTITY = {
            "NOTIFY"
    };

    public UpnpHttpRequestFactory() {
        super();
    }

    private static boolean isOneOf(final String[] methods, final String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    public HttpRequest newHttpRequest(final RequestLine requestline)
            throws MethodNotSupportedException {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        String method = requestline.getMethod();
        String uri = requestline.getUri();
        return newHttpRequest(method, uri);
    }

    public HttpRequest newHttpRequest(final String method, final String uri) throws MethodNotSupportedException {
        if (isOneOf(BASIC, method)) {
            return new BasicHttpRequest(method, uri);
        } else if (isOneOf(WITH_ENTITY, method)) {
            return new BasicHttpEntityEnclosingRequest(method, uri);
        } else {
            return super.newHttpRequest(method, uri);
        }
    }

}
