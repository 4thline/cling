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

package org.fourthline.cling.transport.impl.jetty;

import org.fourthline.cling.model.ServerClientTokens;
import org.fourthline.cling.transport.spi.StreamClientConfiguration;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Settings for the Jetty 8 implementation.
 *
 * @author Christian Bauer
 */
public class StreamClientConfigurationImpl implements StreamClientConfiguration {

    protected ExecutorService executorService;
    protected int connectionTimeoutSeconds = 20;
    protected int responseTimeoutSeconds = 60;

    /**
     * Jetty HttpClient needs a thread pool to execute its maintenance tasks, and
     * to execute requests. However, in Cling, the
     * {@link org.fourthline.cling.transport.spi.StreamClient} is called in a
     * separate thread anyway, so you should simply reuse the
     * {@link org.fourthline.cling.UpnpServiceConfiguration#getSyncProtocolExecutor()}.
     */
    public StreamClientConfigurationImpl(final Executor executor) {
        this(
            new AbstractExecutorService() {

                boolean terminated;

                @Override
                public void shutdown() {
                    terminated = true;
                }

                @Override
                public List<Runnable> shutdownNow() {
                    shutdown();
                    return null;
                }

                @Override
                public boolean isShutdown() {
                    return terminated;
                }

                @Override
                public boolean isTerminated() {
                    return terminated;
                }

                @Override
                public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
                    shutdown();
                    return terminated;
                }

                @Override
                public void execute(Runnable runnable) {
                    executor.execute(runnable);
                }
            }
        );
    }

    public StreamClientConfigurationImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getResponseTimeoutSeconds() {
        return responseTimeoutSeconds;
    }

    public void setResponseTimeoutSeconds(int responseTimeoutSeconds) {
        this.responseTimeoutSeconds = responseTimeoutSeconds;
    }

    public int getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public void setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }

    /**
     * Defaults to string value of {@link org.fourthline.cling.model.ServerClientTokens}.
     */
    public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }

}
