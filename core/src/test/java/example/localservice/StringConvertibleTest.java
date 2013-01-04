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

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * String value converters
 * <p>
 * Consider the following service class with all state variables of
 * <code>string</code> UPnP datatype - but with a much more specific
 * Java type:
 * </p>
 * <a class="citation" href="javacode://example.localservice.MyServiceWithStringConvertibles" style="include: INC1"/>
 * <p>
 * The state variables are all of UPnP datatype <code>string</code> because
 * Cling knows that the Java type of the annotated field is "string convertible".
 * This is always the case for <code>java.net.URI</code> and <code>java.net.URL</code>.
 * </p>
 * <p>
 * Any other Java type you'd like to use for automatic string conversion has to be named
 * in the <code>@UpnpService</code> annotation on the class, like the
 * <code>MyStringConvertible</code>. Note that these types have to
 * have an appropriate <code>toString()</code> method and a single argument constructor
 * that accepts a <code>java.lang.String</code> ("from string" conversion).
 * </p>
 * <p>
 * The <code>List&lt;Integer></code> is the collection you'd use in your service
 * implementation to group several numbers. Let's assume that for UPnP communication
 * you need a comma-separated representation of the individual values in a string,
 * as is required by many of the UPnP A/V specifications. First, tell Cling that
 * the state variable really is a string datatype, it can't infer that
 * from the field type. Then, if an action has this output argument, instead of
 * manually creating the comma-separated string you pick the appropriate converter
 * from the classes in <code>org.fourthline.cling.model.types.csv.*</code> and return
 * it from your action method. These are actually <code>java.util.List</code>
 * implementations, so you could use them <em>instead</em> of
 * <code>java.util.List</code> if you don't care about the dependency. Any action
 * input argument value can also be converted from a comma-separated string
 * representation to a list automatically - all you have to do is use the
 * CSV converter class as an input argument type.
 * </p>
 */
public class StringConvertibleTest {

        public LocalDevice createTestDevice(Class serviceClass) throws Exception {

        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        LocalService svc = binder.read(serviceClass);
        svc.setManager(new DefaultServiceManager(svc, serviceClass));

        return new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new DeviceType("mydomain", "CustomDevice", 1),
                new DeviceDetails("A Custom Device"),
                svc
        );
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() {


        try {
            return new LocalDevice[][]{
                    {createTestDevice(MyServiceWithStringConvertibles.class)},
            };
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            // Damn testng swallows exceptions in provider/factory methods
            throw new RuntimeException(ex);
        }
    }

    @Test(dataProvider = "devices")
    public void validateBinding(LocalDevice device) {

        LocalService svc = device.getServices()[0];

        assertEquals(svc.getStateVariables().length, 4);
        for (StateVariable stateVariable : svc.getStateVariables()) {
            assertEquals(stateVariable.getTypeDetails().getDatatype().getBuiltin(), Datatype.Builtin.STRING);
        }

        assertEquals(svc.getActions().length, 9); // Has 8 actions plus QueryStateVariableAction!

        assertEquals(svc.getAction("SetMyURL").getArguments().length, 1);
        assertEquals(svc.getAction("SetMyURL").getArguments()[0].getName(), "In");
        assertEquals(svc.getAction("SetMyURL").getArguments()[0].getDirection(), ActionArgument.Direction.IN);
        assertEquals(svc.getAction("SetMyURL").getArguments()[0].getRelatedStateVariableName(), "MyURL");
        // The others are all the same...

    }

    @Test(dataProvider =  "devices")
    public void invokeActions(LocalDevice device) {
        LocalService svc = device.getServices()[0];

        ActionInvocation setMyURL = new ActionInvocation(svc.getAction("SetMyURL"));
        setMyURL.setInput("In", "http://foo/bar");
        svc.getExecutor(setMyURL.getAction()).execute(setMyURL);
        assertEquals(setMyURL.getFailure(), null);
        assertEquals(setMyURL.getOutput().length, 0);

        ActionInvocation getMyURL = new ActionInvocation(svc.getAction("GetMyURL"));
        svc.getExecutor(getMyURL.getAction()).execute(getMyURL);
        assertEquals(getMyURL.getFailure(), null);
        assertEquals(getMyURL.getOutput().length, 1);
        assertEquals(getMyURL.getOutput()[0].toString(), "http://foo/bar");

        ActionInvocation setMyURI = new ActionInvocation(svc.getAction("SetMyURI"));
        setMyURI.setInput("In", "http://foo/bar");
        svc.getExecutor(setMyURI.getAction()).execute(setMyURI);
        assertEquals(setMyURI.getFailure(), null);
        assertEquals(setMyURI.getOutput().length, 0);

        ActionInvocation getMyURI = new ActionInvocation(svc.getAction("GetMyURI"));
        svc.getExecutor(getMyURI.getAction()).execute(getMyURI);
        assertEquals(getMyURI.getFailure(), null);
        assertEquals(getMyURI.getOutput().length, 1);
        assertEquals(getMyURI.getOutput()[0].toString(), "http://foo/bar");

        ActionInvocation setMyNumbers = new ActionInvocation(svc.getAction("SetMyNumbers"));
        setMyNumbers.setInput("In", "1,2,3");
        svc.getExecutor(setMyNumbers.getAction()).execute(setMyNumbers);
        assertEquals(setMyNumbers.getFailure(), null);
        assertEquals(setMyNumbers.getOutput().length, 0);

        ActionInvocation getMyNumbers = new ActionInvocation(svc.getAction("GetMyNumbers"));
        svc.getExecutor(getMyNumbers.getAction()).execute(getMyNumbers);
        assertEquals(getMyNumbers.getFailure(), null);
        assertEquals(getMyNumbers.getOutput().length, 1);
        assertEquals(getMyNumbers.getOutput()[0].toString(), "1,2,3");

        ActionInvocation setMyStringConvertible = new ActionInvocation(svc.getAction("SetMyStringConvertible"));
        setMyStringConvertible.setInput("In", "foobar");
        svc.getExecutor(setMyStringConvertible.getAction()).execute(setMyStringConvertible);
        assertEquals(setMyStringConvertible.getFailure(), null);
        assertEquals(setMyStringConvertible.getOutput().length, 0);

        ActionInvocation getMyStringConvertible = new ActionInvocation(svc.getAction("GetMyStringConvertible"));
        svc.getExecutor(getMyStringConvertible.getAction()).execute(getMyStringConvertible);
        assertEquals(getMyStringConvertible.getFailure(), null);
        assertEquals(getMyStringConvertible.getOutput().length, 1);
        assertEquals(getMyStringConvertible.getOutput()[0].toString(), "foobar");

    }
}
