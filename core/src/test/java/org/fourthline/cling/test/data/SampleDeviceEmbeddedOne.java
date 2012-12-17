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
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.profile.DeviceDetailsProvider;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

/**
 * @author Christian Bauer
 */
public class SampleDeviceEmbeddedOne extends SampleDevice {

    public SampleDeviceEmbeddedOne(DeviceIdentity identity, Service service, Device embeddedDevice) {
        super(identity, service, embeddedDevice);
    }

    @Override
    public DeviceType getDeviceType() {
        return new UDADeviceType("MY-DEVICE-TYPE-TWO", 2);
    }

    @Override
    public DeviceDetails getDeviceDetails() {
        return new DeviceDetails(
                "My Testdevice Second",
                new ManufacturerDetails("4th Line", "http://www.4thline.org/"),
                new ModelDetails("MYMODEL", "TEST Device", "ONE", "http://www.4thline.org/this_is_the_embedded_model"),
                "000da201238d",
                "100000000002",
                "http://www.4thline.org/some_other_user_interface");

    }

    @Override
    public DeviceDetailsProvider getDeviceDetailsProvider() {
        return new DeviceDetailsProvider() {
            public DeviceDetails provide(RemoteClientInfo info) {
                return getDeviceDetails();
            }
        };
    }

    @Override
    public Icon[] getIcons() {
        return new Icon[]{
                new Icon("image/png", 32, 32, 8, "icon3.png")
        };
    }

    public static UDN getEmbeddedOneUDN() {
        return new UDN("MY-DEVICE-456");
    }

}
