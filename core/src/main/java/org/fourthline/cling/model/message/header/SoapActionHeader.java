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

import org.fourthline.cling.model.types.SoapActionType;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class SoapActionHeader extends UpnpHeader<SoapActionType> {

    public SoapActionHeader() {
    }

    public SoapActionHeader(URI uri) {
        setValue(SoapActionType.valueOf(uri.toString()));
    }

    public SoapActionHeader(SoapActionType value) {
        setValue(value);
    }

    public SoapActionHeader(String s) throws InvalidHeaderException {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        try {
            if (!s.startsWith("\"") && s.endsWith("\"")) {
                throw new InvalidHeaderException("Invalid SOAP action header, must be enclosed in doublequotes:" + s);
            }

            SoapActionType t = SoapActionType.valueOf(s.substring(1, s.length()-1));
            setValue(t);
        } catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid SOAP action header value, " + ex.getMessage());
        }
    }

    public String getString() {
        return "\"" + getValue().toString() + "\"";
    }
}