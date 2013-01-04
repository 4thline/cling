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

/**
 * Combines a {@link UDN} with a {@link ServiceType}, string representation
 * is separated by double-colon.
 *
 * @author Christian Bauer
 */
public class NamedServiceType {

    private UDN udn;
    private ServiceType serviceType;

    public NamedServiceType(UDN udn, ServiceType serviceType) {
        this.udn = udn;
        this.serviceType = serviceType;
    }

    public UDN getUdn() {
        return udn;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public static NamedServiceType valueOf(String s) throws InvalidValueException {
        String[] strings = s.split("::");
        if (strings.length != 2) {
            throw new InvalidValueException("Can't parse UDN::ServiceType from: " + s);
        }

        UDN udn;
        try {
            udn = UDN.valueOf(strings[0]);
        } catch (Exception ex) {
            throw new InvalidValueException("Can't parse UDN: " + strings[0]);
        }

        ServiceType serviceType = ServiceType.valueOf(strings[1]);
        return new NamedServiceType(udn, serviceType);
    }

    @Override
    public String toString() {
        return getUdn().toString() + "::" + getServiceType().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof NamedServiceType)) return false;

        NamedServiceType that = (NamedServiceType) o;

        if (!serviceType.equals(that.serviceType)) return false;
        if (!udn.equals(that.udn)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = udn.hashCode();
        result = 31 * result + serviceType.hashCode();
        return result;
    }
}