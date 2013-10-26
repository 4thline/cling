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
 * Represents a service type, for example <code>urn:my-domain-namespace:service:MyService:1</code>.
 * <p>
 * Although decimal versions are accepted and parsed, the version used for
 * comparison is only the integer withou the fraction.
 * </p>
 *
 * @author Christian Bauer
 */
public class ServiceType {

    final private static Logger log = Logger.getLogger(ServiceType.class.getName());

    public static final Pattern PATTERN =
        Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):service:(" + Constants.REGEX_TYPE + "):([0-9]+).*");

    // Note: 'serviceId' vs. 'service'
    public static final Pattern BROKEN_PATTERN =
        Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):serviceId:(" + Constants.REGEX_TYPE + "):([0-9]+).*");

    private String namespace;
    private String type;
    private int version = 1;

    public ServiceType(String namespace, String type) {
        this(namespace, type, 1);
    }

    public ServiceType(String namespace, String type, int version) {

        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service type namespace contains illegal characters");
        }
        this.namespace = namespace;

        if (type != null && !type.matches(Constants.REGEX_TYPE)) {
            throw new IllegalArgumentException("Service type suffix too long (64) or contains illegal characters");
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
     * @return Either a {@link UDAServiceType} or a more generic {@link ServiceType}.
     */
    public static ServiceType valueOf(String s) throws InvalidValueException {

        if (s == null)
            throw new InvalidValueException("Can't parse null string");

        ServiceType serviceType = null;

        // Sometimes crazy UPnP devices deliver spaces in a URN, don't ask...
        s = s.replaceAll("\\s", "");

        // First try UDAServiceType parse
        try {
            serviceType = UDAServiceType.valueOf(s);
        } catch (Exception ex) {
            // Ignore
        }

        if (serviceType != null)
            return serviceType;

        // Now try a generic ServiceType parse
        try {
            Matcher matcher = ServiceType.PATTERN.matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }

            matcher = ServiceType.BROKEN_PATTERN.matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                return new ServiceType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
            }

            // TODO: UPNP VIOLATION: EyeTV Netstream uses colons in service type token
            // urn:schemas-microsoft-com:service:pbda:tuner:1
            matcher = Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):service:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                log.warning(
                    "UPnP specification violation, replacing invalid service type token '"
                        + matcher.group(2)
                        + "' with: "
                        + cleanToken
                );
                return new ServiceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }

            // TODO: UPNP VIOLATION: Ceyton InfiniTV uses colons in service type token and 'serviceId' instead of 'service'
            // urn:schemas-opencable-com:serviceId:dri2:debug:1
            matcher = Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):serviceId:(.+?):([0-9]+).*").matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 3) {
                String cleanToken = matcher.group(2).replaceAll("[^a-zA-Z_0-9\\-]", "-");
                log.warning(
                    "UPnP specification violation, replacing invalid service type token '"
                    + matcher.group(2)
                    + "' with: "
                    + cleanToken
                );
                return new ServiceType(matcher.group(1), cleanToken, Integer.valueOf(matcher.group(3)));
            }
        } catch (RuntimeException e) {
            throw new InvalidValueException(String.format(
                "Can't parse service type string (namespace/type/version) '%s': %s", s, e.toString()
            ));
        }

        throw new InvalidValueException("Can't parse service type string (namespace/type/version): " + s);
    }

    /**
     * @return <code>true</code> if this type's namespace/name matches the other type's namespace/name and
     *         this type's version is equal or higher than the given types version.
     */
    public boolean implementsVersion(ServiceType that) {
        if (that == null) return false;
        if (!namespace.equals(that.namespace)) return false;
        if (!type.equals(that.type)) return false;
        if (version < that.version) return false;
        return true;
    }

    public String toFriendlyString() {
        return getNamespace() + ":" + getType() + ":" + getVersion();
    }

    @Override
    public String toString() {
        return "urn:" + getNamespace() + ":service:" + getType() + ":" + getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ServiceType)) return false;

        ServiceType that = (ServiceType) o;

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
