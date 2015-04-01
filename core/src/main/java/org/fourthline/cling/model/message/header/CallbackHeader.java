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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class CallbackHeader extends UpnpHeader<List<URL>> {

    final private static Logger log = Logger.getLogger(CallbackHeader.class.getName());

    public CallbackHeader() {
        setValue(new ArrayList<URL>());
    }

    public CallbackHeader(List<URL> urls) {
        this();
        getValue().addAll(urls);
    }

    public CallbackHeader(URL url) {
        this();
        getValue().add(url);
    }

    public void setString(String s) throws InvalidHeaderException {

        if (s.length() == 0) {
            // Well, no callback URLs are not useful but we have to consider this state
            return;
        }

        if (!s.contains("<") || !s.contains(">")) {
            throw new InvalidHeaderException("URLs not in brackets: " + s);
        }

        s = s.replaceAll("<", "");
        String[] split = s.split(">");
        try {
            List<URL> urls = new ArrayList<>();
            for (String sp : split) {
                sp = sp.trim();

                if (!sp.startsWith("http://")) {
                    log.warning("Discarding non-http callback URL: " + sp);
                    continue;
                }

                URL url = new URL(sp);
                try {
                    /*
                        On some platforms (Android...), a valid URL might not be a valid URI, so
                        we need to test for this and skip any invalid URI, e.g.

                        Java.net.URISyntaxException: Invalid % sequence: %wl in authority at index 32: http://[fe80::208:caff:fec4:824e%wlan0]:8485/eventSub
    		                at libcore.net.UriCodec.validate(UriCodec.java:58)
                            at java.net.URI.parseURI(URI.java:394)
                            at java.net.URI.<init>(URI.java:204)
                            at java.net.URL.toURI(URL.java:497)
            	    */
                    url.toURI();
                } catch (URISyntaxException ex) {
                    log.log(Level.WARNING, "Discarding callback URL, not a valid URI on this platform: " + url, ex);
                    continue;
                }

                urls.add(url);
            }
            setValue(urls);
        } catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Can't parse callback URLs from '" + s + "': " + ex);
        }
    }

    public String getString() {
        StringBuilder s = new StringBuilder();
        for (URL url : getValue()) {
            s.append("<").append(url.toString()).append(">");
        }
        return s.toString();
    }
}
