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

package org.fourthline.cling.test.bridge.proxy;

import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.auth.HashCredentials;
import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.bridge.link.proxy.CombinedDescriptorBinder;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.test.bridge.SampleData;
import org.seamless.util.io.IO;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.net.URL;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class CombinedDescriptorBinderTest {

    @Test
    public void roundtrip() throws Exception {

        BridgeUpnpServiceConfiguration config = new BridgeUpnpServiceConfiguration(SampleData.getLocalBaseURL(), "");

        CombinedDescriptorBinder binder = new CombinedDescriptorBinder(config);

        LocalDevice localDevice = SampleData.createLocalTestDevice();

        String a = binder.write(localDevice);

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/fourthline/cling/test/bridge/samples/test-device.xml");
        String xml = IO.readLines(is);
        is.close();
        LocalDevice readDevice = binder.read(xml, new Endpoint("123", new URL("http://foo.bar/"), true, new HashCredentials("secret")));
        String b = binder.write(readDevice);

        assertEquals(a, b);
    }
}
