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
