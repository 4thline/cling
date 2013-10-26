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

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.Constants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Service type with a fixed <code>schemas-upnp-org</code> namespace.
 *
 * @author Christian Bauer
 */
public class UDAServiceType extends ServiceType {

    public static final String DEFAULT_NAMESPACE = "schemas-upnp-org";

    // This pattern also accepts decimal versions, not only integers (as would be required by UDA), but cuts off fractions
    public static final Pattern PATTERN =
            Pattern.compile("urn:" + DEFAULT_NAMESPACE + ":service:(" + Constants.REGEX_TYPE + "):([0-9]+).*");

    public UDAServiceType(String type) {
        this(type, 1);
    }

    public UDAServiceType(String type, int version) {
        super(DEFAULT_NAMESPACE, type, version);
    }

    public static UDAServiceType valueOf(String s) throws InvalidValueException {
        Matcher matcher = UDAServiceType.PATTERN.matcher(s);

        try {
            if (matcher.matches())
                return new UDAServiceType(matcher.group(1), Integer.valueOf(matcher.group(2)));
        } catch (RuntimeException e) {
            throw new InvalidValueException(String.format(
                "Can't parse UDA service type string (namespace/type/version) '%s': %s", s, e.toString()));
        }
        throw new InvalidValueException("Can't parse UDA service type string (namespace/type/version): " + s);
    }

}
