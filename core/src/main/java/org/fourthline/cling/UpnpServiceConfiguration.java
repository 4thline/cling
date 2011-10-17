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

package org.fourthline.cling;

import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;

import java.util.concurrent.Executor;

/**
 * Shared configuration data of the UPnP stack..
 * <p>
 * This interface offers methods for retrieval of configuration data by the
 * {@link org.fourthline.cling.transport.Router} and the {@link org.fourthline.cling.registry.Registry},
 * as well as other parts of the UPnP stack.
 * </p>
 * <p>
 * You can re-use this interface if you implement a subclass of {@link UpnpServiceImpl} or
 * if you create a new implementation of {@link UpnpService}.
 * </p>
 *
 * @author Christian Bauer
 */
public interface UpnpServiceConfiguration {

    /**
     * @return A new instance of the {@link org.fourthline.cling.transport.spi.NetworkAddressFactory} interface.
     */
    public NetworkAddressFactory createNetworkAddressFactory();

    /**
     * @return The shared implementation of {@link org.fourthline.cling.transport.spi.DatagramProcessor}.
     */
    public DatagramProcessor getDatagramProcessor();

    /**
     * @return The shared implementation of {@link org.fourthline.cling.transport.spi.SOAPActionProcessor}.
     */
    public SOAPActionProcessor getSoapActionProcessor();

    /**
     * @return The shared implementation of {@link org.fourthline.cling.transport.spi.GENAEventProcessor}.
     */
    public GENAEventProcessor getGenaEventProcessor();

    /**
     * @return A new instance of the {@link org.fourthline.cling.transport.spi.StreamClient} interface.
     */
    public StreamClient createStreamClient();

    /**
     * @param networkAddressFactory The configured {@link org.fourthline.cling.transport.spi.NetworkAddressFactory}.
     * @return A new instance of the {@link org.fourthline.cling.transport.spi.MulticastReceiver} interface.
     */
    public MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory);

    /**
     * @param networkAddressFactory The configured {@link org.fourthline.cling.transport.spi.NetworkAddressFactory}.
     * @return A new instance of the {@link org.fourthline.cling.transport.spi.DatagramIO} interface.
     */
    public DatagramIO createDatagramIO(NetworkAddressFactory networkAddressFactory);

    /**
     * @param networkAddressFactory The configured {@link org.fourthline.cling.transport.spi.NetworkAddressFactory}.
     * @return A new instance of the {@link org.fourthline.cling.transport.spi.StreamServer} interface.
     */
    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory);

    /**
     * @return The executor which runs the listening background threads for multicast datagrams.
     */
    public Executor getMulticastReceiverExecutor();

    /**
     * @return The executor which runs the listening background threads for unicast datagrams.
     */
    public Executor getDatagramIOExecutor();

    /**
     * @return The executor which runs the listening background threads for HTTP requests.
     */
    public Executor getStreamServerExecutor();

    /**
     * @return The shared implementation of {@link org.fourthline.cling.binding.xml.DeviceDescriptorBinder} for the UPnP 1.0 Device Architecture..
     */
    public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10();

    /**
     * @return The shared implementation of {@link org.fourthline.cling.binding.xml.ServiceDescriptorBinder} for the UPnP 1.0 Device Architecture..
     */
    public ServiceDescriptorBinder getServiceDescriptorBinderUDA10();

    /**
     * Returns service types that can be handled by this UPnP stack, all others will be ignored.
     * <p>
     * Return <code>null</code> to completely disable remote device and service discovery.
     * All incoming notifications and search responses will then be dropped immediately.
     * This is mostly useful in applications that only provide services with no (remote)
     * control point functionality.
     * </p>
     * <p>
     * Note that a discovered service type with version 2 or 3 will match an exclusive
     * service type with version 1. UPnP services are required to be backwards
     * compatible, version 2 is a superset of version 1, and version 3 is a superset
     * of version 2, etc.
     * </p>
     *
     * @return An array of service types that are exclusively discovered, no other service will
     *         be discovered. A <code>null</code> return value will disable discovery!
     *         An empty array means all services will be discovered.
     */
    public ServiceType[] getExclusiveServiceTypes();

    /**
     * @return The time in milliseconds to wait between each registry maintenance operation.
     */
    public int getRegistryMaintenanceIntervalMillis();

    /**
     * Returns the time in seconds a remote device will be registered until it is expired.
     * <p>
     * This setting is useful on systems which do not support multicast networking
     * (Android on HTC phones, for example). On such a system you will not receive messages when a
     * remote device disappears from the network and you will not receive its periodic heartbeat
     * alive messages. Only an initial search response (UDP unicast) has been received from the
     * remote device, with its proposed maximum age. To avoid (early) expiration of the remote
     * device, you can override its maxium age with this configuration setting, ignoring the
     * initial maximum age sent by the device. You most likely want to return
     * <code>0</code> in this case, so that the remote device is never expired unless you
     * manually remove it from the {@link org.fourthline.cling.registry.Registry}. You typically remove
     * the device when an action or GENA subscription request to the remote device failed.
     * </p>
     *
     * @return <code>null</code> (the default) to accept the remote device's proposed maximum age, or
     *         <code>0</code> for unlimited age, or a value in seconds.
     */
    public Integer getRemoteDeviceMaxAgeSeconds();

    /**
     * @return The executor which runs the processing of asynchronous aspects of the UPnP stack (discovery).
     */
    public Executor getAsyncProtocolExecutor();

    /**
     * @return The executor which runs the processing of synchronous aspects of the UPnP stack (description, control, GENA).
     */
    public Executor getSyncProtocolExecutor();

    /**
     * @return An instance of {@link org.fourthline.cling.model.Namespace} for this UPnP stack.
     */
    public Namespace getNamespace();

    /**
     * @return The executor which runs the background thread for maintainting the registry.
     */
    public Executor getRegistryMaintainerExecutor();

    /**
     * @return The executor which runs the notification threads of registry listeners.
     */
    public Executor getRegistryListenerExecutor();

    /**
     * Called by the {@link org.fourthline.cling.UpnpService} on shutdown, useful to e.g. shutdown thread pools.
     */
    public void shutdown();

}
