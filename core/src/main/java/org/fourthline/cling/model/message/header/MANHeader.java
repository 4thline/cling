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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Christian Bauer
 */
public class MANHeader extends UpnpHeader<String> {

    public static final Pattern PATTERN = Pattern.compile("\"(.+?)\"(;.+?)??");
    public static final Pattern NAMESPACE_PATTERN = Pattern.compile(";\\s?ns\\s?=\\s?([0-9]{2})");

    public String namespace;

    public MANHeader() {
    }

    public MANHeader(String value) {
        setValue(value);
    }

    public MANHeader(String value, String namespace) {
        this(value);
        this.namespace = namespace;
    }

    public void setString(String s) throws InvalidHeaderException {

        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches()) {
            setValue(matcher.group(1));

            if (matcher.group(2) != null) {
                Matcher nsMatcher = NAMESPACE_PATTERN.matcher(matcher.group(2));
                if (nsMatcher.matches()) {
                    setNamespace(nsMatcher.group(1));
                } else {
                    throw new InvalidHeaderException("Invalid namespace in MAN header value: " + s);
                }
            }

        } else {
            throw new InvalidHeaderException("Invalid MAN header value: " + s);
        }
    }

    public String getString() {
        if (getValue() == null) return null;
        StringBuilder s = new StringBuilder();
        s.append("\"").append(getValue()).append("\"");
        if (getNamespace() != null) s.append("; ns=").append(getNamespace());
        return s.toString();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
