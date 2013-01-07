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

package org.fourthline.cling.test.model;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import java.net.URI;

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
                new Icon(null, 0, 0, 0, URI.create("foo")),
                new Icon("foo/bar", 0, 0, 0, URI.create("foo")),
                new Icon("foo/bar", 123, 456, 0, URI.create("foo"))
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
                new Icon("image/png", 123, 123, 8, URI.create("urn:not_a_URL")),
            },
            new RemoteService[0]
        );
        assertEquals(rd.findIcons().length, 0);
    }
}
