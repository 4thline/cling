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

package org.fourthline.cling.test.control;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class ActionSampleData {

    public static LocalDevice createTestDevice() throws Exception {
        return createTestDevice(LocalTestService.class);
    }

    public static LocalDevice createTestDevice(Class<?> clazz) throws Exception {
        return createTestDevice(
                SampleData.readService(
                        new AnnotationLocalServiceBinder(),
                        clazz
                )
        );
    }

    public static LocalDevice createTestDevice(LocalService service) throws Exception {
        return new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                service
        );
    }

    @org.fourthline.cling.binding.annotations.UpnpService(
            serviceId = @UpnpServiceId("SwitchPower"),
            serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
    )
    public static class LocalTestService {

        @UpnpStateVariable(sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            target = newTargetValue;
            status = newTargetValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(name = "GetStatus", out = @UpnpOutputArgument(name = "ResultStatus", getterName = "getStatus"))
        public void dummyStatus() {
            // NOOP
        }

        public boolean getStatus() {
            return status;
        }
    }

    public static class LocalTestServiceThrowsException extends LocalTestService {
        @Override
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            throw new RuntimeException("Something is wrong");
        }
    }

    public static class LocalTestServiceDelays extends LocalTestService {
        @Override
        public boolean getTarget() {
            try {
                Thread.sleep(50); // A small delay so they are really concurrent
            } catch (InterruptedException e) {}
            return super.getTarget();
        }
    }

    public static class LocalTestServiceExtended extends LocalTestService {

        @UpnpStateVariable
        String someValue;

        @UpnpAction
        public void setSomeValue(@UpnpInputArgument(name = "SomeValue", aliases ={"SomeValue1"}) String someValue) {
            this.someValue = someValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "SomeValue"))
        public String getSomeValue() {
            return someValue;
        }

    }
    
    @org.fourthline.cling.binding.annotations.UpnpService(
            serviceId = @UpnpServiceId("SwitchPower"),
            serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
    )
    public static class LocalTestServiceWithClientInfo {

        @UpnpStateVariable(sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue) {
            target = newTargetValue;
            status = newTargetValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget(RemoteClientInfo clientInfo) {
            assertNotNull(clientInfo);
            assertEquals(clientInfo.getRemoteAddress().getHostAddress(), "10.0.0.1");
            assertEquals(clientInfo.getLocalAddress().getHostAddress(), "10.0.0.2");
            assertEquals(clientInfo.getRequestUserAgent(), "foo/bar");
            clientInfo.getExtraResponseHeaders().add("X-MY-HEADER", "foobar");
            return target;
        }

        @UpnpAction(name = "GetStatus", out = @UpnpOutputArgument(name = "ResultStatus", getterName = "getStatus"))
        public void dummyStatus() {
            // NOOP
        }

        public boolean getStatus() {
            return status;
        }

    }

}
