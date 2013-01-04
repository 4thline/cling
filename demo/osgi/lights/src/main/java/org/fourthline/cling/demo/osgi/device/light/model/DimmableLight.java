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
