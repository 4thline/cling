/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.Constants;

import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Represents a service identifier, for example <code>urn:my-domain-namespace:serviceId:MyService123</code>
 *
 * @author Christian Bauer
 */
public class ServiceId {

    final private static Logger log = Logger.getLogger(ServiceId.class.getName());

    public static final Pattern PATTERN =
        Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):serviceId:(" + Constants.REGEX_ID + ")");

    // Note: 'service' vs. 'serviceId'
    public static final Pattern BROKEN_PATTERN =
               Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):service:(" + Constants.REGEX_ID+ ")");

    private String namespace;
    private String id;

    public ServiceId(String namespace, String id) {
        if (namespace != null && !namespace.matches(Constants.REGEX_NAMESPACE)) {
            throw new IllegalArgumentException("Service ID namespace contains illegal characters");
        }
        this.namespace = namespace;

        if (id != null && !id.matches(Constants.REGEX_ID)) {
            throw new IllegalArgumentException("Service ID suffix too long (64) or contains illegal characters");
        }
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }

    public static ServiceId valueOf(String s) throws InvalidValueException {

        ServiceId serviceId = null;

        // First try UDAServiceId parse
        try {
            serviceId = UDAServiceId.valueOf(s);
        } catch (Exception ex) {
            // Ignore
        }

        // Now try a generic ServiceId parse
        if (serviceId == null) {
            Matcher matcher = ServiceId.PATTERN.matcher(s);
            if (matcher.matches() && matcher.groupCount() >= 2) {
                return new ServiceId(matcher.group(1), matcher.group(2));
            } else {
                log.warning("UPnP specification violation, trying to read invalid Service ID: " + s);
                matcher = ServiceId.BROKEN_PATTERN.matcher(s);
                if (matcher.matches() && matcher.groupCount() >= 2) {
                    return new ServiceId(matcher.group(1), matcher.group(2));
                } else {
                    // TODO: UPNP VIOLATION: PS Audio Bridge has invalid service IDs
                    String tokens[] = s.split("[:]");
                    if (tokens.length == 4) {
                        return new ServiceId(tokens[1], tokens[3]);
                    }
                }
            }
            throw new InvalidValueException("Can't parse Service ID string (namespace/id): " + s);
        }
        return serviceId;
    }

    @Override
    public String toString() {
        return "urn:" + getNamespace() + ":serviceId:" + getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ServiceId)) return false;

        ServiceId serviceId = (ServiceId) o;

        if (!id.equals(serviceId.id)) return false;
        if (!namespace.equals(serviceId.namespace)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
