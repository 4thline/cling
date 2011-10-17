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

package org.fourthline.cling.test.gena;

import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class EventXMLProcessingTest {

    @Test
    public void writeReadRequest() throws Exception {

        LocalDevice localDevice = GenaSampleData.createTestDevice(GenaSampleData.LocalTestService.class);
        LocalService localService = localDevice.getServices()[0];

        List<URL> urls = new ArrayList() {{
            add(SampleData.getLocalBaseURL());
        }};
        
        LocalGENASubscription subscription =
                new LocalGENASubscription(localService, 1800, urls) {
                    public void failed(Exception ex) {
                        throw new RuntimeException("TEST SUBSCRIPTION FAILED: " + ex);
                    }

                    public void ended(CancelReason reason) {

                    }

                    public void established() {

                    }

                    public void eventReceived() {

                    }
                };

        OutgoingEventRequestMessage outgoingCall =
                new OutgoingEventRequestMessage(subscription, subscription.getCallbackURLs().get(0));

        MockUpnpService upnpService = new MockUpnpService();
        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoingCall);

        StreamRequestMessage incomingStream = new StreamRequestMessage(outgoingCall);

        RemoteDevice remoteDevice = SampleData.createRemoteDevice();
        RemoteService remoteService = SampleData.getFirstService(remoteDevice);

        IncomingEventRequestMessage incomingCall = new IncomingEventRequestMessage(incomingStream, remoteService);

        upnpService.getConfiguration().getGenaEventProcessor().readBody(incomingCall);

        assertEquals(incomingCall.getStateVariableValues().size(), 2);

        boolean gotValueOne = false;
        boolean gotValueTwo = false;
        for (StateVariableValue stateVariableValue : incomingCall.getStateVariableValues()) {
            if (stateVariableValue.getStateVariable().getName().equals("Status")) {
                gotValueOne = ((Boolean) stateVariableValue.getValue() == false);
            }
            if (stateVariableValue.getStateVariable().getName().equals("SomeVar")) {
                // TODO: So... can it be null at all? It has a default value...
                gotValueTwo = stateVariableValue.getValue() == null;
            }
        }
        assert gotValueOne && gotValueTwo;
    }


}
