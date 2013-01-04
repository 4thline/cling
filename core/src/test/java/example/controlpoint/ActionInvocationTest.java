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
package example.controlpoint;

import example.binarylight.BinaryLightSampleData;
import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Invoking an action
 * <p>
 * UPnP services expose state variables and actions. While the state variables represent the
 * current state of the service, actions are the operations used to query or maniuplate the
 * service's state. You have to obtain a <code>Service</code> instance from a
 * <code>Device</code> to access any <code>Action</code>. The target device can be local
 * to the same UPnP stack as your control point, or it can be remote of another device anywhere
 * on the network. We'll discuss later in this chapter how to access devices through the
 * local stack's <code>Registry</code>.
 * </p>
 * <p>
 * Once you have the device, access the <code>Service</code> through the metadata model, for example:
 * </p>
 * <a class="citation" href="javacode://this#invokeActions(LocalDevice)" id="ai_findservice" style="include: FINDSERVICE"/>
 * <p>
 * This method will search the device and all its embedded devices for a service with the given
 * identifier and returns either the found <code>Service</code> or <code>null</code>. The Cling
 * metamodel is thread-safe, so you can share an instance of <code>Service</code> or
 * <code>Action</code> and access it concurrently.
 * </p>
 * <p>
 * Invoking an action is the job of an instance of <code>ActionInvocation</code>, note that this
 * instance is <em>NOT</em> thread-safe and each thread that wishes to execute an action has to
 * obtain its own invocation from the <code>Action</code> metamodel:
 * </p>
 * <a class="citation" href="javacode://this#invokeActions(LocalDevice)" id="ai_getstatus" style="include: GETSTATUS; exclude: EXC1"/>
 * <p>
 * Execution is asynchronous, your <code>ActionCallback</code> has two methods which will be called
 * by the UPnP stack when the execution completes. If the action is successful, you can obtain any
 * output argument values from the invocation instance, which is conveniently passed into the
 * <code>success()</code> method. You can inspect the named output argument values and their datatypes to
 * continue processing the result.
 * </p>
 * <p>
 * Action execution doesn't have to be processed asynchronously, after all, the underlying HTTP/SOAP protocol
 * is a request waiting for a response. The callback programming model however fits nicely into a typical
 * UPnP client, which also has to process event notifications and device registrations asynchronously. If
 * you want to execute an <code>ActionInvocation</code> directly, within the current thread, use the empty
 * <code>ActionCallback.Default</code> implementation:
 * </p>
 * <a class="citation" href="javacode://this#invokeActions(LocalDevice)" id="ai_synchronous" style="include: SYNCHRONOUS"/>
 * <p>
 * When invocation fails you can access the failure details through
 * <code>invocation.getFailure()</code>, or use the shown convenience method to create a simple error
 * message. See the Javadoc of <code>ActionCallback</code> for more details.
 * </p>
 * <p>
 * When an action requires input argument values, you have to provide them. Like output arguments, any
 * input arguments of actions are also named, so you can set them by calling <code>setInput("MyArgumentName", value)</code>:
 * </p>
 * <a class="citation" href="javacode://this#invokeActions(LocalDevice)" id="ai_settarget" style="include: SETTARGET; exclude: EXC2"/>
 * <p>
 * This action has one input argument of UPnP type "boolean". You can set a Java <code>boolean</code>
 * primitive or <code>Boolean</code> instance and it will be automatically converted. If you set an
 * invalid value for a particular argument, such as an instance with the wrong type,
 * an <code>InvalidValueException</code> will be thrown immediately.
 * </p>
 * <div class="note">
 * <div class="title">Empty values and null in Cling</div>
 * There is no difference between empty string <code>""</code> and <code>null</code> in Cling,
 * because the UPnP specification does not address this issue. The SOAP  message of an action call
 * or an event message must contain an element {@code <SomeVar></SomeVar>} for all arguments, even if
 * it is an empty XML element. If you provide  an empty string or a null value when preparing a message,
 * it will always be a <code>null</code> on the receiving end because we can only transmit one
 * thing, an empty XML element. If you forget to set an input argument's value, it will be null/empty element.
 * </div>
 */
public class ActionInvocationTest {

    protected LocalService bindService(Class<?> clazz) throws Exception {
        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        // Let's also test the overloaded reader
        LocalService svc = binder.read(
                clazz,
                new UDAServiceId("SwitchPower"),
                new UDAServiceType("SwitchPower", 1),
                true,
                new Class[]{MyString.class}
        );
        svc.setManager(
                new DefaultServiceManager(svc, clazz)
        );
        return svc;
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() throws Exception {
        return new LocalDevice[][]{
                {BinaryLightSampleData.createDevice(bindService(TestServiceOne.class))},
                {BinaryLightSampleData.createDevice(bindService(TestServiceTwo.class))},
                {BinaryLightSampleData.createDevice(bindService(TestServiceThree.class))},
        };
    }

    @Test(dataProvider = "devices")
    public void invokeActions(LocalDevice device) throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        Service service = device.findService(new UDAServiceId("SwitchPower")); // DOC: FINDSERVICE
        Action getStatusAction = service.getAction("GetStatus");               // DOC: FINDSERVICE

        final boolean[] tests = new boolean[3];

        ActionInvocation getStatusInvocation = new ActionInvocation(getStatusAction);   // DOC: GETSTATUS

        ActionCallback getStatusCallback = new ActionCallback(getStatusInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue status  = invocation.getOutput("ResultStatus");

                assert status != null;

                assertEquals(status.getArgument().getName(), "ResultStatus");

                assertEquals(status.getDatatype().getClass(), BooleanDatatype.class);
                assertEquals(status.getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);

                assertEquals((Boolean) status.getValue(), Boolean.valueOf(false));
                assertEquals(status.toString(), "0"); // '0' is 'false' in UPnP
                tests[0] = true; // DOC: EXC1
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                System.err.println(defaultMsg);
            }
        };

        upnpService.getControlPoint().execute(getStatusCallback);                       // DOC: GETSTATUS


        Action action = service.getAction("SetTarget");                                 // DOC: SETTARGET

        ActionInvocation setTargetInvocation = new ActionInvocation(action);

        setTargetInvocation.setInput("NewTargetValue", true); // Can throw InvalidValueException

        // Alternative:
        //
        // setTargetInvocation.setInput(
        //         new ActionArgumentValue(
        //                 action.getInputArgument("NewTargetValue"),
        //                 true
        //         )
        // );

        ActionCallback setTargetCallback = new ActionCallback(setTargetInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue[] output = invocation.getOutput();
                assertEquals(output.length, 0);
                tests[1] = true; // DOC: EXC2
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                System.err.println(defaultMsg);
            }
        };

        upnpService.getControlPoint().execute(setTargetCallback);                       // DOC: SETTARGET

        getStatusInvocation = new ActionInvocation(getStatusAction);
        new ActionCallback.Default(getStatusInvocation, upnpService.getControlPoint()).run(); // DOC: SYNCHRONOUS
        ActionArgumentValue status  = getStatusInvocation.getOutput("ResultStatus");
        if (Boolean.valueOf(true).equals(status.getValue())) {
            tests[2] = true;
        }

        for (boolean test : tests) {
            assertEquals(test, true);
        }


        LocalService svc = (LocalService) service;

        ActionInvocation getTargetInvocation = new ActionInvocation(svc.getAction("GetTarget"));
        svc.getExecutor(getTargetInvocation.getAction()).execute(getTargetInvocation);
        assertEquals(getTargetInvocation.getFailure(), null);
        assertEquals(getTargetInvocation.getOutput().length, 1);
        assertEquals(getTargetInvocation.getOutput()[0].toString(), "1");

        ActionInvocation setMyStringInvocation = new ActionInvocation(svc.getAction("SetMyString"));
        setMyStringInvocation.setInput("MyString", "foo");
        svc.getExecutor(setMyStringInvocation.getAction()).execute(setMyStringInvocation);
        assertEquals(setMyStringInvocation.getFailure(), null);
        assertEquals(setMyStringInvocation.getOutput().length, 0);

        ActionInvocation getMyStringInvocation = new ActionInvocation(svc.getAction("GetMyString"));
        svc.getExecutor(getMyStringInvocation.getAction()).execute(getMyStringInvocation);
        assertEquals(getTargetInvocation.getFailure(), null);
        assertEquals(getMyStringInvocation.getOutput().length, 1);
        assertEquals(getMyStringInvocation.getOutput()[0].toString(), "foo");

    }

    @Test(dataProvider = "devices")
    public void invokeActionsWithAlias(LocalDevice device) throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        Service service = device.findService(new UDAServiceId("SwitchPower"));
        Action getStatusAction = service.getAction("GetStatus");

        final boolean[] tests = new boolean[1];

        Action action = service.getAction("SetTarget");
        ActionInvocation setTargetInvocation = new ActionInvocation(action);
        setTargetInvocation.setInput("NewTargetValue1", true);
        ActionCallback setTargetCallback = new ActionCallback(setTargetInvocation) {

            @Override
            public void success(ActionInvocation invocation) {
                ActionArgumentValue[] output = invocation.getOutput();
                assertEquals(output.length, 0);
                tests[0] = true;
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                System.err.println(defaultMsg);
            }
        };
        upnpService.getControlPoint().execute(setTargetCallback);

        for (boolean test : tests) {
            assertEquals(test, true);
        }

        LocalService svc = (LocalService) service;

        ActionInvocation getTargetInvocation = new ActionInvocation(svc.getAction("GetTarget"));
        svc.getExecutor(getTargetInvocation.getAction()).execute(getTargetInvocation);
        assertEquals(getTargetInvocation.getFailure(), null);
        assertEquals(getTargetInvocation.getOutput().length, 1);
        assertEquals(getTargetInvocation.getOutput()[0].toString(), "1");

        ActionInvocation setMyStringInvocation = new ActionInvocation(svc.getAction("SetMyString"));
        setMyStringInvocation.setInput("MyString1", "foo");
        svc.getExecutor(setMyStringInvocation.getAction()).execute(setMyStringInvocation);
        assertEquals(setMyStringInvocation.getFailure(), null);
        assertEquals(setMyStringInvocation.getOutput().length, 0);

        ActionInvocation getMyStringInvocation = new ActionInvocation(svc.getAction("GetMyString"));
        svc.getExecutor(getMyStringInvocation.getAction()).execute(getMyStringInvocation);
        assertEquals(getTargetInvocation.getFailure(), null);
        assertEquals(getMyStringInvocation.getOutput().length, 1);
        assertEquals(getMyStringInvocation.getOutput()[0].toString(), "foo");

    }

    /* ####################################################################################################### */

    public static class TestServiceOne {

        @UpnpStateVariable(sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpStateVariable(sendEvents = false)
        private MyString myString;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue", aliases ={"NewTargetValue1"}) boolean newTargetValue) {
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

        @UpnpAction
        public void setMyString(@UpnpInputArgument(name = "MyString", aliases ={"MyString1"}) MyString myString) {
            this.myString = myString;
        }

        @UpnpAction(name = "GetMyString", out = @UpnpOutputArgument(name = "MyString", getterName = "getMyString"))
        public void getMyStringDummy() {
        }

        public MyString getMyString() {
            return myString;
        }
    }

    public static class TestServiceTwo {

        @UpnpStateVariable(sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpStateVariable(sendEvents = false)
        private MyString myString;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue", aliases ={"NewTargetValue1"}) boolean newTargetValue) {
            target = newTargetValue;
            status = newTargetValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(name = "GetStatus", out = @UpnpOutputArgument(name = "ResultStatus", getterName = "getStatus"))
        public StatusHolder dummyStatus() {
            return new StatusHolder(status);
        }

        @UpnpAction
        public void setMyString(@UpnpInputArgument(name = "MyString", aliases ={"MyString1"}) MyString myString) {
            this.myString = myString;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "MyString", getterName = "getMyString"))
        public MyStringHolder getMyString() {
            return new MyStringHolder(myString);
        }

        public class StatusHolder {
            boolean st;

            public StatusHolder(boolean st) {
                this.st = st;
            }

            public boolean getStatus() {
                return st;
            }
        }

        public class MyStringHolder {
            MyString myString;

            public MyStringHolder(MyString myString) {
                this.myString = myString;
            }

            public MyString getMyString() {
                return myString;
            }
        }

    }

    public static class TestServiceThree {

        @UpnpStateVariable(sendEvents = false)
        private boolean target = false;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpStateVariable(sendEvents = false)
        private MyString myString;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = "NewTargetValue", aliases ={"NewTargetValue1"}) boolean newTargetValue) {
            target = newTargetValue;
            status = newTargetValue;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
        public boolean getTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
        public boolean getStatus() {
            return status;
        }

        @UpnpAction
        public void setMyString(@UpnpInputArgument(name = "MyString", aliases ={"MyString1"}) MyString myString) {
            this.myString = myString;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "MyString"))
        public MyString getMyString() {
            return myString;
        }
    }

    public static class MyString {
        private String s;

        public MyString(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }

        @Override
        public String toString() {
            return s;
        }
    }

}
