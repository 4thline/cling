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

package org.fourthline.cling.mock;

import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import javax.enterprise.inject.Alternative;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Christian Bauer
 */
@Alternative
public class MockUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    final protected boolean maintainsRegistry;
    final protected boolean multiThreaded;

    /**
     * Does not maintain registry, single threaded execution.
     */
    public MockUpnpServiceConfiguration() {
        this(false, false);
    }

    /**
     * Single threaded execution.
     */
    public MockUpnpServiceConfiguration(boolean maintainsRegistry) {
        this(maintainsRegistry, false);
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
        return getDefaultExecutorService();
    }

    @Override
    protected ExecutorService getDefaultExecutorService() {
        if (isMultiThreaded()) {
            return super.getDefaultExecutorService();
        }
        return new AbstractExecutorService() {

            boolean terminated;

            public void shutdown() {
                terminated = true;
            }

            public List<Runnable> shutdownNow() {
                shutdown();
                return null;
            }

            public boolean isShutdown() {
                return terminated;
            }

            public boolean isTerminated() {
                return terminated;
            }

            public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
                shutdown();
                return terminated;
            }

            public void execute(Runnable runnable) {
                runnable.run();
            }
        };
    }

}
