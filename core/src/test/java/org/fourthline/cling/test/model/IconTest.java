/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.test.model;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class IconTest {

    @Test
    public void validIcons() throws Exception {
        RemoteDevice rd = new RemoteDevice(
            SampleData.createRemoteDeviceIdentity(),
            new UDADeviceType("Foo", 1),
            new DeviceDetails("Foo"),
            new Icon[]{
                new Icon(null, 0, 0, 0, "foo"),
                new Icon("foo/bar", 0, 0, 0, "foo"),
                new Icon("foo/bar", 123, 456, 0, "foo")
            },
            new RemoteService[0]
        );
        assertEquals(rd.findIcons().length, 3);
    }

    @Test
    public void invalidIcons() throws Exception {
        RemoteDevice rd = new RemoteDevice(
            SampleData.createRemoteDeviceIdentity(),
            new UDADeviceType("Foo", 1),
            new DeviceDetails("Foo"),
            new Icon[]{
                new Icon("image/png", 123, 123, 8, "urn:not_a_URL"),
            },
            new RemoteService[0]
        );
        assertEquals(rd.findIcons().length, 0);
    }
}
