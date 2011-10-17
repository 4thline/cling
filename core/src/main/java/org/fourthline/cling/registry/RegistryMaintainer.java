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

package org.fourthline.cling.registry;

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
        log.fine("Setting stopped status on thread");
        stopped = true;
    }

    public void run() {
        stopped = false;
        log.fine("Running registry maintenance loop every milliseconds: " + sleepIntervalMillis);
        while (!stopped) {

            try {
                registry.maintain();
                Thread.sleep(sleepIntervalMillis);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        }
        log.fine("Stopped status on thread received, ending maintenance loop");
    }

}