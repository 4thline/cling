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
