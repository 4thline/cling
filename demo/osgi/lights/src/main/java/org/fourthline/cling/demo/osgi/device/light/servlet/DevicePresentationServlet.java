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

package org.fourthline.cling.demo.osgi.device.light.servlet;

import javax.servlet.http.HttpServlet;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Bruce Green
 */
public class DevicePresentationServlet extends HttpServlet {

    private static final long serialVersionUID = 2828791839299050304L;
    private final String PROPERTY_PORT = "org.osgi.service.http.port";
    private String alias;

    public DevicePresentationServlet(String alias) {
        this.alias = alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getPresentationURL() {
        String url = null;

        try {
            InetAddress host = InetAddress.getLocalHost();
            String port = System.getProperty(PROPERTY_PORT);
            url = String.format("http://%s%s%s", host.getHostAddress(), port != null ? (":" + port) : "", alias);

        } catch (UnknownHostException e) {
        }

        return url;
    }


}
