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

package org.fourthline.cling.bridge.ui.backend;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.BridgeNamespace;
import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.seamless.swing.logging.LogMessage;
import org.seamless.util.Exceptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class Bridge {

    final private static Logger log = Logger.getLogger(Bridge.class.getName());

    final protected UpnpService upnpService;

    protected String lanHost;
    protected URL localURL;
    protected Service connectionService;
    protected boolean mapPort;
    protected PortMapping activePortMapping;

    protected WrappingBridgeUpnpService bridgeUpnpService;
    protected BridgeWebServer bridgeWebServer;

    public Bridge(UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    synchronized public Service getConnectionService() {
        return connectionService;
    }

    synchronized public PortMapping getActivePortMapping() {
        return activePortMapping;
    }

    synchronized protected void setActivePortMapping(PortMapping activePortMapping) {
        this.activePortMapping = activePortMapping;
    }

    synchronized public String getLanHost() {
        return lanHost;
    }

    synchronized public URL getLocalURL() {
        return localURL;
    }

    synchronized public boolean isMapPort() {
        return mapPort;
    }

    synchronized public BridgeUpnpService getBridgeUpnpService() {
        return bridgeUpnpService;
    }

    synchronized public BridgeWebServer getBridgeWebServer() {
        return bridgeWebServer;
    }

    synchronized public boolean isRunning() {
        return getBridgeUpnpService() != null;
    }

    synchronized public void start(String lanHost, URL localURL) {
        start(lanHost, localURL, null, false);
    }

    synchronized public void start(String lanHost, URL localURL, Service connectionService, boolean mapPort) {
        if (isRunning()) throw new IllegalStateException("Bridge is already running");

        this.lanHost = lanHost;
        this.localURL = localURL;
        this.connectionService = connectionService;
        this.mapPort = mapPort;

        if (mapPort && connectionService != null) {
            addPortMapping(lanHost, localURL.getPort());
        }

        try {
            bridgeUpnpService = new WrappingBridgeUpnpService(
                    upnpService,
                    new BridgeUpnpServiceConfiguration(localURL) {
                        @Override
                        public BridgeNamespace getNamespace() {
                            return new BridgeNamespace();
                        }
                    }
            );


            bridgeWebServer = new BridgeWebServer(
                    createConnectors(lanHost, localURL.getPort()),
                    bridgeUpnpService
            );

            bridgeWebServer.setGracefulShutdown(1000); // Let's wait a second for ongoing transfers to complete
            bridgeWebServer.start();

        } catch (Exception ex) {
            // Exit Jetty if it couldn't start
            if (bridgeWebServer != null) {
                try {
                    bridgeWebServer.stop();
                } catch (Exception e) {
                    log.severe("Could not shut down bridge webserver: " + e.toString());
                }
            }
            throw new RuntimeException("Error starting bridge webserver: " + Exceptions.unwrap(ex).toString());
        }
    }

    synchronized public void stop(boolean shutdown) {
        try {
            if (bridgeWebServer != null) {
                bridgeWebServer.stop();
                bridgeWebServer = null;
            }

            if (bridgeUpnpService != null) {
                bridgeUpnpService.shutdown();
                bridgeUpnpService = null;
            }

            if (getActivePortMapping() != null) {
                deletePortMapping(getActivePortMapping(), shutdown);
                setActivePortMapping(null);
            }

            lanHost = null;
            localURL = null;
            connectionService = null;
            mapPort = false;

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected Connector[] createConnectors(String lanHost, int localPort) {
        List<Connector> connectors = new ArrayList();
        SocketConnector con = new SocketConnector();
        con.setHost(lanHost);
        con.setPort(localPort);
        connectors.add(con);
        return connectors.toArray(new Connector[connectors.size()]);
    }

    protected PortMapping createPortMapping(String internalClient, int port) {
        final PortMapping mapping = new PortMapping();
        mapping.setProtocol(PortMapping.Protocol.TCP);
        mapping.setEnabled(true);
        mapping.setInternalPort(new UnsignedIntegerTwoBytes(port));
        mapping.setExternalPort(new UnsignedIntegerTwoBytes(port));
        mapping.setInternalClient(internalClient);
        mapping.setLeaseDurationSeconds(new UnsignedIntegerFourBytes(0));
        mapping.setDescription(Workbench.APPNAME + " - UPnP WAN Bridge");
        return mapping;
    }

    protected void addPortMapping(String internalClient, int port) {
        addPortMapping(createPortMapping(internalClient, port));
    }

    protected void addPortMapping(final PortMapping mapping) {
        if (getConnectionService() == null) return;
        getUpnpService().getControlPoint().execute(
                new PortMappingAdd(getConnectionService(), mapping) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "Bridge",
                                "Added port mapping: " + mapping.getProtocol() + "/" + mapping.getExternalPort()
                        ));
                        setActivePortMapping(mapping);
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.WARNING,
                                "Bridge",
                                "Port mapping failed: " + defaultMsg
                        ));
                    }
                }
        );
    }

    protected void deletePortMapping(final PortMapping mapping, boolean synchronous) {
        if (getConnectionService() == null) return;
        ActionCallback cb =
                new PortMappingDelete(getConnectionService(), mapping) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "Bridge",
                                "Removed port mapping: " + mapping.getProtocol() + "/" + mapping.getExternalPort()
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        if (invocation.getFailure().getErrorCode() == 714) return; // That's ok, just didn't exist
                        Workbench.log(new LogMessage(
                                Level.WARNING,
                                "Bridge",
                                "Port mapping removal failed: " + defaultMsg
                        ));
                    }
                };
        if (synchronous) {
            // Block the (shutdown) thread
            cb.setControlPoint(getUpnpService().getControlPoint());
            cb.run();
        } else {
            getUpnpService().getControlPoint().execute(cb);
        }
    }

}
