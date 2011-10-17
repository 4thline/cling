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

/**
 * @author Bruce Green
 */
public class DimmableLight extends PoweredDevice implements Light {

    private LightSwitch lightSwitch;
    private Dimmer dimmer;
    private LightBulb lightBulb;

    public DimmableLight(PowerSource source, LightSwitch lightSwitch, Dimmer dimmer, LightBulb lightBulb) {
        super(source);
        setLightSwitch(lightSwitch);
        setDimmer(dimmer);
        setLightBulb(lightBulb);
    }

    public DimmableLight() {
        super();
        setLightSwitch(new LightSwitch());
        setDimmer(new Dimmer());
        setLightBulb(new LightBulb());
    }

    public LightSwitch getLightSwitch() {
        return lightSwitch;
    }

    public void setLightSwitch(LightSwitch lightSwitch) {
        if (this.lightSwitch != lightSwitch) {
            if (this.lightSwitch != null) {
                this.lightSwitch.setPowerSource(null);

                if (dimmer != null) {
                    dimmer.setPowerSource(null);
                }
            }

            this.lightSwitch = lightSwitch;

            if (this.lightSwitch != null) {
                this.lightSwitch.setPowerSource(getPowerSource());

                if (dimmer != null) {
                    dimmer.setPowerSource(lightSwitch);
                }
            }

            notifyObservers(this);
        }
    }

    public Dimmer getDimmer() {
        return dimmer;
    }

    public void setDimmer(Dimmer dimmer) {
        if (this.dimmer != dimmer) {
            if (this.dimmer != null) {
                this.dimmer.setPowerSource(null);

                if (lightBulb != null) {
                    lightBulb.setPowerSource(null);
                }
            }

            this.dimmer = dimmer;

            if (this.dimmer != null) {
                this.dimmer.setPowerSource(lightSwitch);

                if (lightBulb != null) {
                    lightBulb.setPowerSource(dimmer);
                }
            }

            notifyObservers(this);
        }
    }

    public LightBulb getLightBulb() {
        return lightBulb;
    }

    public void setLightBulb(LightBulb lightBulb) {
        if (this.lightBulb != lightBulb) {
            if (this.lightBulb != null) {
                this.lightBulb.setPowerSource(null);
            }

            this.lightBulb = lightBulb;

            if (this.lightBulb != null) {
                lightBulb.setPowerSource(dimmer);
            }

            notifyObservers(this);
        }
    }

    @Override
    public Integer getLuminance() {
        return getLightBulb() != null ? getLightBulb().getLuminance() : null;
    }

}
