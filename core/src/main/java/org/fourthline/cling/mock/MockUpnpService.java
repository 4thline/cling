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

package org.fourthline.cling.mock;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.ControlPointImpl;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryImpl;
import org.fourthline.cling.registry.RegistryMaintainer;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.UpnpStream;

import javax.enterprise.inject.Alternative;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Simplifies testing of core and non-core modules.
 * <p>
 * This service has no real network transport layer, it collects all messages instead and makes
 * them available for testing with {@link #getOutgoingDatagramMessages()},
 * {@link #getSentStreamRequestMessages()}, etc. Mock responses for TCP (HTTP) stream requests
 * can be returned by overriding {@link #getStreamResponseMessage(org.fourthline.cling.model.message.StreamRequestMessage)}
 * or {@link #getStreamResponseMessages()} if you know the order of requests.
 * </p>
 * <p>
 * It uses the {@link org.fourthline.cling.mock.MockUpnpService.MockProtocolFactory}.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class MockUpnpService implements UpnpService {

    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final Router router;

    protected final NetworkAddressFactory networkAddressFactory;

    private List<IncomingDatagramMessage> incomingDatagramMessages = new ArrayList();
    private List<OutgoingDatagramMessage> outgoingDatagramMessages = new ArrayList();
    private List<UpnpStream> receivedUpnpStreams = new ArrayList();
    private List<StreamRequestMessage> sentStreamRequestMessages = new ArrayList();
    private List<byte[]> broadcastedBytes = new ArrayList();

    /**
     * Single-thread of execution for the whole UPnP stack, no ALIVE messages or registry maintenance.
     */
    public MockUpnpService() {
        this(false, false, false);
    }

    /**
     * Single-thread of execution for the whole UPnP stack, except one background registry maintenance thread.
     */
    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry) {
        this(sendsAlive, maintainsRegistry, false);
    }

    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry, final boolean multiThreaded) {

        this.configuration = new DefaultUpnpServiceConfiguration(false) {
            @Override
            protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort) {
                // We are only interested in 127.0.0.1
                return new NetworkAddressFactoryImpl(streamListenPort) {
                    @Override
                    protected boolean isUsableNetworkInterface(NetworkInterface iface) throws Exception {
                        return (iface.isLoopback());
                    }

                    @Override
                    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
                        return (address.isLoopbackAddress() && address instanceof Inet4Address);
                    }

                };
            }

            @Override
            public Executor getRegistryMaintainerExecutor() {
                if (maintainsRegistry) {
                    return new Executor() {
                        public void execute(Runnable runnable) {
                            new Thread(runnable).start();
                        }
                    };
                }
                return createDefaultExecutor();
            }

            @Override
            protected Executor createDefaultExecutor() {
                return multiThreaded
                        ?
                        super.createDefaultExecutor()
                        :
                        new Executor() {
                            public void execute(Runnable runnable) {
                                runnable.run();
                            }
                        };
            }
        };

        this.protocolFactory = createProtocolFactory(this, sendsAlive);

        this.registry = new RegistryImpl(this) {
            @Override
            protected RegistryMaintainer createRegistryMaintainer() {
                return maintainsRegistry ? super.createRegistryMaintainer() : null;
            }
        };

        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();

        this.router = createRouter();

        this.controlPoint = new ControlPointImpl(configuration, protocolFactory, registry);
    }

    protected ProtocolFactory createProtocolFactory(UpnpService service, boolean sendsAlive) {
        return new MockProtocolFactory(service, sendsAlive);
    }

    protected Router createRouter() {
        return new MockRouter();
    }

    /**
     * This factory customizes several protocols.
     * <p>
     * The {@link org.fourthline.cling.protocol.async.SendingNotificationAlive} protocol
     * only sends messages if this feature is enabled when instantiating the factory.
     * </p>
     * <p>
     * The {@link org.fourthline.cling.protocol.async.SendingSearch} protocol doesn't wait between
     * sending search message bulks, this speeds up testing.
     * </p>
     */
    public static class MockProtocolFactory extends ProtocolFactoryImpl {

        private boolean sendsAlive;

        public MockProtocolFactory(UpnpService upnpService, boolean sendsAlive) {
            super(upnpService);
            this.sendsAlive = sendsAlive;
        }

        @Override
        public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
            return new SendingNotificationAlive(getUpnpService(), localDevice) {
                @Override
                protected void execute() {
                    if (sendsAlive) super.execute();
                }
            };
        }

        @Override
        public SendingSearch createSendingSearch(UpnpHeader searchTarget, int mxSeconds) {
            return new SendingSearch(getUpnpService(), searchTarget, mxSeconds) {
                @Override
                public int getBulkIntervalMilliseconds() {
                    return 0; // Don't wait
                }
            };
        }
    }

    public class MockRouter implements Router {
            int counter = -1;

            public UpnpServiceConfiguration getConfiguration() {
                return configuration;
            }

            public ProtocolFactory getProtocolFactory() {
                return protocolFactory;
            }

            public StreamClient getStreamClient() {
                return null;
            }

            public NetworkAddressFactory getNetworkAddressFactory() {
                return networkAddressFactory;
            }

            public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
                // Simulate an active stream server, otherwise the notification/search response
                // protocols won't even run
                try {
                    return Arrays.asList(
                            new NetworkAddress(
                                    InetAddress.getByName("127.0.0.1"),
                                    NetworkAddressFactoryImpl.DEFAULT_TCP_HTTP_LISTEN_PORT
                            )
                    );
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }

            public void shutdown() {

            }

            public void received(IncomingDatagramMessage msg) {
                incomingDatagramMessages.add(msg);
            }

            public void received(UpnpStream stream) {
                receivedUpnpStreams.add(stream);
            }

            public void send(OutgoingDatagramMessage msg) {
                outgoingDatagramMessages.add(msg);
            }

            public StreamResponseMessage send(StreamRequestMessage msg) {
                sentStreamRequestMessages.add(msg);
                counter++;
                return getStreamResponseMessages() != null
                        ? getStreamResponseMessages()[counter]
                        : getStreamResponseMessage(msg);
            }

            public void broadcast(byte[] bytes) {
                broadcastedBytes.add(bytes);
            }
    }

    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    public Registry getRegistry() {
        return registry;
    }

    public Router getRouter() {
        return router;
    }

    public void shutdown() {
        getRouter().shutdown();
        getRegistry().shutdown();
        getConfiguration().shutdown();
    }

    public List<IncomingDatagramMessage> getIncomingDatagramMessages() {
        return incomingDatagramMessages;
    }

    public List<OutgoingDatagramMessage> getOutgoingDatagramMessages() {
        return outgoingDatagramMessages;
    }

    public List<UpnpStream> getReceivedUpnpStreams() {
        return receivedUpnpStreams;
    }

    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return sentStreamRequestMessages;
    }

    public List<byte[]> getBroadcastedBytes() {
        return broadcastedBytes;
    }

    public StreamResponseMessage[] getStreamResponseMessages() {
        return null;
    }

    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage request) {
        return null;
    }
}
