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

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Service identifier with a fixed <code>upnp-org</code> namespace.
 * <p>
 * Also accepts the namespace sometimes used by broken devices, <code>schemas-upnp-org</code>.
 * </p>
 *
 * @author Christian Bauer
 */
public class UDAServiceId extends ServiceId {
	
	private static Logger log = Logger.getLogger(UDAServiceId.class.getName());

    public static final String DEFAULT_NAMESPACE = "upnp-org";
    public static final String BROKEN_DEFAULT_NAMESPACE = "schemas-upnp-org"; // TODO: UPNP VIOLATION: Intel UPnP tools!

    public static final Pattern PATTERN =
            Pattern.compile("urn:" + DEFAULT_NAMESPACE + ":serviceId:(" + Constants.REGEX_ID+ ")");

     // Note: 'service' vs. 'serviceId'
    public static final Pattern BROKEN_PATTERN =
            Pattern.compile("urn:" + BROKEN_DEFAULT_NAMESPACE + ":service:(" + Constants.REGEX_ID+ ")");

    public UDAServiceId(String id) {
        super(DEFAULT_NAMESPACE, id);
    }

    public static UDAServiceId valueOf(String s) throws InvalidValueException {
        Matcher matcher = UDAServiceId.PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            return new UDAServiceId(matcher.group(1));
        }

        matcher = UDAServiceId.BROKEN_PATTERN.matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            return new UDAServiceId(matcher.group(1));
        }

        // TODO: UPNP VIOLATION: Handle garbage sent by Eyecon Android app
        matcher = Pattern.compile("urn:upnp-orgerviceId:urnchemas-upnp-orgervice:(" + Constants.REGEX_ID + ")").matcher(s);
        if (matcher.matches()) {
            log.warning("UPnP specification violation, recovering from Eyecon garbage: " + s);
            return new UDAServiceId(matcher.group(1));
        }

        // Some devices just set the last token of the Service ID, e.g. 'ContentDirectory'
        if("ContentDirectory".equals(s) ||
           "ConnectionManager".equals(s) ||
           "RenderingControl".equals(s) ||
           "AVTransport".equals(s)) {
            log.warning("UPnP specification violation, fixing broken Service ID: " + s);
            return new UDAServiceId(s);
        }

        throw new InvalidValueException("Can't parse UDA service ID string (upnp-org/id): " + s);
    }

}
