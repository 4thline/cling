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

package org.fourthline.cling.test.local;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class LocalActionInvocationNullTest {

    @Test
    public void invokeActions() throws Exception {

        LocalDevice device = new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("SomeDevice", 1),
                new DeviceDetails("Some Device"),
                SampleData.readService(LocalTestServiceOne.class)
        );
        LocalService<LocalTestServiceOne> svc = SampleData.getFirstService(device);

        ActionInvocation invocation;

        // This succeeds
        invocation = new ActionInvocation(svc.getAction("SetSomeValues"));
        invocation.setInput("One", "foo");
        invocation.setInput("Two", "bar");
        invocation.setInput("Three", "baz");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(svc.getManager().getImplementation().one, "foo");
        assertEquals(svc.getManager().getImplementation().two, "bar");
        assertEquals(svc.getManager().getImplementation().three.toString(), "baz");

        // Empty string is fine, will be converted into "null"
        invocation = new ActionInvocation(svc.getAction("SetSomeValues"));
        invocation.setInput("One", "foo");
        invocation.setInput("Two", "");
        invocation.setInput("Three", null);
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(svc.getManager().getImplementation().one, "foo");
        assertEquals(svc.getManager().getImplementation().two, null);
        assertEquals(svc.getManager().getImplementation().three, null);

        // Null is not fine for primitive input arguments
        invocation = new ActionInvocation(svc.getAction("SetPrimitive"));
        invocation.setInput("Primitive", "");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure().getErrorCode(), ErrorCode.ARGUMENT_VALUE_INVALID.getCode());
        assertEquals(
                invocation.getFailure().getMessage(),
                "The argument value is invalid. Primitive action method argument 'Primitive' requires input value, can't be null or empty string."
        );

        // We forgot to set one and it's a local invocation (no string conversion)
        invocation = new ActionInvocation(svc.getAction("SetSomeValues"));
        invocation.setInput("One", null);
        // OOPS! invocation.setInput("Two", null);
        invocation.setInput("Three", null);
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(svc.getManager().getImplementation().one, null);
        assertEquals(svc.getManager().getImplementation().two, null);
        assertEquals(svc.getManager().getImplementation().three, null);

    }

    @UpnpService(
            serviceId = @UpnpServiceId("SomeService"),
            serviceType = @UpnpServiceType(value = "SomeService", version = 1),
            supportsQueryStateVariables = false,
            stringConvertibleTypes = MyString.class
    )
    public static class LocalTestServiceOne {

        @UpnpStateVariable(name = "A_ARG_TYPE_One", sendEvents = false)
        private String one;

        @UpnpStateVariable(name = "A_ARG_TYPE_Two", sendEvents = false)
        private String two;

        @UpnpStateVariable(name = "A_ARG_TYPE_Three", sendEvents = false)
        private MyString three;

        @UpnpStateVariable(sendEvents = false)
        private boolean primitive;

        @UpnpAction
        public void setSomeValues(@UpnpInputArgument(name = "One") String one,
                                  @UpnpInputArgument(name = "Two") String two,
                                  @UpnpInputArgument(name = "Three") MyString three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }

        @UpnpAction
        public void setPrimitive(@UpnpInputArgument(name = "Primitive") boolean b) {
            this.primitive = primitive;
        }
    }

    public static class MyString {
        private String s;

        public MyString(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}