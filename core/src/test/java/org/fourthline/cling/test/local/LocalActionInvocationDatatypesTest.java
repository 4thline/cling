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
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class LocalActionInvocationDatatypesTest {

    @Test
    public void invokeActions() throws Exception {

        LocalDevice device = new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("SomeDevice", 1),
                new DeviceDetails("Some Device"),
                SampleData.readService(LocalTestServiceOne.class)
        );
        LocalService svc = SampleData.getFirstService(device);

        ActionInvocation getDataInvocation = new ActionInvocation(svc.getAction("GetData"));
        svc.getExecutor(getDataInvocation.getAction()).execute(getDataInvocation);
        assertEquals(getDataInvocation.getFailure(), null);
        assertEquals(getDataInvocation.getOutput().length, 1);
        assertEquals(((byte[]) getDataInvocation.getOutput()[0].getValue()).length, 512);

        // This fails, we can't put arbitrary bytes into a String and hope it will be valid unicode characters!
        /* TODO: This now only logs a warning!
        ActionInvocation getStringDataInvocation = new ActionInvocation(svc.getAction("GetDataString"));
        svc.getExecutor(getStringDataInvocation.getAction()).execute(getStringDataInvocation);
        assertEquals(getStringDataInvocation.getFailure().getErrorCode(), ErrorCode.ARGUMENT_VALUE_INVALID.getCode());
        assertEquals(
                getStringDataInvocation.getFailure().getMessage(),
                "The argument value is invalid. Wrong type or invalid value for 'RandomDataString': " +
                        "Invalid characters in string value (XML 1.0, section 2.2) produced by (StringDatatype)."
        );
        */

        ActionInvocation invocation = new ActionInvocation(svc.getAction("GetStrings"));
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(invocation.getOutput().length, 2);
        assertEquals(invocation.getOutput("One").toString(), "foo");
        assertEquals(invocation.getOutput("Two").toString(), "bar");

        invocation = new ActionInvocation(svc.getAction("GetThree"));
        assertEquals(svc.getAction("GetThree").getOutputArguments()[0].getDatatype().getBuiltin().getDescriptorName(), "i2");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(invocation.getOutput().length, 1);
        assertEquals(invocation.getOutput("three").toString(), "123");

        invocation = new ActionInvocation(svc.getAction("GetFour"));
        assertEquals(svc.getAction("GetFour").getOutputArguments()[0].getDatatype().getBuiltin().getDescriptorName(), "int");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(invocation.getOutput().length, 1);
        assertEquals(invocation.getOutput("four").toString(), "456");

        invocation = new ActionInvocation(svc.getAction("GetFive"));
        assertEquals(svc.getAction("GetFive").getOutputArguments()[0].getDatatype().getBuiltin().getDescriptorName(), "int");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure(), null);
        assertEquals(invocation.getOutput().length, 1);
        assertEquals(invocation.getOutput("five").toString(), "456");
    }

    @UpnpService(
            serviceId = @UpnpServiceId("SomeService"),
            serviceType = @UpnpServiceType(value = "SomeService", version = 1),
            supportsQueryStateVariables = false
    )
    public static class LocalTestServiceOne {

        @UpnpStateVariable(sendEvents = false)
        private byte[] data;

        @UpnpStateVariable(sendEvents = false, datatype = "string")
        private String dataString;

        @UpnpStateVariable(sendEvents = false)
        private String one;

        @UpnpStateVariable(sendEvents = false)
        private String two;

        @UpnpStateVariable(sendEvents = false)
        private short three;

        @UpnpStateVariable(sendEvents = false, name = "four", datatype = "int")
        private int four;

        public LocalTestServiceOne() {
            data = new byte[512];
            new Random().nextBytes(data);

            try {
                dataString = new String(data, "UTF-8");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        // This works and the byte[] should not interfere with any Object[] handling in the executors
        @UpnpAction(out = @UpnpOutputArgument(name = "RandomData"))
        public byte[] getData() {
            return data;
        }

        // This fails, we can't just put random data into a string
        @UpnpAction(out = @UpnpOutputArgument(name = "RandomDataString"))
        public String getDataString() {
            return dataString;
        }

        // We are testing _several_ output arguments returned in a bean, access through getters
        @UpnpAction(out = {
                @UpnpOutputArgument(name = "One", getterName = "getOne"),
                @UpnpOutputArgument(name = "Two", getterName = "getTwo")
        })
        public StringsHolder getStrings() {
            return new StringsHolder();
        }

        // Conversion of short into integer/UPnP "i2" datatype
        @UpnpAction(out = @UpnpOutputArgument(name = "three"))
        public short getThree() {
            return 123;
        }

        // Conversion of int into integer/UPnP "int" datatype
        @UpnpAction(out = @UpnpOutputArgument(name = "four"))
        public Integer getFour() {
            return 456;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "five", stateVariable = "four"))
        public int getFive() {
            return 456;
        }
    }

    public static class StringsHolder {
        String one = "foo";
        String two = "bar";

        public String getOne() {
            return one;
        }

        public String getTwo() {
            return two;
        }
    }
}
