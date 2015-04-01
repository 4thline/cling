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

import org.seamless.util.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transforms known and standardized UPnP/HTTP headers from/to string representation.
 * <p>
 * The {@link #newInstance(org.fourthline.cling.model.message.header.UpnpHeader.Type, String)} method
 * attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class UpnpHeader<T> {

    final private static Logger log = Logger.getLogger(UpnpHeader.class.getName());

    /**
     * Maps a standardized UPnP header to potential header subtypes.
     */
    public static enum Type {

        USN("USN",
                USNRootDeviceHeader.class,
                DeviceUSNHeader.class,
                ServiceUSNHeader.class,
                UDNHeader.class
        ),
        NT("NT",
                RootDeviceHeader.class,
                UDADeviceTypeHeader.class,
                UDAServiceTypeHeader.class,
                DeviceTypeHeader.class,
                ServiceTypeHeader.class,
                UDNHeader.class,
                NTEventHeader.class
        ),
        NTS("NTS", NTSHeader.class),
        HOST("HOST", HostHeader.class),
        SERVER("SERVER", ServerHeader.class),
        LOCATION("LOCATION", LocationHeader.class),
        MAX_AGE("CACHE-CONTROL", MaxAgeHeader.class),
        USER_AGENT("USER-AGENT", UserAgentHeader.class),
        CONTENT_TYPE("CONTENT-TYPE", ContentTypeHeader.class),
        MAN("MAN", MANHeader.class),
        MX("MX", MXHeader.class),
        ST("ST",
                STAllHeader.class,
                RootDeviceHeader.class,
                UDADeviceTypeHeader.class,
                UDAServiceTypeHeader.class,
                DeviceTypeHeader.class,
                ServiceTypeHeader.class,
                UDNHeader.class
        ),
        EXT("EXT", EXTHeader.class),
        SOAPACTION("SOAPACTION", SoapActionHeader.class),
        TIMEOUT("TIMEOUT", TimeoutHeader.class),
        CALLBACK("CALLBACK", CallbackHeader.class),
        SID("SID", SubscriptionIdHeader.class),
        SEQ("SEQ", EventSequenceHeader.class),
        RANGE("RANGE", RangeHeader.class),
        CONTENT_RANGE("CONTENT-RANGE", ContentRangeHeader.class),
        PRAGMA("PRAGMA", PragmaHeader.class),
        
        EXT_IFACE_MAC("X-CLING-IFACE-MAC", InterfaceMacHeader.class),
        EXT_AV_CLIENT_INFO("X-AV-CLIENT-INFO", AVClientInfoHeader.class);

        private static Map<String, Type> byName = new HashMap<String, Type>() {{
            for (Type t : Type.values()) {
                put(t.getHttpName(), t);
            }
        }};

        private String httpName;
        private Class<? extends UpnpHeader>[] headerTypes;

        @SafeVarargs
        private Type(String httpName, Class<? extends UpnpHeader>... headerClass) {
            this.httpName = httpName;
            this.headerTypes = headerClass;
        }

        public String getHttpName() {
            return httpName;
        }

        public Class<? extends UpnpHeader>[] getHeaderTypes() {
            return headerTypes;
        }

        public boolean isValidHeaderType(Class<? extends UpnpHeader> clazz) {
            for (Class<? extends UpnpHeader> permissibleType : getHeaderTypes()) {
                if (permissibleType.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param httpName A case-insensitive HTTP header name.
         */
        public static Type getByHttpName(String httpName) {
            if (httpName == null) return null;
        	return byName.get(httpName.toUpperCase(Locale.ROOT));
        }
    }

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * @param s This header's value as a string representation.
     * @throws InvalidHeaderException If the value is invalid for this UPnP header.
     */
    public abstract void setString(String s) throws InvalidHeaderException;

    /**
     * @return A string representing this header's value.
     */
    public abstract String getString();

    /**
     * Create a new instance of a {@link UpnpHeader} subtype that matches the given type and value.
     * <p>
     * This method iterates through all potential header subtype classes as declared in {@link Type}.
     * It creates a new instance of the subtype class and calls its {@link #setString(String)} method.
     * If no {@link org.fourthline.cling.model.message.header.InvalidHeaderException} is thrown, the subtype
     * instance is returned.
     * </p>
     *
     * @param type The type (or name) of the header.
     * @param headerValue The value of the header.
     * @return The best matching header subtype instance, or <code>null</code> if no subtype can be found.
     */
    public static UpnpHeader newInstance(UpnpHeader.Type type, String headerValue) {

        // Try all the UPnP headers and see if one matches our value parsers
        UpnpHeader upnpHeader = null;
        for (int i = 0; i < type.getHeaderTypes().length && upnpHeader == null; i++) {
            Class<? extends UpnpHeader> headerClass = type.getHeaderTypes()[i];
            try {
                log.finest("Trying to parse '" + type + "' with class: " + headerClass.getSimpleName());
                upnpHeader = headerClass.newInstance();
                if (headerValue != null) {
                    upnpHeader.setString(headerValue);
                }
            } catch (InvalidHeaderException ex) {
                log.finest("Invalid header value for tested type: " + headerClass.getSimpleName() + " - " + ex.getMessage());
                upnpHeader = null;
            } catch (Exception ex) {
                log.severe("Error instantiating header of type '" + type + "' with value: " + headerValue);
                log.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(ex));
            }

        }
        return upnpHeader;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getValue() + "'";
    }
}
