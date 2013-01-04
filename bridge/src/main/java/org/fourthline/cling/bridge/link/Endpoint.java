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

package org.fourthline.cling.bridge.link;

import org.fourthline.cling.bridge.auth.AuthCredentials;

import java.net.URL;

/**
 * @author Christian Bauer
 */
public class Endpoint {

    final protected String id;
    final protected URL callback;
    final AuthCredentials credentials;
    final boolean localOrigin;

    public Endpoint(String id, URL callback, boolean localOrigin, AuthCredentials credentials) {
        this.id = id;
        this.callback = callback;
        this.credentials = credentials;
        this.localOrigin = localOrigin;
    }

    public String getId() {
        return id;
    }

    public URL getCallback() {
        return callback;
    }

    public AuthCredentials getCredentials() {
        return credentials;
    }

    public String getCallbackString() {
        String callbackURL = getCallback().toString();
        return (callbackURL.endsWith("/") ? callbackURL.substring(0, callbackURL.length()-1) : callbackURL);
    }

    public boolean isLocalOrigin() {
        return localOrigin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        if (localOrigin != endpoint.localOrigin) return false;
        if (!callback.equals(endpoint.callback)) return false;
        if (!id.equals(endpoint.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + callback.hashCode();
        result = 31 * result + (localOrigin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") ID: " + getId() + ", local origin: " + isLocalOrigin() + ", callback: " + getCallback() + ", credentials: " + getCredentials();
    }
}
