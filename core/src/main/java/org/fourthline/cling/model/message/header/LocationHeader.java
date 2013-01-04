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

package org.fourthline.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO: UDA 1.1 says it should be RfC 3986 compatible.
 *
 * <p>See http://blog.jclark.com/2008/11/what-allowed-in-uri.html</p>
 *
 * @author Christian Bauer
 */
public class LocationHeader extends UpnpHeader<URL> {

    public LocationHeader() {
    }

    public LocationHeader(URL value) {
        setValue(value);
    }

    public LocationHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        try {
            URL url = new URL(s);
            setValue(url);
        } catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Invalid URI: " + ex.getMessage());
        }
    }

    public String getString() {
        return getValue().toString();
    }
}
