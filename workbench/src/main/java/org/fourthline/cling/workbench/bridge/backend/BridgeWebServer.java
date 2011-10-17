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

package org.fourthline.cling.workbench.bridge.backend;


import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.bridge.auth.AuthFilter;
import org.fourthline.cling.bridge.gateway.ActionResource;
import org.fourthline.cling.bridge.gateway.DeviceResource;
import org.fourthline.cling.bridge.gateway.GatewayFilter;
import org.fourthline.cling.bridge.gateway.RegistryResource;
import org.fourthline.cling.bridge.gateway.ServiceResource;
import org.fourthline.cling.bridge.link.LinkResource;
import org.fourthline.cling.bridge.link.proxy.ProxyResource;
import org.fourthline.cling.bridge.provider.XHTMLBodyWriter;

import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class BridgeWebServer extends Server {

    private static Logger log = Logger.getLogger(BridgeWebServer.class.getName());

    final protected BridgeUpnpService bridgeUpnpService;

    public BridgeWebServer(Connector[] connectors, BridgeUpnpService bridgeUpnpService) {
        this(connectors, null, bridgeUpnpService);
    }

    public BridgeWebServer(Connector[] connectors, String contextPath, BridgeUpnpService bridgeUpnpService) {

        this.bridgeUpnpService = bridgeUpnpService;

        // Listening on these sockets
        for (Connector connector : connectors) {
            addConnector(connector);
        }

        HandlerCollection handlers = new HandlerCollection();

        // We don't need session support
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        // Context prefix
        if (contextPath != null) {
            if (contextPath.endsWith("/"))
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            log.info("Using context path: " + contextPath);
            contextHandler.setContextPath(contextPath);
        }

        // Context attribute(s)
        contextHandler.getServletContext().setAttribute(Constants.ATTR_UPNP_SERVICE, bridgeUpnpService);
        contextHandler.setAttribute(Constants.ATTR_UPNP_SERVICE, bridgeUpnpService);

        // Context listener
        /*
        contextHandler.addEventListener(new WARBridgeContextListener());
        contextHandler.getServletContext().setInitParameter(
                "org.fourthline.cling.bridge.localBaseURL",
                "http://10.0.0.2:8081"
        );
        */

        // Register servlets
        //ServletHolder s = new ServletHolder(new TestServlet());
        ServletHolder s = new ServletHolder(new HttpServletDispatcher());

        s.setInitOrder(1); // load-on-startup

        // s.setInitParameter("resteasy.servlet.mapping.prefix", BridgeNamespace.PATH_PREFIX);

        // Scanning is no good with this deployment style (expects WAR structure)
        //s.setInitParameter("resteasy.scan", "true");
        s.setInitParameter(
                "resteasy.resources",
                ActionResource.class.getName() + "," +
                        DeviceResource.class.getName() + "," +
                        RegistryResource.class.getName() + "," +
                        ServiceResource.class.getName() + "," +
                        LinkResource.class.getName() + "," +
                        ProxyResource.class.getName()
                );
        s.setInitParameter(
                "resteasy.providers",
                XHTMLBodyWriter.class.getName()
        );

        contextHandler.addServlet(s, "/*");

        // Register filters
        FilterHolder f = new FilterHolder(new GatewayFilter());
        contextHandler.addFilter(f, "/*", 1);

        f = new FilterHolder(new AuthFilter(bridgeUpnpService.getConfiguration().getAuthManager()));
        contextHandler.addFilter(f, "/*", 1);

        // TODO Register error handlers
        //contextHandler.setErrorHandler(new ErrorHandler());

        handlers.addHandler(contextHandler);

        setHandler(handlers);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        bridgeUpnpService.shutdown();
    }
}
