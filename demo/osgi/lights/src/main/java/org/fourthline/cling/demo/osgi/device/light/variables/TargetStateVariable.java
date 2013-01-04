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

package org.fourthline.cling.demo.osgi.device.light.variables;

import org.fourthline.cling.demo.osgi.device.light.model.LightSwitch;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPService;

/**
 * @author Bruce Green
 */
public class TargetStateVariable implements UPnPLocalStateVariable {

    private UPnPDevice device;
    private UPnPService service;
    private LightSwitch lightSwitch;

    public TargetStateVariable(UPnPDevice device, UPnPService service, LightSwitch lightSwitch) {
        this.device = device;
        this.service = service;
        this.lightSwitch = lightSwitch;
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Override
    public Class getJavaDataType() {
        return Boolean.class;
    }

    @Override
    public String getUPnPDataType() {
        return UPnPLocalStateVariable.TYPE_BOOLEAN;
    }

    @Override
    public Object getDefaultValue() {
        return Boolean.FALSE;
    }

    @Override
    public String[] getAllowedValues() {
        return null;
    }

    @Override
    public Number getMinimum() {
        return null;
    }

    @Override
    public Number getMaximum() {
        return null;
    }

    @Override
    public Number getStep() {
        return null;
    }

    @Override
    public boolean sendsEvents() {
        return false;
    }

    @Override
    public Object getCurrentValue() {
        return lightSwitch.getState();
    }

    public void setCurrentValue(Boolean value) {
        lightSwitch.setState(value);
    }
}
