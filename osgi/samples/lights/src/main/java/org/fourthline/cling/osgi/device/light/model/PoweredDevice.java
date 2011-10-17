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

package org.fourthline.cling.osgi.device.light.model;

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
