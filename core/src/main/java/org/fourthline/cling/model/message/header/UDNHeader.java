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

import org.fourthline.cling.model.types.UDN;

/**
 * @author Christian Bauer
 */
public class UDNHeader extends UpnpHeader<UDN> {

    public UDNHeader() {
    }

    public UDNHeader(UDN udn) {
        setValue(udn);
    }

    public void setString(String s) throws InvalidHeaderException {
        if (!s.startsWith(UDN.PREFIX)) {
            throw new InvalidHeaderException("Invalid UDA header value, must start with '"+UDN.PREFIX+"': " + s);
        }

        if (s.contains("::urn")) {
            throw new InvalidHeaderException("Invalid UDA header value, must not contain '::urn': " + s);
        }

        UDN udn = new UDN( s.substring(UDN.PREFIX.length()) );
        setValue(udn);
    }

    public String getString() {
        return getValue().toString();
    }
}

