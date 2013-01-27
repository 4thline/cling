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

import org.fourthline.cling.transport.spi.AbstractStreamClientConfiguration;

import java.util.concurrent.ExecutorService;

/**
 * Settings for the default implementation.
 *
 * @author Christian Bauer
 */
public class StreamClientConfigurationImpl extends AbstractStreamClientConfiguration {

    private boolean usePersistentConnections = false;

    public StreamClientConfigurationImpl(ExecutorService timeoutExecutorService) {
        super(timeoutExecutorService);
    }

    public StreamClientConfigurationImpl(ExecutorService timeoutExecutorService, int timeoutSeconds) {
        super(timeoutExecutorService, timeoutSeconds);
    }

    /**
     * Defaults to <code>false</code>, avoiding obscure bugs in the JDK.
     */
    public boolean isUsePersistentConnections() {
        return usePersistentConnections;
    }

    public void setUsePersistentConnections(boolean usePersistentConnections) {
        this.usePersistentConnections = usePersistentConnections;
    }

}
