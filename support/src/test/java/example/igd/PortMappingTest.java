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
package example.igd;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.callback.PortMappingEntryGet;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Mapping a NAT port
 * <p>
 * Cling Support contains all the neccessary functionality, creating a port mapping
 * on all NAT routers on a network requires only three lines of code:
 * </p>
 * <a class="citation" href="javacode://this#addDeleteWithListener" style="include: PM1;"/>
 * <p>
 * The first line creates a port mapping configuration with the external/internal port, an
 * internal host IP, the protocol and an optional description.
 * </p>
 * <p>
 * The second line starts the UPnP service with a special listener. This listener
 * will add the port mapping on any <em>InternetGatewayDevice</em> with a <em>WANIPConnection</em>
 * or a <em>WANPPPConnection</em> service as soon as it is discovered. You should immediately start
 * a <code>ControlPoint#search()</code> for all devices on your network, this triggers a response
 * and discovery of all NAT routers, activating the port mapping.
 * </p>
 * <p>
 * The listener will also delete the port mapping when you stop the UPnP stack through
 * <code>UpnpService#shutdown()</code>, usually before your application quits. If you forget
 * to shutdown the stack the port mapping will remain on the <em>InternetGatewayDevice</em>
 * - the default lease duration is <code>0</code>!
 * </p>
 * <p>
 * If anything goes wrong, log messages with <code>WARNING</code> level will be created on the
 * category <code>org.fourthline.cling.support.igd.PortMappingListener</code>. You can override the
 * <code>PortMappingListener#handleFailureMessage(String)</code> method to customize this behavior.
 * </p>
 * <p>
 * Alternatively, you can manually add and delete port mappings on an already discovered device with
 * the following ready-to-use action callbacks:
 * </p>
 * <a class="citation" href="javacode://this#addDeleteManually" style="include: PM1; exclude: EXC1, EXC2"/>
 *
 */
public class PortMappingTest {

    @Test
    public void addDeleteWithListener() throws Exception {

        PortMapping desiredMapping =                                    // DOC: PM1
                new PortMapping(
                        8123,
                        "192.168.0.123",
                        PortMapping.Protocol.TCP,
                        "My Port Mapping"
                );

        UpnpService upnpService =
                new UpnpServiceImpl(
                        new PortMappingListener(desiredMapping)
                );

        upnpService.getControlPoint().search();                         // DOC: PM1

        LocalDevice device = IGDSampleData.createIGDevice(TestConnection.class);
        upnpService.getRegistry().addDevice(device);

        upnpService.shutdown();

        LocalService<TestConnection> service = device.findService(new UDAServiceId("WANIPConnection"));
        for (boolean test : service.getManager().getImplementation().tests) {
            assert test;
        }

    }

    @Test
    public void addDeleteManually() throws Exception {

        final boolean[] tests = new boolean[2];

        PortMapping desiredMapping =
                new PortMapping(
                        8123,
                        "192.168.0.123",
                        PortMapping.Protocol.TCP,
                        "My Port Mapping"
                );

        UpnpService upnpService = new UpnpServiceImpl();

        LocalDevice device = IGDSampleData.createIGDevice(TestConnection.class);
        upnpService.getRegistry().addDevice(device);

        Service service = device.findService(new UDAServiceId("WANIPConnection"));         // DOC: PM1

        upnpService.getControlPoint().execute(
            new PortMappingAdd(service, desiredMapping) {

                @Override
                public void success(ActionInvocation invocation) {
                    // All OK
                    tests[0] = true;                                                        // DOC: EXC1
                }

                @Override
                public void failure(ActionInvocation invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    // Something is wrong
                }
            }
        );

        final PortMapping[] mapping = {null};
        upnpService.getControlPoint().execute(
                new PortMappingEntryGet(service, 0L) {

                    @Override
                    public void success(PortMapping portMapping) {
                        // All OK
                        mapping[0] = portMapping;                                       // DOC: EXC1
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        // Something is wrong
                    }
                }
        );
        assertEquals(mapping[0].getInternalClient(), "192.168.0.123");
        assertEquals(mapping[0].getInternalPort().getValue().longValue(), 8123);
        assertTrue(mapping[0].isEnabled());

        upnpService.getControlPoint().execute(
            new PortMappingDelete(service, desiredMapping) {

                @Override
                public void success(ActionInvocation invocation) {
                    // All OK
                    tests[1] = true;                                                        // DOC: EXC2
                }

                @Override
                public void failure(ActionInvocation invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    // Something is wrong
                }
            }
        );                                                                                      // DOC: PM1

        for (boolean test : tests) {
            assert test;
        }
        for (boolean test : ((LocalService<TestConnection>)service).getManager().getImplementation().tests) {
            assert test;
        }

    }

    public static class TestConnection extends IGDSampleData.WANIPConnectionService {

        boolean[] tests = new boolean[2];

        @Override
        protected void addPortMapping(PortMapping portMapping) {
            assertEquals(portMapping.getExternalPort().getValue(), new Long(8123));
            assertEquals(portMapping.getInternalPort().getValue(), new Long(8123));
            assertEquals(portMapping.getProtocol(), PortMapping.Protocol.TCP);
            assertEquals(portMapping.getDescription(), "My Port Mapping");
            assertEquals(portMapping.getInternalClient(), "192.168.0.123");
            assertEquals(portMapping.getLeaseDurationSeconds().getValue(), new Long(0));
            assertEquals(portMapping.hasRemoteHost(), false);
            assertEquals(portMapping.hasDescription(), true);
            tests[0] = true;
        }

        @Override
        protected void deletePortMapping(PortMapping portMapping) {
            assertEquals(portMapping.getExternalPort().getValue(), new Long(8123));
            assertEquals(portMapping.getProtocol(), PortMapping.Protocol.TCP);
            assertEquals(portMapping.hasRemoteHost(), false);
            tests[1] = true;
        }

        public PortMapping getGenericPortMappingEntry(UnsignedIntegerTwoBytes index) {
            assertEquals(index.getValue().longValue(), 0);
            return new PortMapping(
                    8123,
                    "192.168.0.123",
                    PortMapping.Protocol.TCP,
                    "My Port Mapping"
            );
        }
    }

    class UpnpServiceImpl extends MockUpnpService {
        UpnpServiceImpl(RegistryListener... registryListeners) {
            super();
            for (RegistryListener registryListener : registryListeners) {
                getRegistry().addListener(registryListener);
            }
        }
    }

}
