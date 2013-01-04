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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.ServletContainerAdapter;
import org.fourthline.cling.transport.spi.StreamServerConfiguration;

/**
 * Settings for the async Servlet 3.0 implementation.
 * <p>
 * If you are trying to integrate Cling with an existing/running servlet
 * container, implement {@link org.fourthline.cling.transport.spi.ServletContainerAdapter}.
 * </p>
 *
 * @author Christian Bauer
 */
public class AsyncServletStreamServerConfigurationImpl implements StreamServerConfiguration {

    protected ServletContainerAdapter servletContainerAdapter;
    protected int listenPort = 0;
    protected int asyncTimeoutMillis = 10000;

    /**
     * Defaults to port '0', ephemeral.
     */
    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter) {
        this.servletContainerAdapter = servletContainerAdapter;
    }

    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter,
                                                     int listenPort) {
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
    }

    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter,
                                                     int listenPort,
                                                     int asyncTimeoutMillis) {
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
        this.asyncTimeoutMillis = asyncTimeoutMillis;
    }

    /**
     * @return Defaults to <code>0</code>.
     */
    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * The time in milliseconds this server wait for the {@link org.fourthline.cling.transport.Router}
     * to execute a {@link org.fourthline.cling.transport.spi.UpnpStream}.
     *
     * @return The default of 10 seconds.
     */
    public int getAsyncTimeoutMillis() {
        return asyncTimeoutMillis;
    }

    public void setAsyncTimeoutMillis(int asyncTimeoutMillis) {
        this.asyncTimeoutMillis = asyncTimeoutMillis;
    }

    public ServletContainerAdapter getServletContainerAdapter() {
        return servletContainerAdapter;
    }

    public void setServletContainerAdapter(ServletContainerAdapter servletContainerAdapter) {
        this.servletContainerAdapter = servletContainerAdapter;
    }
}
