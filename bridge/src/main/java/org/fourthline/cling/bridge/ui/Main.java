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

package org.fourthline.cling.bridge.ui;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.bridge.ui.backend.Bridge;
/* TODO
import org.fourthline.cling.workbench.plugins.binarylight.device.DemoBinaryLight;
*/
import org.seamless.util.logging.LoggingUtil;
import org.seamless.util.URIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

/**
 * @author Christian Bauer
 */
public class Main {

    final protected UpnpService upnpService;
    final protected Bridge bridge;

    public static void main(String[] args) throws Exception {

        final Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);
        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("USAGE: java -jar <jarfile> [options]");
            cmdLineParser.printUsage(System.err);
            System.exit(1);
        }

        File file = new File("cling-logging.properties");
        if (file.exists())
            LoggingUtil.loadDefaultConfiguration(new FileInputStream(file));

        new Main(options);
    }

    public Main(Options options) throws Exception {
        upnpService = new UpnpServiceImpl() {
            @Override
            public void shutdown() {
                bridge.stop(true);
                super.shutdown();
            }
        };

        bridge = new Bridge(upnpService);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                upnpService.shutdown();
            }
        });

        bridge.start(options.lanHost, URIUtil.toURL(URI.create(options.localURL)));

        if (options.demo) {
/*
            UDN udn = UDN.uniqueSystemIdentifier("Bridge Demo Binary Light");
            LocalService service = new AnnotationLocalServiceBinder().read(DemoBinaryLight.class);

            service.setManager(
                    new DefaultServiceManager(service) {
                        @Override
                        protected Object createServiceInstance() throws Exception {
                            return new DemoBinaryLight() {
                                @Override
                                public void setTarget(boolean newTargetValue) {
                                    super.setTarget(newTargetValue);
                                    System.out.println("### DEMO LIGHT SET TARGET TO: " + newTargetValue);
                                }
                            };
                        }
                    }
            );

            upnpService.getRegistry().addDevice(DemoBinaryLight.createDefaultDevice(udn, service));
*/
        }

        upnpService.getControlPoint().search();
    }

    public static class Options {

        @Option(required = true, name = "-h", metaVar = "<IP>", usage = "The LAN IP of this host")
        public String lanHost;

        @Option(required = true, name = "-url", metaVar = "<URL>", usage = "The local WAN URL (e.g. http://<external IP>:<mapped port>)")
        public String localURL;

        @Option(required = false, name = "-demo", usage = "Start demo binary light device and service")
        public boolean demo;
    }

}
