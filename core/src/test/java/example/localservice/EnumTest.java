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
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Working with enums
 * <p>
 * Java <code>enum</code>'s are special, unfortunately: Cling can convert
 * your enum value into a string for transport in UPnP messages, but you
 * have to convert it back manually from a string. This is shown in the
 * following service example:
 * </p>
 * <a class="citation" href="javacode://example.localservice.MyServiceWithEnum" style="include: INC1"/>
 * <p>
 * Cling will automatically assume that the datatype is a UPnP string if the
 * field (or getter) or getter Java type is an enum. Furthermore, an
 * <code>&lt;allowedValueList&gt;</code> will be created in your service descriptor
 * XML, so control points know that this state variable has in fact a defined
 * set of possible values.
 * </p>
 */
public class EnumTest {

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
                    {createTestDevice(MyServiceWithEnum.class)},
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

        assertEquals(svc.getStateVariables().length, 1);
        assertEquals(svc.getStateVariables()[0].getTypeDetails().getDatatype().getBuiltin(), Datatype.Builtin.STRING);

        assertEquals(svc.getActions().length, 3); // Has 2 actions plus QueryStateVariableAction!

        assertEquals(svc.getAction("GetColor").getArguments().length, 1);
        assertEquals(svc.getAction("GetColor").getArguments()[0].getName(), "Out");
        assertEquals(svc.getAction("GetColor").getArguments()[0].getDirection(), ActionArgument.Direction.OUT);
        assertEquals(svc.getAction("GetColor").getArguments()[0].getRelatedStateVariableName(), "Color");

        assertEquals(svc.getAction("SetColor").getArguments().length, 1);
        assertEquals(svc.getAction("SetColor").getArguments()[0].getName(), "In");
        assertEquals(svc.getAction("SetColor").getArguments()[0].getDirection(), ActionArgument.Direction.IN);
        assertEquals(svc.getAction("SetColor").getArguments()[0].getRelatedStateVariableName(), "Color");

    }

    @Test(dataProvider = "devices")
    public void invokeActions(LocalDevice device) {
        LocalService svc = device.getServices()[0];

        ActionInvocation setColor = new ActionInvocation(svc.getAction("SetColor"));
        setColor.setInput("In", MyServiceWithEnum.Color.Blue);
        svc.getExecutor(setColor.getAction()).execute(setColor);
        assertEquals(setColor.getFailure(), null);
        assertEquals(setColor.getOutput().length, 0);

        ActionInvocation getColor = new ActionInvocation(svc.getAction("GetColor"));
        svc.getExecutor(getColor.getAction()).execute(getColor);
        assertEquals(getColor.getFailure(), null);
        assertEquals(getColor.getOutput().length, 1);
        assertEquals(getColor.getOutput()[0].toString(), MyServiceWithEnum.Color.Blue.name());

    }
}
