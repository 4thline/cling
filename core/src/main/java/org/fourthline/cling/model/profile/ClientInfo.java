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

package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.UserAgentHeader;

/**
 * Encapsulates information about a (local) client.
 *
 * @author Christian Bauer
 */
public class ClientInfo {

    final protected UpnpHeaders requestHeaders;

    public ClientInfo() {
        this(new UpnpHeaders());
    }

    public ClientInfo(UpnpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public UpnpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestUserAgent() {
        return getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.USER_AGENT);
    }

    public void setRequestUserAgent(String userAgent) {
        getRequestHeaders().add(UpnpHeader.Type.USER_AGENT, new UserAgentHeader(userAgent));
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") User-Agent: " + getRequestUserAgent();
    }
}
