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

package org.fourthline.cling.bridge;

import org.fourthline.cling.model.message.header.STAllHeader;
import org.seamless.util.logging.SystemOutLoggingHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class WARBridgeContextListener implements ServletContextListener {

    final private static Logger log = Logger.getLogger(WARBridgeContextListener.class.getName());

    protected final WARBridgeUpnpService upnpService = new WARBridgeUpnpService();

    public void contextInitialized(ServletContextEvent sce) {

        final ServletContext sc = sce.getServletContext();

        initializeLogging(sc);

        final URL localBaseURL = getLocalBaseURL(sc);
        log.info("Using local base URL: " + localBaseURL);

        try {
            upnpService.setConfiguration(
                    new BridgeUpnpServiceConfiguration(localBaseURL, sc.getContextPath())
            );

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            upnpService.start();
            sc.setAttribute(Constants.ATTR_UPNP_SERVICE, upnpService);

            upnpService.getControlPoint().search(new STAllHeader());

        } catch (Exception ex) {
            System.err.println("Exception starting UPnP service: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        upnpService.shutdown();
    }

    protected void initializeLogging(ServletContext sc) {
        String loggingConfig = System.getProperty(Constants.INIT_PARAM_LOGGING_CONFIG);
        if (loggingConfig == null) {
            loggingConfig = sc.getInitParameter(Constants.INIT_PARAM_LOGGING_CONFIG);
        }
        if (loggingConfig == null) return;

        if (!loggingConfig.startsWith("/")) loggingConfig = "/" + loggingConfig;
        InputStream is = sc.getResourceAsStream(loggingConfig);
        if (is == null) {
            System.err.println("Can't find logging configuration in WAR: " + loggingConfig);
            System.exit(1);
        }
        try {
            LogManager.getLogManager().readConfiguration(is);
            Logger.getLogger("").addHandler(new SystemOutLoggingHandler());
        } catch (Exception ex) {
            System.err.println("Logging configuration failed: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    protected URL getLocalBaseURL(ServletContext sc) {
        String localBaseURLString = System.getProperty(Constants.INIT_PARAM_LOCAL_BASE_URL);
        if (localBaseURLString == null) {
            localBaseURLString = sc.getInitParameter(Constants.INIT_PARAM_LOCAL_BASE_URL);
        }

        if (localBaseURLString == null) {
            System.err.println(
                    "Missing configuration parameter (as servlet contex parameter or system property): " + Constants.INIT_PARAM_LOCAL_BASE_URL
            );
            System.exit(1);
        }

        URL localBaseURL = null;
        try {
            localBaseURL = new URL(localBaseURLString);
            if (localBaseURL.getPath().length() > 1) {
                // We don't want a path here, users should not configure the servlet context path twice
                System.err.println("Base URL should not include path or query: " + localBaseURL);
                System.exit(1);
            }
            if (localBaseURL.getPort() == -1) {
                // TODO: Instead if failing we could just assume port 80 or 443, no?
                // We need the port to initalize the network stack properly
                System.err.println("Base URL does not include TCP port number: " + localBaseURL);
                System.exit(1);
            }
        } catch (Exception ex) {
            System.err.println("Can't parse base URL value: " + localBaseURL);
            System.exit(1);
        }
        return localBaseURL;
    }
}
