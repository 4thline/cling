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

package org.fourthline.cling.test.data;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.profile.DeviceDetailsProvider;
import org.fourthline.cling.model.types.DeviceType;

import java.lang.reflect.Constructor;

/**
 * @author Christian Bauer
 */
public abstract class SampleDevice {

    public DeviceIdentity identity;
    public Service service;
    public Device embeddedDevice;

    protected SampleDevice(DeviceIdentity identity, Service service, Device embeddedDevice) {
        this.identity = identity;
        this.service = service;
        this.embeddedDevice = embeddedDevice;
    }

    public DeviceIdentity getIdentity() {
        return identity;
    }

    public Service getService() {
        return service;
    }

    public Device getEmbeddedDevice() {
        return embeddedDevice;
    }

    public abstract DeviceType getDeviceType();
    public abstract DeviceDetails getDeviceDetails();
    public abstract DeviceDetailsProvider getDeviceDetailsProvider();
    public abstract Icon[] getIcons();

    public <D extends Device> D newInstance(Constructor<D> deviceConstructor) {
        return newInstance(deviceConstructor, false);
    }

    public <D extends Device> D newInstance(Constructor<D> deviceConstructor, boolean useProvider) {
        try {
            if (useProvider) {
                return deviceConstructor.newInstance(
                        getIdentity(), getDeviceType(), getDeviceDetailsProvider(),
                        getIcons(), getService(), getEmbeddedDevice()
                );
            }
            return deviceConstructor.newInstance(
                    getIdentity(), getDeviceType(), getDeviceDetails(),
                    getIcons(), getService(), getEmbeddedDevice()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
