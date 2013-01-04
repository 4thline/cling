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

package org.fourthline.cling.demo.osgi.device.light.model;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Bruce Green
 */
public class PoweredDevice extends Observable implements Observer {

    private PowerSource source;

    public PoweredDevice(PowerSource source) {
        setPowerSource(source);
    }

    public PoweredDevice() {
        this(new GenericPowerSource());
    }

    public Power getPower() {
        return getPowerSource().getPower();
    }

    public PowerSource getPowerSource() {
        return source;
    }

    public void setPowerSource(PowerSource source) {
        if (this.source != source) {
            this.source = source;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
