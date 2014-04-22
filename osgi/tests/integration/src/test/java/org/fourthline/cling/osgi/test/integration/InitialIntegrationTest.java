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

package org.fourthline.cling.osgi.test.integration;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.osgi.test.data.TestData;
import org.fourthline.cling.osgi.test.data.TestDataFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

/**
 * TODO: This is broken, probably. There are many error messages in between all
 * the OS-shit-eye junk. Test still completes successfully.
 */
@ExamReactorStrategy(PerClass.class)
@RunWith(PaxExam.class)
public class InitialIntegrationTest extends BaseIntegration {

	static private final String INITIAL_TEST_DATA_ID = "initial";
	static private final String SET_TEST_DATA_ID = "set";

	@Inject
	BundleContext bundleContext = null;

	@Before
	@Override
	public void setup() throws Exception {
		super.setup();
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();
	}

    @org.ops4j.pax.exam.Configuration
    public Option[] config() {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .type("tar.gz")
                .versionAsInProject();

        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .classifier("features")
                .type("xml")
                .versionAsInProject();
        return new Option[] {
                //debugConfiguration("5005", true),
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                logLevel(LogLevelOption.LogLevel.INFO),
                KarafDistributionOption.features(karafStandardRepo, "scr, eventadmin"),

                mavenBundle().groupId("org.osgi").artifactId("org.osgi.core").versionAsInProject().start(),
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").versionAsInProject().start(),

                mavenBundle().groupId("org.fourthline.cling.osgi").artifactId("seamless-http").versionAsInProject().start(),
                mavenBundle().groupId("org.fourthline.cling.osgi").artifactId("seamless-util").versionAsInProject().start(),
                mavenBundle().groupId("org.fourthline.cling.osgi").artifactId("seamless-xml").versionAsInProject().start(),

                mavenBundle().groupId("org.fourthline.cling.osgi").artifactId("httpcomponents-http-core").versionAsInProject().start(),
                mavenBundle().groupId("org.fourthline.cling.osgi").artifactId("httpcomponents-http-client").versionAsInProject().start(),

                mavenBundle().groupId("commons-codec").artifactId("commons-codec").versionAsInProject().start(),

                mavenBundle().groupId("org.fourthline.cling").artifactId("cling-core").versionAsInProject().start(),
                mavenBundle().groupId("org.fourthline.cling").artifactId("cling-osgi-tests-common").versionAsInProject().start(),

                mavenBundle().groupId("org.fourthline.cling").artifactId("cling-osgi-basedriver").versionAsInProject().start(),
                mavenBundle().groupId("org.fourthline.cling").artifactId("cling-osgi-tests-devices-simple").versionAsInProject().start(),




        };
    }



	static private final String DEVICE_TYPE = "urn:schemas-4thline-com:device:simple-test:1";
	static private final String SERVICE_TYPE = "urn:schemas-4thline-com:service:SimpleTest:1";

	class GetTargetActionInvocation extends ActionInvocation {
	    GetTargetActionInvocation(Service service, String name) {
	        super(service.getAction(name));
	        try {

	        } catch (InvalidValueException ex) {
	            System.err.println(ex.getMessage());
	        }
	    }
	}

    public void doSimpleDeviceGetAction(final String name, String testDataId) {
        Device device = getDevice(ServiceType.valueOf(SERVICE_TYPE));
        assertNotNull(device);
        Service service = getService(device, ServiceType.valueOf(SERVICE_TYPE));
        assertNotNull(service);
        Action action = getAction(service, name);
        assertNotNull(action);

        final TestData data = TestDataFactory.getInstance().getTestData(testDataId);
        assertNotNull(data);

        final boolean[] tests = new boolean[24];

        ActionInvocation setTargetInvocation = new GetTargetActionInvocation(service, name);
        getUpnpService().getControlPoint().execute(
                new ActionCallback(setTargetInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        System.out.printf("Successfully called action: %s\n", name);
                        ActionArgumentValue[] outputs = invocation.getOutput();
                        int i=0;
                        for (ActionArgumentValue output : outputs) {
                            ActionArgument argument = output.getArgument();
                            String name =  argument.getName();
                            String type = (String) name;
                            Object value = output.getValue();
                            Object desired = data.getOSGiUPnPValue(name, type);

                            System.out.println("Name: " + name + " type: " + type + " value: " + value + " desired: " +desired);

                            assertTrue(validate(name, type, value, desired));
                            tests[i++] = true;
                        }
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        System.out.printf("Failure called action: %s\n", name);
                        ActionArgumentValue[] outputs = invocation.getOutput();
                        for (ActionArgumentValue output : outputs) {
                            ActionArgument argument = output.getArgument();
                            String name = argument.getName();
                            String type = (String) name;
                            Object value = output.getValue();
                            Object desired = data.getOSGiUPnPValue(name, type);

                            System.out.println("Name: " + name + " type: " + type + " value: " + value + " desired: " + desired);

                            assertTrue(validate(name, type, value, desired));

                            tests[0] = false;
                        }
                    }
                }
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (boolean test : tests) {
            assertTrue(test);
        }
    }
	
	class SetTargetActionInvocation extends ActionInvocation {
	    SetTargetActionInvocation(Service service, String name, TestData data) {
	        super(service.getAction(name));
    		//System.out.printf("@@@ name: %s  inputs: %d\n", name, getAction().getInputArguments().length);
	        try {
	        	for (ActionArgument argument : getAction().getInputArguments()) {
	        		//System.out.printf("@@@ argument: %s\n", argument);
	        		String type = argument.getDatatype().getBuiltin().getDescriptorName();
	        		//System.out.printf("@@@ type: %s\n", type);
	        		
	        		Object object = data.getOSGiUPnPValue(argument.getName(), type);
	        		//System.out.printf("@@@ object: %s\n", object);
	        		object = data.getClingUPnPValue(type, object);
	        		//System.out.printf("@@@ type: %s  value: %s (%s)\n", type, object, object.getClass().getName());
	        		setInput(argument.getName(), object);
	        	}
	        } catch (InvalidValueException ex) {
	            System.err.println(ex.getMessage());
	        }
	    }
	}

	public void doSimpleDeviceSetAction(final String name, String testDataId) {
		Device device = getDevice(ServiceType.valueOf(SERVICE_TYPE));
		assertNotNull(device);
		Service service = getService(device, ServiceType.valueOf(SERVICE_TYPE));
		assertNotNull(service);
		Action action = getAction(service, name);
		assertNotNull(action);
		
		TestData data = TestDataFactory.getInstance().getTestData(testDataId);
		assertNotNull(data);
		
        final boolean[] tests = new boolean[1];

        ActionInvocation setTargetInvocation = new SetTargetActionInvocation(service, name, data);
        getUpnpService().getControlPoint().execute(
                new ActionCallback(setTargetInvocation) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        System.out.printf("Successfully called action: %s\n", name);
                        // TODO: What is going on here...
                    }

					@Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        System.err.println(defaultMsg);
                        
                        assertTrue(true);
                    }
                }
        );
        
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testSimpleDeviceGetAllVariablesAction() {
		doSimpleDeviceGetAction("GetAllVariables", INITIAL_TEST_DATA_ID);
	}

	@Test
	public void testSimpleDeviceSetAllVariablesAction() {
		doSimpleDeviceSetAction("SetAllVariables", SET_TEST_DATA_ID);
		doSimpleDeviceGetAction("GetAllVariables", SET_TEST_DATA_ID);
	}
}
