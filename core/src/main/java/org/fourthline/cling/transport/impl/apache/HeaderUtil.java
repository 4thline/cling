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
