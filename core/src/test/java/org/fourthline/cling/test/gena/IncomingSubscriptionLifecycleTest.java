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
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.OutgoingSubscribeResponseMessage;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.EventSequenceHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.protocol.sync.ReceivingSubscribe;
import org.fourthline.cling.protocol.sync.ReceivingUnsubscribe;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertEquals;


public class IncomingSubscriptionLifecycleTest {

    @Test
    public void subscriptionLifecycle() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Register local device and its service
        LocalDevice device = GenaSampleData.createTestDevice(GenaSampleData.LocalTestService.class);
        upnpService.getRegistry().addDevice(device);

        Namespace ns = upnpService.getConfiguration().getNamespace();

        LocalService<?> service = SampleData.getFirstService(device);
        URL callbackURL = URIUtil.createAbsoluteURL(
                SampleData.getLocalBaseURL(), ns.getEventCallbackPath(service)
        );


        StreamRequestMessage subscribeRequestMessage =
                new StreamRequestMessage(UpnpRequest.Method.SUBSCRIBE, ns.getEventSubscriptionPath(service));

        subscribeRequestMessage.getHeaders().add(
                UpnpHeader.Type.CALLBACK,
                new CallbackHeader(callbackURL)
        );
        subscribeRequestMessage.getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());

        ReceivingSubscribe subscribeProt = new ReceivingSubscribe(upnpService, subscribeRequestMessage);
        subscribeProt.run();
        OutgoingSubscribeResponseMessage subscribeResponseMessage = subscribeProt.getOutputMessage();

        assertEquals(subscribeResponseMessage.getOperation().getStatusCode(), UpnpResponse.Status.OK.getStatusCode());
        String subscriptionId = subscribeResponseMessage.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue();
        assert subscriptionId.startsWith("uuid:");
        assertEquals(subscribeResponseMessage.getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue(), new Integer(1800));
        assertEquals(upnpService.getRegistry().getLocalSubscription(subscriptionId).getActualDurationSeconds(), 1800);

        // Now send the initial event
        subscribeProt.responseSent(subscribeResponseMessage);

        // And immediately "modify" the state of the service, this should result in "concurrent" event messages
        service.getManager().getPropertyChangeSupport().firePropertyChange("Status", false, true);

        StreamRequestMessage unsubscribeRequestMessage =
                new StreamRequestMessage(UpnpRequest.Method.UNSUBSCRIBE, ns.getEventSubscriptionPath(service));
        unsubscribeRequestMessage.getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscriptionId));

        ReceivingUnsubscribe unsubscribeProt = new ReceivingUnsubscribe(upnpService, unsubscribeRequestMessage);
        unsubscribeProt.run();
        StreamResponseMessage unsubscribeResponseMessage = unsubscribeProt.getOutputMessage();
        assertEquals(unsubscribeResponseMessage.getOperation().getStatusCode(), UpnpResponse.Status.OK.getStatusCode());
        assert(upnpService.getRegistry().getLocalSubscription(subscriptionId) == null);

        assertEquals(upnpService.getSentStreamRequestMessages().size(), 2);
        assertEquals(
                (upnpService.getSentStreamRequestMessages().get(0).getOperation()).getMethod(),
                UpnpRequest.Method.NOTIFY
        );
        assertEquals(
                (upnpService.getSentStreamRequestMessages().get(1).getOperation()).getMethod(),
                UpnpRequest.Method.NOTIFY
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                subscriptionId
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                subscriptionId
        );
        assertEquals(
                (upnpService.getSentStreamRequestMessages().get(0).getOperation()).getURI().toString(),
                callbackURL.toString()
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.SEQ, EventSequenceHeader.class).getValue().getValue(),
                new Long(0)
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SEQ, EventSequenceHeader.class).getValue().getValue(),
                new Long(1)
        );

    }

    @Test
    public void subscriptionLifecycleFailedResponse() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Register local device and its service
        LocalDevice device = GenaSampleData.createTestDevice(GenaSampleData.LocalTestService.class);
        upnpService.getRegistry().addDevice(device);

        Namespace ns = upnpService.getConfiguration().getNamespace();

        LocalService<?> service = SampleData.getFirstService(device);
        URL callbackURL = URIUtil.createAbsoluteURL(
                SampleData.getLocalBaseURL(), ns.getEventCallbackPath(service)
        );

        StreamRequestMessage subscribeRequestMessage =
                new StreamRequestMessage(UpnpRequest.Method.SUBSCRIBE, ns.getEventSubscriptionPath(service));

        subscribeRequestMessage.getHeaders().add(
                UpnpHeader.Type.CALLBACK,
                new CallbackHeader(callbackURL)
        );
        subscribeRequestMessage.getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());

        ReceivingSubscribe subscribeProt = new ReceivingSubscribe(upnpService, subscribeRequestMessage);
        subscribeProt.run();

        // From the response the subsciber _should_ receive, keep the identifier for later
        OutgoingSubscribeResponseMessage subscribeResponseMessage = subscribeProt.getOutputMessage();
        String subscriptionId = subscribeResponseMessage.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue();

        // Now, instead of passing the successful response to the protocol, we make it think something went wrong
        subscribeProt.responseSent(null);

        // The subscription should be removed from the registry!
        assert upnpService.getRegistry().getLocalSubscription(subscriptionId) == null;

    }
}