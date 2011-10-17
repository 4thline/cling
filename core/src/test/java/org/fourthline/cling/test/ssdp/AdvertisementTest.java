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

package org.fourthline.cling.test.ssdp;

import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.ServerClientTokens;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.fourthline.cling.test.data.SampleUSNHeaders;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class AdvertisementTest {

    @Test
    public void sendAliveMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice rootDevice = SampleData.createLocalDevice();
        LocalDevice embeddedDevice = rootDevice.getEmbeddedDevices()[0];

        SendingNotificationAlive prot = new SendingNotificationAlive(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage msg : upnpService.getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(upnpService.getOutgoingDatagramMessages(), rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    @Test
    public void sendByebyeMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice rootDevice = SampleData.createLocalDevice();
        LocalDevice embeddedDevice = rootDevice.getEmbeddedDevices()[0];

        SendingNotificationByebye prot = new SendingNotificationByebye(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage msg : upnpService.getOutgoingDatagramMessages()) {
            assertByebyeMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(upnpService.getOutgoingDatagramMessages(), rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    protected void assertAliveMsgBasics(UpnpMessage msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.ALIVE);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getValue().toString(), SampleDeviceRoot.getDeviceDescriptorURL().toString());
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getValue(), 1800);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getValue(), new ServerClientTokens());
    }

    protected void assertByebyeMsgBasics(UpnpMessage msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.BYEBYE);
    }

}