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

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.types.HostPort;

/**
 * @author Christian Bauer
 */
public class HostHeader extends UpnpHeader<HostPort> {

    int port = Constants.UPNP_MULTICAST_PORT;
    String group = Constants.IPV4_UPNP_MULTICAST_GROUP;

    public HostHeader() {
        setValue(new HostPort(group, port));
    }

    public HostHeader(int port) {
        setValue(new HostPort(group, port));
    }

    public HostHeader(String host, int port) {
        setValue(new HostPort(host, port));
    }

    public void setString(String s) throws InvalidHeaderException {
        // UDA 1.1/1.0 section 1.2.2
        if (s.contains(":")) {
            // We have a port in the header, so we have to use that instead of the UDA default
            try {
                this.port = Integer.valueOf(s.substring(s.indexOf(":")+1));
                this.group = s.substring(0, s.indexOf(":"));
                setValue(new HostPort(group, port));
            } catch (NumberFormatException ex) {
                throw new InvalidHeaderException("Invalid HOST header value, can't parse port: " + s + " - " + ex.getMessage());
            }
        } else {
            this.group = s;
            setValue(new HostPort(group, port));
        }
    }

    public String getString() {
        return getValue().toString();
    }
}