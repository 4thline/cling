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

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * A request message, with a method (GET, POST, NOTIFY, etc).
 *
 * @author Christian Bauer
 */
public class UpnpRequest extends UpnpOperation {

    public static enum Method {

        GET("GET"),
        POST("POST"),
        NOTIFY("NOTIFY"),
        MSEARCH("M-SEARCH"),
        SUBSCRIBE("SUBSCRIBE"),
        UNSUBSCRIBE("UNSUBSCRIBE"),
        UNKNOWN("UNKNOWN");

        private static Map<String, Method> byName = new HashMap<String, Method>() {{
            for (Method m : Method.values()) {
                put(m.getHttpName(), m);
            }
        }};

        private String httpName;

        Method(String httpName) {
            this.httpName = httpName;
        }

        public String getHttpName() {
            return httpName;
        }

        public static Method getByHttpName(String httpName) {
            if (httpName == null) return UNKNOWN;
        	Method m = byName.get(httpName.toUpperCase(Locale.ROOT));
            return m != null ? m : UNKNOWN;
        }
    }

    private Method method;
    private URI uri;

    public UpnpRequest(Method method) {
        this.method = method;
    }

    public UpnpRequest(Method method, URI uri) {
        this.method = method;
        this.uri = uri;
    }

    public UpnpRequest(Method method, URL url) {
        this.method = method;
        try {
            if (url != null) {
                this.uri = url.toURI();
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getHttpMethodName() {
        return method.getHttpName();
    }

    public URI getURI() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return getHttpMethodName() + (getURI() != null ? " " + getURI() : "");
    }
}
