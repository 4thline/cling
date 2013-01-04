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
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Annotating a service implementation
 * <p>
 * The previously shown service class had a few annotations on the class itself, declaring
 * the name and version of the service. Then annotations on fields were used to declare the
 * state variables of the service and annotations on methods to declare callable actions.
 * </p>
 * <p>
 * Your service implementation might not have fields that directly map to UPnP state variables.
 * </p>
 * <div class="section">
 * <a class="citation" href="javadoc://example.localservice.SwitchPowerAnnotatedClass"/>
 * </div>
 * <p>
 * Cling tries to provide smart defaults. For example, the previously shown service classes
 * did not name the related state variable of action output arguments, as required by UPnP.
 * Cling will automatically detect that the <code>getStatus()</code> method is a JavaBean
 * getter method (its name starts with <code>get</code> or <code>is</code>) and use the
 * JavaBean property name to find the related state variable. In this case that would be
 * the JavaBean property <code>status</code> and Cling is also smart enough to know that
 * you really want the uppercase UPnP state variable named <code>Status</code>.
 * </p>
 * <div class="section">
 * <a class="citation" href="javadoc://example.localservice.SwitchPowerNamedStateVariable"/>
 * </div>
 * <p>
 * For the next example, let's assume you have a class that was already written, not
 * necessarily  as a service backend for UPnP but for some other purpose. You can't
 * redesign and rewrite your class without interrupting all existing code. Cling offers
 * some flexibility in the mapping of action methods, especially how the output of
 * an action call is obtained.
 * </p>
 * <div class="section">
 * <a class="citation" href="javadoc://example.localservice.SwitchPowerExtraGetter"/>
 * </div>
 * <p>
 * Alternatively, and especially if an action has several output arguments, you
 * can return multiple values wrapped in a JavaBean from your action method.
 * </p>
 * <div class="section">
 * <a class="citation" href="javadoc://example.localservice.SwitchPowerBeanReturn"/>
 * </div>
 */
public class BasicBindingTest {

    public LocalDevice createTestDevice(Class serviceClass) throws Exception {

        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        LocalService svc = binder.read(serviceClass);
        svc.setManager(new DefaultServiceManager(svc, serviceClass));

        return new LocalDevice(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                svc
        );
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() {


        try {
            return new LocalDevice[][]{
                    {createTestDevice(SwitchPowerNamedStateVariable.class)},
                    {createTestDevice(SwitchPowerAnnotatedClass.class)},
                    {createTestDevice(SwitchPowerExtraGetter.class)},
                    {createTestDevice(SwitchPowerBeanReturn.class)},
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

/*
        System.out.println("############################################################################");
        ServiceDescriptorBinder binder = new DefaultUpnpServiceConfiguration().getServiceDescriptorBinderUDA10();
        try {
            System.out.println(binder.generate(svc));
        } catch (DescriptorBindingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("############################################################################");

*/
        assertEquals(svc.getServiceId().toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:SwitchPower");
        assertEquals(svc.getServiceType().toString(), "urn:" + UDAServiceType.DEFAULT_NAMESPACE + ":service:SwitchPower:1");

        assertEquals(svc.getStateVariables().length, 2);
        assertEquals(svc.getStateVariable("Target").getTypeDetails().getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);
        assertEquals(svc.getStateVariable("Target").getTypeDetails().getDefaultValue(), "0");
        assertEquals(svc.getStateVariable("Target").getEventDetails().isSendEvents(), false);

        assertEquals(svc.getStateVariable("Status").getTypeDetails().getDatatype().getBuiltin(), Datatype.Builtin.BOOLEAN);
        assertEquals(svc.getStateVariable("Status").getTypeDetails().getDefaultValue(), "0");
        assertEquals(svc.getStateVariable("Status").getEventDetails().isSendEvents(), true);

        assertEquals(svc.getActions().length, 4); // Has 3 actions plus QueryStateVariableAction!

        assertEquals(svc.getAction("SetTarget").getName(), "SetTarget");
        assertEquals(svc.getAction("SetTarget").getArguments().length, 1);
        assertEquals(svc.getAction("SetTarget").getArguments()[0].getName(), "NewTargetValue");
        assertEquals(svc.getAction("SetTarget").getArguments()[0].getDirection(), ActionArgument.Direction.IN);
        assertEquals(svc.getAction("SetTarget").getArguments()[0].getRelatedStateVariableName(), "Target");

        assertEquals(svc.getAction("GetTarget").getName(), "GetTarget");
        assertEquals(svc.getAction("GetTarget").getArguments().length, 1);
        assertEquals(svc.getAction("GetTarget").getArguments()[0].getName(), "RetTargetValue");
        assertEquals(svc.getAction("GetTarget").getArguments()[0].getDirection(), ActionArgument.Direction.OUT);
        assertEquals(svc.getAction("GetTarget").getArguments()[0].getRelatedStateVariableName(), "Target");
        assertEquals(svc.getAction("GetTarget").getArguments()[0].isReturnValue(), true);

        assertEquals(svc.getAction("GetStatus").getName(), "GetStatus");
        assertEquals(svc.getAction("GetStatus").getArguments().length, 1);
        assertEquals(svc.getAction("GetStatus").getArguments()[0].getName(), "ResultStatus");
        assertEquals(svc.getAction("GetStatus").getArguments()[0].getDirection(), ActionArgument.Direction.OUT);
        assertEquals(svc.getAction("GetStatus").getArguments()[0].getRelatedStateVariableName(), "Status");
        assertEquals(svc.getAction("GetStatus").getArguments()[0].isReturnValue(), true);

    }

    @Test(dataProvider =  "devices")
    public void invokeActions(LocalDevice device) {
        // We mostly care about the binding without exceptions, but let's also test invocation
        LocalService svc = device.getServices()[0];

        ActionInvocation setTargetInvocation = new ActionInvocation(svc.getAction("SetTarget"));
        setTargetInvocation.setInput("NewTargetValue", true);
        svc.getExecutor(setTargetInvocation.getAction()).execute(setTargetInvocation);
        assertEquals(setTargetInvocation.getFailure(), null);
        assertEquals(setTargetInvocation.getOutput().length, 0);

        ActionInvocation getStatusInvocation = new ActionInvocation(svc.getAction("GetStatus"));
        svc.getExecutor(getStatusInvocation.getAction()).execute(getStatusInvocation);
        assertEquals(getStatusInvocation.getFailure(), null);
        assertEquals(getStatusInvocation.getOutput().length, 1);
        assertEquals(getStatusInvocation.getOutput()[0].toString(), "1");

        setTargetInvocation = new ActionInvocation(svc.getAction("SetTarget"));
        setTargetInvocation.setInput("NewTargetValue", false);
        svc.getExecutor(setTargetInvocation.getAction()).execute(setTargetInvocation);
        assertEquals(setTargetInvocation.getFailure(), null);
        assertEquals(setTargetInvocation.getOutput().length, 0);

        ActionInvocation queryStateVariableInvocation = new ActionInvocation(svc.getAction("QueryStateVariable"));
        queryStateVariableInvocation.setInput("varName", "Status");
        svc.getExecutor(queryStateVariableInvocation.getAction()).execute(queryStateVariableInvocation);
        assertEquals(queryStateVariableInvocation.getFailure(), null);
        assertEquals(queryStateVariableInvocation.getOutput().length, 1);
        assertEquals(queryStateVariableInvocation.getOutput()[0].toString(), "0");

    }

}
