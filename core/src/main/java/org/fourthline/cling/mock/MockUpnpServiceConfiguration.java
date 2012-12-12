/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.Executor;

/**
 * @author Christian Bauer
 */
public class MockUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    final protected boolean maintainsRegistry;
    final protected boolean multiThreaded;

    /**
     * Does not maintain registry, single threaded execution.
     */
    public MockUpnpServiceConfiguration() {
        this(false, false);
    }

    public MockUpnpServiceConfiguration(boolean maintainsRegistry, boolean multiThreaded) {
        super(false);
        this.maintainsRegistry = maintainsRegistry;
        this.multiThreaded = multiThreaded;
    }

    public boolean isMaintainsRegistry() {
        return maintainsRegistry;
    }

    public boolean isMultiThreaded() {
        return multiThreaded;
    }

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
        if (isMaintainsRegistry()) {
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
        return isMultiThreaded()
                ? super.createDefaultExecutor()
                : new Executor() {
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                 };
    }

}
