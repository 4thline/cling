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
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.transport.impl.DatagramIOConfigurationImpl;
import org.fourthline.cling.transport.impl.DatagramIOImpl;
import org.fourthline.cling.transport.impl.DatagramProcessorImpl;
import org.fourthline.cling.transport.impl.GENAEventProcessorImpl;
import org.fourthline.cling.transport.impl.MulticastReceiverConfigurationImpl;
import org.fourthline.cling.transport.impl.MulticastReceiverImpl;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.impl.SOAPActionProcessorImpl;
import org.fourthline.cling.transport.impl.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.impl.StreamClientImpl;
import org.fourthline.cling.transport.impl.StreamServerConfigurationImpl;
import org.fourthline.cling.transport.impl.StreamServerImpl;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.StreamServer;
import org.seamless.util.Exceptions;

import javax.enterprise.inject.Alternative;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Default configuration data of a typical UPnP stack.
 * <p>
 * This configuration utilizes the default network transport implementation found in
 * {@link org.fourthline.cling.transport.impl}.
 * </p>
 * <p>
 * This configuration utilizes the DOM default descriptor binders found in
 * {@link org.fourthline.cling.binding.xml}.
 * </p>
 * <p>
 * The thread <code>Executor</code> is an <code>Executors.newCachedThreadPool()</code> with
 * a custom {@link ClingThreadFactory} (it only sets a thread name).
 * </p>
 * <p>
 * Note that this pool is effectively unlimited, so the number of threads will
 * grow (and shrink) as needed - or restricted by your JVM.
 * </p>
 * <p>
 * The default {@link org.fourthline.cling.model.Namespace} is configured without any
 * base path or prefix.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class DefaultUpnpServiceConfiguration implements UpnpServiceConfiguration {

    private static Logger log = Logger.getLogger(DefaultUpnpServiceConfiguration.class.getName());

    final private int streamListenPort;

    final private Executor defaultExecutor;

    final private DatagramProcessor datagramProcessor;
    final private SOAPActionProcessor soapActionProcessor;
    final private GENAEventProcessor genaEventProcessor;

    final private DeviceDescriptorBinder deviceDescriptorBinderUDA10;
    final private ServiceDescriptorBinder serviceDescriptorBinderUDA10;

    final private Namespace namespace;

    /**
     * Defaults to port '0', ephemeral.
     */
    public DefaultUpnpServiceConfiguration() {
        this(NetworkAddressFactoryImpl.DEFAULT_TCP_HTTP_LISTEN_PORT);
    }

    public DefaultUpnpServiceConfiguration(int streamListenPort) {
        this(streamListenPort, true);
    }

    protected DefaultUpnpServiceConfiguration(boolean checkRuntime) {
        this(NetworkAddressFactoryImpl.DEFAULT_TCP_HTTP_LISTEN_PORT, checkRuntime);
    }

    protected DefaultUpnpServiceConfiguration(int streamListenPort, boolean checkRuntime) {
        if (checkRuntime && ModelUtil.ANDROID_RUNTIME) {
            throw new Error("Unsupported runtime environment, use org.fourthline.cling.android.AndroidUpnpServiceConfiguration");
        }

        this.streamListenPort = streamListenPort;

        defaultExecutor = createDefaultExecutor();

        datagramProcessor = createDatagramProcessor();
        soapActionProcessor = createSOAPActionProcessor();
        genaEventProcessor = createGENAEventProcessor();

        deviceDescriptorBinderUDA10 = createDeviceDescriptorBinderUDA10();
        serviceDescriptorBinderUDA10 = createServiceDescriptorBinderUDA10();

        namespace = createNamespace();
    }

    public DatagramProcessor getDatagramProcessor() {
        return datagramProcessor;
    }

    public SOAPActionProcessor getSoapActionProcessor() {
        return soapActionProcessor;
    }

    public GENAEventProcessor getGenaEventProcessor() {
        return genaEventProcessor;
    }

    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl());
    }

    public MulticastReceiver createMulticastReceiver(NetworkAddressFactory networkAddressFactory) {
        return new MulticastReceiverImpl(
                new MulticastReceiverConfigurationImpl(
                        networkAddressFactory.getMulticastGroup(),
                        networkAddressFactory.getMulticastPort()
                )
        );
    }

    public DatagramIO createDatagramIO(NetworkAddressFactory networkAddressFactory) {
        return new DatagramIOImpl(new DatagramIOConfigurationImpl());
    }

    public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
        return new StreamServerImpl(
                new StreamServerConfigurationImpl(
                        networkAddressFactory.getStreamListenPort()
                )
        );
    }

    public Executor getMulticastReceiverExecutor() {
        return getDefaultExecutor();
    }

    public Executor getDatagramIOExecutor() {
        return getDefaultExecutor();
    }

    public Executor getStreamServerExecutor() {
        return getDefaultExecutor();
    }

    public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
        return deviceDescriptorBinderUDA10;
    }

    public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
        return serviceDescriptorBinderUDA10;
    }

    public ServiceType[] getExclusiveServiceTypes() {
        return new ServiceType[0];
    }

    /**
     * @return Defaults to <code>false</code>.
     */
	public boolean isReceivedSubscriptionTimeoutIgnored() {
		return false;
	}

    public UpnpHeaders getDescriptorRetrievalHeaders(RemoteDeviceIdentity identity) {
        return null;
    }

    public UpnpHeaders getEventSubscriptionHeaders(RemoteService service) {
        return null;
    }

    /**
     * @return Defaults to 1000 milliseconds.
     */
    public int getRegistryMaintenanceIntervalMillis() {
        return 1000;
    }

    /**
     * @return Defaults to zero, disabling ALIVE flooding.
     */
    public int getAliveIntervalMillis() {
    	return 0;
    }

    public Integer getRemoteDeviceMaxAgeSeconds() {
        return null;
    }

    public Executor getAsyncProtocolExecutor() {
        return getDefaultExecutor();
    }

    public Executor getSyncProtocolExecutor() {
        return getDefaultExecutor();
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public Executor getRegistryMaintainerExecutor() {
        return getDefaultExecutor();
    }

    public Executor getRegistryListenerExecutor() {
        return getDefaultExecutor();
    }

    public NetworkAddressFactory createNetworkAddressFactory() {
        return createNetworkAddressFactory(streamListenPort);
    }

    public void shutdown() {
        if (getDefaultExecutor() instanceof ThreadPoolExecutor) {
            log.fine("Shutting down thread pool");
            ((ThreadPoolExecutor) getDefaultExecutor()).shutdown();
        }
    }

    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
        return new NetworkAddressFactoryImpl(streamListenPort);
    }

    protected DatagramProcessor createDatagramProcessor() {
        return new DatagramProcessorImpl();
    }

    protected SOAPActionProcessor createSOAPActionProcessor() {
        return new SOAPActionProcessorImpl();
    }

    protected GENAEventProcessor createGENAEventProcessor() {
        return new GENAEventProcessorImpl();
    }

    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new UDA10DeviceDescriptorBinderImpl();
    }

    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderImpl();
    }

    protected Namespace createNamespace() {
        return new Namespace();
    }

    protected Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    protected Executor createDefaultExecutor() {
        return new ClingExecutor();
    }

    public static class ClingExecutor extends ThreadPoolExecutor {

        public ClingExecutor() {
            this(new ClingThreadFactory(),
                 new ThreadPoolExecutor.DiscardPolicy() {
                     // The pool is unbounded but rejections will happen during shutdown
                     @Override
                     public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                         // Log and discard
                         log.info("Thread pool rejected execution of " + runnable.getClass());
                         super.rejectedExecution(runnable, threadPoolExecutor);
                     }
                 }
            );
        }

        public ClingExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedHandler) {
            // This is the same as Executors.newCachedThreadPool
            super(0,
                  Integer.MAX_VALUE,
                  60L,
                  TimeUnit.SECONDS,
                  new SynchronousQueue<Runnable>(),
                  threadFactory,
                  rejectedHandler
            );
        }

        @Override
        protected void afterExecute(Runnable runnable, Throwable throwable) {
            super.afterExecute(runnable, throwable);
            if (throwable != null) {
                // Log only
                log.warning("Thread terminated " + runnable + " abruptly with exception: " + throwable);
                log.warning("Root cause: " + Exceptions.unwrap(throwable));
            }
        }
    }

    // Executors.DefaultThreadFactory is package visibility (...no touching, you unworthy JDK user!)
    public static class ClingThreadFactory implements ThreadFactory {

        protected final ThreadGroup group;
        protected final AtomicInteger threadNumber = new AtomicInteger(1);
        protected final String namePrefix = "cling-";

        public ClingThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(
                    group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0
            );
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);

            return t;
        }
    }

}
