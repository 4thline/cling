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

package org.fourthline.cling.model.message;

import org.seamless.http.Headers;
import org.fourthline.cling.model.message.header.UpnpHeader;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides UPnP header API in addition to plain multi-map HTTP header access.
 *
 * @author Christian Bauer
 */
public class UpnpHeaders extends Headers {

    private static final Logger log = Logger.getLogger(UpnpHeaders.class.getName());

    protected Map<UpnpHeader.Type, List<UpnpHeader>> parsedHeaders;

    public UpnpHeaders() {
    }

    public UpnpHeaders(Map<String, List<String>> headers) {
        super(headers);
    }

    public UpnpHeaders(ByteArrayInputStream inputStream) {
        super(inputStream);
    }

    public UpnpHeaders(boolean normalizeHeaders) {
        super(normalizeHeaders);
    }

    protected void parseHeaders() {
        // This runs as late as possible and only when necessary (getter called and map is dirty)
        parsedHeaders = new LinkedHashMap<>();
        if (log.isLoggable(Level.FINE))
            log.fine("Parsing all HTTP headers for known UPnP headers: " + size());
        for (Entry<String, List<String>> entry : entrySet()) {

            if (entry.getKey() == null) continue; // Oh yes, the JDK has 'null' HTTP headers

            UpnpHeader.Type type = UpnpHeader.Type.getByHttpName(entry.getKey());
            if (type == null) {
                if (log.isLoggable(Level.FINE))
                    log.fine("Ignoring non-UPNP HTTP header: " + entry.getKey());
                continue;
            }

            for (String value : entry.getValue()) {
                UpnpHeader upnpHeader = UpnpHeader.newInstance(type, value);
                if (upnpHeader == null || upnpHeader.getValue() == null) {
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            "Ignoring known but irrelevant header (value violates the UDA specification?) '"
                                + type.getHttpName()
                                + "': "
                                + value
                        );
                } else {
                    addParsedValue(type, upnpHeader);
                }
            }
        }
    }

    protected void addParsedValue(UpnpHeader.Type type, UpnpHeader value) {
        if (log.isLoggable(Level.FINE))
            log.fine("Adding parsed header: " + value);
        List<UpnpHeader> list = parsedHeaders.get(type);
        if (list == null) {
            list = new LinkedList<>();
            parsedHeaders.put(type, list);
        }
        list.add(value);
    }

    @Override
    public List<String> put(String key, List<String> values) {
        parsedHeaders = null;
        return super.put(key, values);
    }

    @Override
    public void add(String key, String value) {
        parsedHeaders = null;
        super.add(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        parsedHeaders = null;
        return super.remove(key);
    }

    @Override
    public void clear() {
        parsedHeaders = null;
        super.clear();
    }

    public boolean containsKey(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.containsKey(type);
    }

    public List<UpnpHeader> get(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.get(type);
    }

    public void add(UpnpHeader.Type type, UpnpHeader value) {
        super.add(type.getHttpName(), value.getString());
        if (parsedHeaders != null)
            addParsedValue(type, value);
    }

    public void remove(UpnpHeader.Type type) {
        super.remove(type.getHttpName());
        if (parsedHeaders != null)
            parsedHeaders.remove(type);
    }

    public UpnpHeader[] getAsArray(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.get(type) != null
                ? parsedHeaders.get(type).toArray(new UpnpHeader[parsedHeaders.get(type).size()])
                : new UpnpHeader[0];
    }

    public UpnpHeader getFirstHeader(UpnpHeader.Type type) {
        return getAsArray(type).length > 0
                ? getAsArray(type)[0]
                : null;
    }

    public <H extends UpnpHeader> H getFirstHeader(UpnpHeader.Type type, Class<H> subtype) {
        UpnpHeader[] headers = getAsArray(type);
        if (headers.length == 0) return null;

        for (UpnpHeader header : headers) {
            if (subtype.isAssignableFrom(header.getClass())) {
                return (H) header;
            }
        }
        return null;
    }

    public String getFirstHeaderString(UpnpHeader.Type type) {
        UpnpHeader header = getFirstHeader(type);
        return header != null ? header.getString() : null;
    }

    public void log() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("############################ RAW HEADERS ###########################");
            for (Entry<String, List<String>> entry : entrySet()) {
                log.fine("=== NAME : " + entry.getKey());
                for (String v : entry.getValue()) {
                    log.fine("VALUE: " + v);
                }
            }
            if (parsedHeaders != null && parsedHeaders.size() > 0) {
                log.fine("########################## PARSED HEADERS ##########################");
                for (Map.Entry<UpnpHeader.Type, List<UpnpHeader>> entry : parsedHeaders.entrySet()) {
                    log.fine("=== TYPE: " + entry.getKey());
                    for (UpnpHeader upnpHeader : entry.getValue()) {
                        log.fine("HEADER: " + upnpHeader);
                    }
                }
            }
            log.fine("####################################################################");
        }
    }

}
