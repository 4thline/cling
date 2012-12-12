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
        	Method m = byName.get(httpName.toUpperCase(Locale.ENGLISH));
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
