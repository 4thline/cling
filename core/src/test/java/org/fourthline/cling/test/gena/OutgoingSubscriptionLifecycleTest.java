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

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.URIUtil;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class OutgoingSubscriptionLifecycleTest {

    @Test
    public void subscriptionLifecycle() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            public StreamResponseMessage[] getStreamResponseMessages() {

                return new StreamResponseMessage[]{
                        createSubscribeResponseMessage(),
                        createUnsubscribeResponseMessage()

                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList();

        // Register remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(device);

        RemoteService service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                testAssertions.add(false);
            }

            @Override
            public void established(GENASubscription subscription) {
                testAssertions.add(true);
            }

            @Override
            public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
                assert reason == null;
                assertEquals(responseStatus.getStatusCode(), UpnpResponse.Status.OK.getStatusCode());
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                assertEquals(subscription.getCurrentValues().get("Target").toString(), "1");
                testAssertions.add(true);
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        // Subscription process OK?
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        // Simulate received event
        upnpService.getProtocolFactory().createReceivingSync(
                createEventRequestMessage(upnpService, callback)
        ).run();

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(0));
        assertEquals(callback.getSubscription().getSubscriptionId(), "uuid:1234");
        assertEquals(callback.getSubscription().getActualDurationSeconds(), 180);

        List<URL> callbackURLs = ((RemoteGENASubscription) callback.getSubscription())
                .getEventCallbackURLs(upnpService.getRouter().getActiveStreamServers(null), upnpService.getConfiguration().getNamespace());

        callback.end();

        assert callback.getSubscription() == null;

        assertEquals(testAssertions.size(), 3);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }

        assertEquals(upnpService.getSentStreamRequestMessages().size(), 2);
        assertEquals(
                (upnpService.getSentStreamRequestMessages().get(0).getOperation()).getMethod(),
                UpnpRequest.Method.SUBSCRIBE
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue(),
                Integer.valueOf(1800)
        );

        assertEquals(callbackURLs.size(), 1);
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class).getValue().get(0),
                callbackURLs.get(0)
        );

        assertEquals(
                (upnpService.getSentStreamRequestMessages().get(1).getOperation()).getMethod(),
                UpnpRequest.Method.UNSUBSCRIBE
        );
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                "uuid:1234"
        );
    }

    protected StreamResponseMessage createSubscribeResponseMessage() {
        StreamResponseMessage msg = new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
        msg.getHeaders().add(
                UpnpHeader.Type.SID, new SubscriptionIdHeader("uuid:1234")
        );
        msg.getHeaders().add(
                UpnpHeader.Type.TIMEOUT, new TimeoutHeader(180)
        );
        return msg;
    }

    protected StreamResponseMessage createUnsubscribeResponseMessage() {
        return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
    }

    protected IncomingEventRequestMessage createEventRequestMessage(final UpnpService upnpService, final SubscriptionCallback callback) {

        List<StateVariableValue> values = new ArrayList();
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Status"), false)
        );
        values.add(
                new StateVariableValue(callback.getService().getStateVariable("Target"), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                callback.getSubscription(),
                URIUtil.toURL(URI.create("http://10.0.0.123/this/is/ignored/anyway")),
                new UnsignedIntegerFourBytes(0),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(callback.getService())
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, ((RemoteGENASubscription)callback.getSubscription()).getService());
    }

}
