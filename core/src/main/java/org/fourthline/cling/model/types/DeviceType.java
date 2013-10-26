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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a device type, for example <code>urn:my-domain-namespace:device:MyDevice:1</code>.
 * <p>
 * Although decimal versions are accepted and parsed, the version used for
 * comparison is only the integer without the fraction.
 * </p>
 *
 * @author Christian Bauer
 */
public class DeviceType {

    final private static Logger log = Logger.getLogger(DeviceType.class.getName());

    public static final String UNKNOWN = "UNKNOWN";

    public static final Pattern PATTERN =
            Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):device:(" + Constants.REGEX_TYPE + "):([0-9]+).*");

    private String namespace;
    private String type;
    private int version = 1;

    public DeviceType(String namespace, String type) {
        this(namespace, type, 1);
    }

    public DeviceType(String namespace, String type, int version) {
        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Device type namespace contains illegal characters");
        }
        this.namespace = namespace;

        if (type != null && !type.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Device type suffix too long (64) or contains illegal characters");
        }
        this.type = type;

        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getType() {
        return type;
    }

    public int getVersion() {
        return version;
    }

    /**
     * @return Either a {@link UDADeviceType} or a more generic {@link DeviceType}.
     */
    public static DeviceType valueOf(String s) throws InvalidValueException {

        DeviceType deviceType = null;

        // Sometimes crazy UPnP devices deliver spaces in a URN, don't ask...
        s = s.replaceAll("\\s", "");

        // First try UDADeviceType parse
        try {
            deviceType = UDADeviceType.valueOf(s);
        } catch (Exception ex) {
            // Ignore
        }

        if (deviceType != null)
            return deviceType;

        try {
            // Now try a generic DeviceType parse
            Matcher matcher = PATTERN.matcher(s);
            if (matcher.matches()) {
                return new DeviceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }

            // TODO: UPNP VIOLATION: Escient doesn't provide any device type token
            // urn:schemas-upnp-org:device::1
            matcher = Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):device::([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 2) {
                log.warning("UPnP specification violation, no device type token, defaulting to " + UNKNOWN + ": " + s);
                return new DeviceType(matcher.group(1), UNKNOWN, Integer.valueOf(matcher.group(2)));
            }

            // TODO: UPNP VIOLATION: EyeTV Netstream uses colons in device type token
            // urn:schemas-microsoft-com:service:pbda:tuner:1
            matcher = Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):device:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                log.warning(
                    "UPnP specification violation, replacing invalid device type token '"
                        + matcher.group(2)
                        + "' with: "
                        + cleanToken
                );
                return new DeviceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }
        } catch (RuntimeException e) {
            throw new InvalidValueException(String.format(
                "Can't parse device type string (namespace/type/version) '%s': %s", s, e.toString()
            ));
        }

        throw new InvalidValueException("Can't parse device type string (namespace/type/version): " + s);
    }

    public boolean implementsVersion(DeviceType that) {
        if (!namespace.equals(that.namespace)) return false;
        if (!type.equals(that.type)) return false;
        if (version < that.version) return false;
        return true;
    }

    public String getDisplayString() {
        return getType();
    }

    @Override
    public String toString() {
        return "urn:" + getNamespace() + ":device:" + getType()+ ":" + getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof DeviceType)) return false;

        DeviceType that = (DeviceType) o;

        if (version != that.version) return false;
        if (!namespace.equals(that.namespace)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + version;
        return result;
    }
}
