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
