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
package example.localservice;

import example.binarylight.BinaryLightSampleData;
import example.controlpoint.EventSubscriptionTest;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.mock.MockRouter;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.Reflections;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Providing events on service state changes
 * <p>
 * The standard mechanism in the JDK for eventing is the <code>PropertyChangeListener</code> reacting
 * on a <code>PropertyChangeEvent</code>. Cling utilizes this API for service eventing, thus avoiding
 * a dependency between your service code and proprietary APIs.
 * </p>
 * <p>
 * Consider the following modification of the original <a href="#section.SwitchPower">SwitchPower:1</a>
 * implementation:
 * </p>
 * <a class="citation" href="javacode://example.localservice.SwitchPowerWithPropertyChangeSupport"/>
 * <p>
 * The only additional dependency is on <code>java.beans.PropertyChangeSupport</code>. Cling
 * detects the <code>getPropertyChangeSupport()</code> method of your service class and automatically
 * binds the service management on it. You will have to have this method for eventing to work with
 * Cling. You can create the <code>PropertyChangeSupport</code> instance
 * in your service's constructor or any other way, the only thing Cling is interested in are property
 * change events with the "property" name of a UPnP state variable.
 * </p>
 * <p>
 * Consequently, <code>firePropertyChange("NameOfAStateVariable")</code> is how you tell Cling that
 * a state variable value has changed. It doesn't even matter if you call
 * <code>firePropertyChange("Status", null, null)</code> or
 * <code>firePropertyChange("Status", oldValue, newValue)</code>.
 * Cling <em>only</em> cares about the state variable name; it will then check if the state variable is
 * evented and pull the data out of your service implementation instance by accessing the appropriate
 * field or a getter. Any "old" or "new" value you pass along is ignored.
 * </p>
 * <p>
 * Also note that <code>firePropertyChange("Target", null, null)</code> would have no effect, because
 * <code>Target</code> is mapped with <code>sendEvents="false"</code>.
 * </p>
 * <p>
 * Most of the time a JavaBean property name is <em>not</em> the same as UPnP state variable
 * name. For example, the JavaBean <code>status</code> property name is lowercase, while the UPnP state
 * variable name is uppercase <code>Status</code>. The Cling eventing system ignores any property
 * change event that doesn't exactly name a service state variable. This allows you to use
 * JavaBean eventing independently from UPnP eventing, e.g. for GUI binding (Swing components also
 * use the JavaBean eventing system).
 * </p>
 * <p>
 * Let's assume for the sake of the next example that <code>Target</code> actually is also evented,
 * like <code>Status</code>. If several evented state variables change in your service, but you don't
 * want to trigger individual change events for each variable, you can combine them in a single event
 * as a comma-separated list of state variable names:
 * </p>
 * <a class="citation" href="javacode://example.localservice.SwitchPowerWithBundledPropertyChange#setTarget(boolean)"/>
 */
public class EventProviderTest extends EventSubscriptionTest {

    @Test
    public void subscriptionLifecycleChangeSupport() throws Exception {

        MockUpnpService upnpService = createMockUpnpService();

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register local device and its service
        LocalDevice device = BinaryLightSampleData.createDevice(SwitchPowerWithPropertyChangeSupport.class);
        upnpService.getRegistry().addDevice(device);

        LocalService<SwitchPowerWithPropertyChangeSupport> service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service, 180) {

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
                assertNotNull(subscription);
                assertNull(reason);
                assertNull(responseStatus);
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                if (subscription.getCurrentSequence().getValue() == 0) {
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                    testAssertions.add(true);
                } else if (subscription.getCurrentSequence().getValue() == 1) {
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "1");
                    testAssertions.add(true);
                } else {
                    testAssertions.add(false);
                }
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        // This triggers the internal PropertyChangeSupport of the service impl!
        service.getManager().getImplementation().setTarget(true);

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(2)); // It's the NEXT sequence!
        assertTrue(callback.getSubscription().getSubscriptionId().startsWith("uuid:"));
        assertEquals(callback.getSubscription().getActualDurationSeconds(), Integer.MAX_VALUE);

        callback.end();

        assertEquals(testAssertions.size(), 4);
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void bundleSeveralVariables() throws Exception {

        MockUpnpService upnpService = createMockUpnpService();

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register local device and its service
        LocalDevice device = BinaryLightSampleData.createDevice(SwitchPowerWithBundledPropertyChange.class);
        upnpService.getRegistry().addDevice(device);

        LocalService<SwitchPowerWithBundledPropertyChange> service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service, 180) {

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
                assertNotNull(subscription);
                assertNull(reason);
                assertNull(responseStatus);
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                if (subscription.getCurrentSequence().getValue() == 0) {
                    assertEquals(subscription.getCurrentValues().get("Target").toString(), "0");
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                    testAssertions.add(true);
                } else if (subscription.getCurrentSequence().getValue() == 1) {
                    assertEquals(subscription.getCurrentValues().get("Target").toString(), "1");
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "1");
                    testAssertions.add(true);
                } else {
                    testAssertions.add(false);
                }
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        // This triggers the internal PropertyChangeSupport of the service impl!
        service.getManager().getImplementation().setTarget(true);

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(2)); // It's the NEXT sequence!
        assertTrue(callback.getSubscription().getSubscriptionId().startsWith("uuid:"));
        assertEquals(callback.getSubscription().getActualDurationSeconds(), Integer.MAX_VALUE);

        callback.end();

        assertEquals(testAssertions.size(), 4);
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void moderateMaxRate() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            createSubscribeResponseMessage(),
                            createUnsubscribeResponseMessage()
                        };
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register local device and its service
        LocalDevice device = BinaryLightSampleData.createDevice(SwitchPowerModerated.class);
        upnpService.getRegistry().addDevice(device);

        LocalService service = SampleData.getFirstService(device);

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
                assertNotNull(subscription);
                assertNull(reason);
                assertNull(responseStatus);
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                if (subscription.getCurrentSequence().getValue() == 0) {

                    // Initial event contains all evented variables, snapshot of the service state
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                    assertEquals(subscription.getCurrentValues().get("ModeratedMinDeltaVar").toString(), "1");

                    // Initial state
                    assertEquals(subscription.getCurrentValues().get("ModeratedMaxRateVar").toString(), "one");

                    testAssertions.add(true);
                } else if (subscription.getCurrentSequence().getValue() == 1) {

                    // Subsequent events do NOT contain unchanged variables
                    assertNull(subscription.getCurrentValues().get("Status"));
                    assertNull(subscription.getCurrentValues().get("ModeratedMinDeltaVar"));

                    // We didn't see the intermediate values "two" and "three" because it's moderated
                    assertEquals(subscription.getCurrentValues().get("ModeratedMaxRateVar").toString(), "four");

                    testAssertions.add(true);
                } else {
                    testAssertions.add(false);
                }
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        Thread.sleep(200);

        Object serviceImpl = service.getManager().getImplementation();

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMaxRateVar"), serviceImpl, "two");
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMaxRateVar", null, null);

        Thread.sleep(200);

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMaxRateVar"), serviceImpl, "three");
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMaxRateVar", null, null);

        Thread.sleep(200);

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMaxRateVar"), serviceImpl, "four");
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMaxRateVar", null, null);

        Thread.sleep(100);

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(2)); // It's the NEXT sequence!

        callback.end();

        assertEquals(testAssertions.size(), 4);
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void moderateMinDelta() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            createSubscribeResponseMessage(),
                            createUnsubscribeResponseMessage()
                        };
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register local device and its service
        LocalDevice device = BinaryLightSampleData.createDevice(SwitchPowerModerated.class);
        upnpService.getRegistry().addDevice(device);

        LocalService service = SampleData.getFirstService(device);

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
                assertNotNull(subscription);
                assertNull(reason);
                assertNull(responseStatus);
                testAssertions.add(true);
            }

            public void eventReceived(GENASubscription subscription) {
                if (subscription.getCurrentSequence().getValue() == 0) {

                    // Initial event contains all evented variables, snapshot of the service state
                    assertEquals(subscription.getCurrentValues().get("Status").toString(), "0");
                    assertEquals(subscription.getCurrentValues().get("ModeratedMaxRateVar").toString(), "one");

                    // Initial state
                    assertEquals(subscription.getCurrentValues().get("ModeratedMinDeltaVar").toString(), "1");

                    testAssertions.add(true);
                } else if (subscription.getCurrentSequence().getValue() == 1) {

                    // Subsequent events do NOT contain unchanged variables
                    assertNull(subscription.getCurrentValues().get("Status"));
                    assertNull(subscription.getCurrentValues().get("ModeratedMaxRateVar"));

                    // We didn't get events for values 2 and 3
                    assertEquals(subscription.getCurrentValues().get("ModeratedMinDeltaVar").toString(), "4");

                    testAssertions.add(true);
                } else {
                    testAssertions.add(false);
                }
            }

            public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        Object serviceImpl = service.getManager().getImplementation();

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMinDeltaVar"), serviceImpl, 2);
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMinDeltaVar", 1, 2);

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMinDeltaVar"), serviceImpl, 3);
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMinDeltaVar", 2, 3);

        Reflections.set(Reflections.getField(serviceImpl.getClass(), "moderatedMinDeltaVar"), serviceImpl, 4);
        service.getManager().getPropertyChangeSupport().firePropertyChange("ModeratedMinDeltaVar", 3, 4);

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(2)); // It's the NEXT sequence!

        callback.end();

        assertEquals(testAssertions.size(), 4);
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }


}
