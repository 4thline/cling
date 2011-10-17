/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fourthline.cling.test.ssdp;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.ExpirationDetails;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;

public class RegistryExpirationTest {

    @Test
    public void addAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(1)
        );
        upnpService.getRegistry().addDevice(rd);
        
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        Thread.sleep(3000);

        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);

        upnpService.shutdown();
    }

    @Test
    public void overrideAgeThenAddAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true) {
            @Override
            public UpnpServiceConfiguration getConfiguration() {
                final UpnpServiceConfiguration wrapped = super.getConfiguration();
                return new UpnpServiceConfiguration() {
                    @Override
                    public NetworkAddressFactory createNetworkAddressFactory() {
                        return wrapped.createNetworkAddressFactory();
                    }

                    @Override
                    public DatagramProcessor getDatagramProcessor() {
                        return wrapped.getDatagramProcessor();
                    }

                    @Override
                    public SOAPActionProcessor getSoapActionProcessor() {
                        return wrapped.getSoapActionProcessor();
                    }

                    @Override
                    public GENAEventProcessor getGenaEventProcessor() {
                        return wrapped.getGenaEventProcessor();
                    }

                    @Override
                    public StreamClient createStreamClient() {
                        return wrapped.createStreamClient();
                    }

                    @Override
                    public MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory) {
                        return wrapped.createMulticastReceiver(networkAddressFactory);
                    }

                    @Override
                    public DatagramIO createDatagramIO(NetworkAddressFactory networkAddressFactory) {
                        return wrapped.createDatagramIO(networkAddressFactory);
                    }

                    @Override
                    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
                        return wrapped.createStreamServer(networkAddressFactory);
                    }

                    @Override
                    public Executor getMulticastReceiverExecutor() {
                        return wrapped.getMulticastReceiverExecutor();
                    }

                    @Override
                    public Executor getDatagramIOExecutor() {
                        return wrapped.getDatagramIOExecutor();
                    }

                    @Override
                    public Executor getStreamServerExecutor() {
                        return wrapped.getStreamServerExecutor();
                    }

                    @Override
                    public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                        return wrapped.getDeviceDescriptorBinderUDA10();
                    }

                    @Override
                    public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
                        return wrapped.getServiceDescriptorBinderUDA10();
                    }

                    @Override
                    public ServiceType[] getExclusiveServiceTypes() {
                        return wrapped.getExclusiveServiceTypes();
                    }

                    @Override
                    public int getRegistryMaintenanceIntervalMillis() {
                        return wrapped.getRegistryMaintenanceIntervalMillis();
                    }

                    @Override
                    public Integer getRemoteDeviceMaxAgeSeconds() {
                        return 0; // Override the expiration timeout!
                    }

                    @Override
                    public Executor getAsyncProtocolExecutor() {
                        return wrapped.getAsyncProtocolExecutor();
                    }

                    @Override
                    public Executor getSyncProtocolExecutor() {
                        return wrapped.getSyncProtocolExecutor();
                    }

                    @Override
                    public Namespace getNamespace() {
                        return wrapped.getNamespace();
                    }

                    @Override
                    public Executor getRegistryMaintainerExecutor() {
                        return wrapped.getRegistryMaintainerExecutor();
                    }

                    @Override
                    public Executor getRegistryListenerExecutor() {
                        return wrapped.getRegistryListenerExecutor();
                    }

                    @Override
                    public void shutdown() {
                        wrapped.shutdown();
                    }
                };
            }
        };

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(1)
        );
        upnpService.getRegistry().addDevice(rd);

        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        Thread.sleep(3000);

        // Still registered!
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update should not change the expiration time
        upnpService.getRegistry().update(rd.getIdentity());

        Thread.sleep(3000);

        // Still registered!
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        upnpService.shutdown();
    }

    @Test
    public void addAndUpdateAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(2)
        );

        // Add it to registry
        upnpService.getRegistry().addDevice(rd);
        Thread.sleep(1000);
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update it in registry
        upnpService.getRegistry().addDevice(rd);
        Thread.sleep(1000);
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Update again
        upnpService.getRegistry().update(rd.getIdentity());
        Thread.sleep(1000);
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        // Wait for expiration
        Thread.sleep(3000);
        Assert.assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);


        upnpService.shutdown();
    }

    @Test
    public void addResourceAndExpire() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        Resource resource = new Resource(URI.create("/this/is/a/test"), "foo");
        upnpService.getRegistry().addResource(resource, 2);

        Assert.assertEquals(upnpService.getRegistry().getResources().size(), 1);

        Thread.sleep(4000);

        Assert.assertEquals(upnpService.getRegistry().getResources().size(), 0);

        upnpService.shutdown();
    }

    @Test
    public void addResourceAndMaintain() throws Exception {

        MockUpnpService upnpService = new MockUpnpService(false, true);

        final TestRunnable testRunnable = new TestRunnable();

        Resource resource = new Resource<String>(URI.create("/this/is/a/test"), "foo") {
            @Override
            public void maintain(List<Runnable> pendingExecutions, ExpirationDetails expirationDetails) {
                if (expirationDetails.getSecondsUntilExpiration() == 1) {
                    pendingExecutions.add(testRunnable);
                }
            }
        };
        upnpService.getRegistry().addResource(resource, 2);

        Assert.assertEquals(upnpService.getRegistry().getResources().size(), 1);

        Thread.sleep(2000);

        Assert.assertEquals(testRunnable.wasExecuted, true);

        upnpService.shutdown();
    }

    protected class TestRunnable implements Runnable {
        boolean wasExecuted = false;

        public void run() {
            wasExecuted = true;
        }
    }

}
