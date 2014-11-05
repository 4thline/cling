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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.MimeType;

/**
 * Encaspulates a MIME type (content format) and transport, protocol, additional information.
 *
 * @author Christian Bauer
 */
public class ProtocolInfo {

    public static final String WILDCARD = "*";

    protected Protocol protocol = Protocol.ALL;
    protected String network = WILDCARD;
    protected String contentFormat = WILDCARD;
    protected String additionalInfo = WILDCARD;

    public ProtocolInfo(String s) throws InvalidValueException {
        if (s == null) throw new NullPointerException();
        s = s.trim();
        String[] split = s.split(":");
        if (split.length != 4) {
            throw new InvalidValueException("Can't parse ProtocolInfo string: " + s);
        }
        this.protocol = Protocol.value(split[0]);
        this.network = split[1];
        this.contentFormat = split[2];
        this.additionalInfo = split[3];
    }

    public ProtocolInfo(MimeType contentFormatMimeType) {
        this.protocol = Protocol.HTTP_GET;
        this.contentFormat = contentFormatMimeType.toString();
    }

    public ProtocolInfo(Protocol protocol, String network, String contentFormat, String additionalInfo) {
        this.protocol = protocol;
        this.network = network;
        this.contentFormat = contentFormat;
        this.additionalInfo = additionalInfo;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getNetwork() {
        return network;
    }

    public String getContentFormat() {
        return contentFormat;
    }

    public MimeType getContentFormatMimeType() throws IllegalArgumentException {
        return MimeType.valueOf(contentFormat);
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolInfo that = (ProtocolInfo) o;

        if (!additionalInfo.equals(that.additionalInfo)) return false;
        if (!contentFormat.equals(that.contentFormat)) return false;
        if (!network.equals(that.network)) return false;
        if (protocol != that.protocol) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = protocol.hashCode();
        result = 31 * result + network.hashCode();
        result = 31 * result + contentFormat.hashCode();
        result = 31 * result + additionalInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return protocol.toString() + ":" +
                network + ":" +
                contentFormat + ":" +
                additionalInfo;

    }
}
