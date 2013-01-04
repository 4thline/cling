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

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.seamless.http.Headers;

import java.util.List;
import java.util.Map;

/**
 * Converts from/to Apache HTTP Components header format.
 *
 * @author Christian Bauer
 */
public class HeaderUtil {

    public static void add(HttpMessage httpMessage, Headers headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                httpMessage.addHeader(entry.getKey(), value);
            }
        }
    }

    public static Headers get(HttpMessage httpMessage) {
        Headers headers = new Headers();
        for (Header header : httpMessage.getAllHeaders()) {
            headers.add(header.getName(), header.getValue());
        }
        return headers;
    }

}
