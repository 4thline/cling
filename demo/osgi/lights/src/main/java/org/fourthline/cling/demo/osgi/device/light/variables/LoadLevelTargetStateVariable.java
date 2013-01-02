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

import org.fourthline.cling.demo.osgi.device.light.model.Dimmer;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPService;

/**
 * @author Bruce Green
 */
public class LoadLevelTargetStateVariable implements UPnPLocalStateVariable {

    private UPnPDevice device;
    private UPnPService service;
    private Dimmer dimmer;

    public LoadLevelTargetStateVariable(UPnPDevice device, UPnPService service, Dimmer dimmer) {
        this.device = device;
        this.service = service;
        this.dimmer = dimmer;
    }

    @Override
    public String getName() {
        return "LoadLevelTarget";
    }

    @Override
    public Class getJavaDataType() {
        return Integer.class;
    }

    @Override
    public String getUPnPDataType() {
        return UPnPLocalStateVariable.TYPE_UI1;
    }

    @Override
    public Object getDefaultValue() {
        return 0;
    }

    @Override
    public String[] getAllowedValues() {
        return null;
    }

    @Override
    public Number getMinimum() {
        return 0;
    }

    @Override
    public Number getMaximum() {
        return 100;
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
        return dimmer.getLevel();
    }

    public void setCurrentValue(Integer value) {
        dimmer.setLevel(value);
    }
}
