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

package org.fourthline.cling.test.protocol;

import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.protocol.sync.ReceivingEvent;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class ProtocolFactoryTest {

    @Test(expectedExceptions = org.fourthline.cling.protocol.ProtocolCreationException.class)
    public void noSyncProtocol() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        ReceivingSync protocol = upnpService.getProtocolFactory().createReceivingSync(
            new StreamRequestMessage(
                UpnpRequest.Method.NOTIFY,
                URI.create("/dev/1234/upnp-org/SwitchPower/invalid"),
                ""
            )
        );
    }

    @Test
    public void receivingEvent() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        StreamRequestMessage message = new StreamRequestMessage(
            UpnpRequest.Method.NOTIFY,
            URI.create("/dev/1234/upnp-org/SwitchPower" + Namespace.EVENTS + Namespace.CALLBACK_FILE),
            ""
        );
        ReceivingSync protocol = upnpService.getProtocolFactory().createReceivingSync(message);
        assertTrue(protocol instanceof ReceivingEvent);

        // TODO: UPNP VIOLATION: Onkyo devices send event messages with trailing garbage characters
        // dev/1234/svc/upnp-org/MyService/event/callback192%2e168%2e10%2e38
        message = new StreamRequestMessage(
            UpnpRequest.Method.NOTIFY,
            URI.create("/dev/1234/upnp-org/SwitchPower" + Namespace.EVENTS + Namespace.CALLBACK_FILE + "192%2e168%2e10%2e38"),
            ""
        );
        protocol = upnpService.getProtocolFactory().createReceivingSync(message);
        assertTrue(protocol instanceof ReceivingEvent);

    }
}