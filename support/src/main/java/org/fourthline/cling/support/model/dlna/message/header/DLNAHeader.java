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

package org.fourthline.cling.support.model.dlna.message.header;

import org.seamless.util.Exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.message.header.UpnpHeader;

/**
 * Transforms known and standardized DLNA/HTTP headers from/to string representation.
 * <p>
 * The {@link #newInstance(org.fourthline.cling.support.model.dlna.message.header.DLNAHeader.Type, String)} method
 * attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Mario Franco
 * @author Christian Bauer
 */
public abstract class DLNAHeader<T> extends UpnpHeader<T> {

    final private static Logger log = Logger.getLogger(DLNAHeader.class.getName());

    /**
     * Maps a standardized DLNA header to potential header subtypes.
     */
    public static enum Type {

        TimeSeekRange("TimeSeekRange.dlna.org", TimeSeekRangeHeader.class),
        XSeekRange("X-Seek-Range", TimeSeekRangeHeader.class),
        PlaySpeed("PlaySpeed.dlna.org", PlaySpeedHeader.class),
        AvailableSeekRange("availableSeekRange.dlna.org", AvailableSeekRangeHeader.class),
        GetAvailableSeekRange("getAvailableSeekRange.dlna.org", GetAvailableSeekRangeHeader.class),
        GetContentFeatures("getcontentFeatures.dlna.org", GetContentFeaturesHeader.class),
        ContentFeatures("contentFeatures.dlna.org", ContentFeaturesHeader.class),
        TransferMode("transferMode.dlna.org", TransferModeHeader.class),
        FriendlyName("friendlyName.dlna.org", FriendlyNameHeader.class),
        PeerManager("peerManager.dlna.org", PeerManagerHeader.class),
        AvailableRange("Available-Range.dlna.org", AvailableRangeHeader.class),
        SCID("scid.dlna.org", SCIDHeader.class),
        RealTimeInfo("realTimeInfo.dlna.org", RealTimeInfoHeader.class),
        ScmsFlag("scmsFlag.dlna.org", ScmsFlagHeader.class),
        WCT("WCT.dlna.org", WCTHeader.class),
        MaxPrate("Max-Prate.dlna.org", MaxPrateHeader.class),
        EventType("Event-Type.dlna.org", EventTypeHeader.class),
        Supported("Supported", SupportedHeader.class),
        BufferInfo("Buffer-Info.dlna.org", BufferInfoHeader.class),
        RTPH264DeInterleaving("rtp-h264-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAACDeInterleaving("rtp-aac-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAMRDeInterleaving("rtp-amr-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAMRWBPlusDeInterleaving("rtp-amrwbplus-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        PRAGMA("PRAGMA", PragmaHeader.class);
            
        private static Map<String, Type> byName = new HashMap<String, Type>() {{
            for (Type t : Type.values()) {
                put(t.getHttpName(), t);
            }
        }};

        private String httpName;
        private Class<? extends DLNAHeader>[] headerTypes;

        @SafeVarargs
        private Type(String httpName, Class<? extends DLNAHeader>... headerClass) {
            this.httpName = httpName;
            this.headerTypes = headerClass;
        }

        public String getHttpName() {
            return httpName;
        }

        public Class<? extends DLNAHeader>[] getHeaderTypes() {
            return headerTypes;
        }

        public boolean isValidHeaderType(Class<? extends DLNAHeader> clazz) {
            for (Class<? extends DLNAHeader> permissibleType : getHeaderTypes()) {
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
        	return byName.get(httpName);
        }
    }



    /**
     * Create a new instance of a {@link DLNAHeader} subtype that matches the given type and value.
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
    public static DLNAHeader newInstance(DLNAHeader.Type type, String headerValue) {

        // Try all the UPnP headers and see if one matches our value parsers
        DLNAHeader upnpHeader = null;
        for (int i = 0; i < type.getHeaderTypes().length && upnpHeader == null; i++) {
            Class<? extends DLNAHeader> headerClass = type.getHeaderTypes()[i];
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
}
