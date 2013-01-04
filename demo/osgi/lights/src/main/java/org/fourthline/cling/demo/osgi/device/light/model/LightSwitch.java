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

/**
 * @author Bruce Green
 */
public class LightSwitch extends PoweredDevice implements PowerSource {

    private Boolean state;

    public LightSwitch(PowerSource source, Boolean state) {
        super(source);
        setState(state);
    }

    public LightSwitch() {
        super();
        setState(Boolean.FALSE);
    }

    public void setState(Boolean state) {
        if (this.state != state) {
            this.state = state;
            setChanged();
            notifyObservers(this);
        }
    }

    public Boolean getState() {
        return state;
    }

    @Override
    public Power getPower() {
        Power input = getPowerSource().getPower();
        Power output = null;

        if (input != null && state.booleanValue()) {
            output = input;
        }

        return output;
    }
}
