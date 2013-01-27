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

package org.fourthline.cling.registry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs periodically and calls {@link org.fourthline.cling.registry.RegistryImpl#maintain()}.
 *
 * @author Christian Bauer
 */
public class RegistryMaintainer implements Runnable {

    private static Logger log = Logger.getLogger(RegistryMaintainer.class.getName());

    final private RegistryImpl registry;
    final private int sleepIntervalMillis;

    private volatile boolean stopped = false;

    public RegistryMaintainer(RegistryImpl registry, int sleepIntervalMillis) {
        this.registry = registry;
        this.sleepIntervalMillis = sleepIntervalMillis;
    }

    public void stop() {
        if (log.isLoggable(Level.FINE))
            log.fine("Setting stopped status on thread");
        stopped = true;
    }

    public void run() {
        stopped = false;
        if (log.isLoggable(Level.FINE))
            log.fine("Running registry maintenance loop every milliseconds: " + sleepIntervalMillis);
        while (!stopped) {

            try {
                registry.maintain();
                Thread.sleep(sleepIntervalMillis);
            } catch (InterruptedException ex) {
                stopped = true;
            }

        }
        log.fine("Stopped status on thread received, ending maintenance loop");
    }

}