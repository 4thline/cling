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

package org.fourthline.cling.support.shared;

import org.seamless.swing.logging.LogCategory;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class CoreLogCategories extends ArrayList<LogCategory> {

    public CoreLogCategories() {
        super(10);

        add(new LogCategory("Network", new LogCategory.Group[]{

                new LogCategory.Group(
                        "UDP communication",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.DatagramIO.class.getName(), Level.FINE),
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.MulticastReceiver.class.getName(), Level.FINE),
                        }
                ),

                new LogCategory.Group(
                        "UDP datagram processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.DatagramProcessor.class.getName(), Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "TCP communication",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.UpnpStream.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.StreamServer.class.getName(), Level.FINE),
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.StreamClient.class.getName(), Level.FINE),
                        }
                ),

                new LogCategory.Group(
                        "SOAP action message processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.SOAPActionProcessor.class.getName(), Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "GENA event message processing and content",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.spi.GENAEventProcessor.class.getName(), Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "HTTP header processing",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.model.message.UpnpHeaders.class.getName(), Level.FINER)
                        }
                ),
        }));


        add(new LogCategory("UPnP Protocol", new LogCategory.Group[]{

                new LogCategory.Group(
                        "Discovery (Notification & Search)",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.ProtocolFactory.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.protocol.async", Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "Description",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.ProtocolFactory.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.RetrieveRemoteDescriptors.class.getName(), Level.FINE),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.ReceivingRetrieval.class.getName(), Level.FINE),
                                new LogCategory.LoggerLevel(org.fourthline.cling.binding.xml.DeviceDescriptorBinder.class.getName(), Level.FINE),
                                new LogCategory.LoggerLevel(org.fourthline.cling.binding.xml.ServiceDescriptorBinder.class.getName(), Level.FINE),
                        }
                ),

                new LogCategory.Group(
                        "Control",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.ProtocolFactory.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.ReceivingAction.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.SendingAction.class.getName(), Level.FINER),
                        }
                ),

                new LogCategory.Group(
                        "GENA ",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.model.gena", Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.ProtocolFactory.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.ReceivingEvent.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.ReceivingSubscribe.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.ReceivingUnsubscribe.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.SendingEvent.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.SendingSubscribe.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.SendingUnsubscribe.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.protocol.sync.SendingRenewal.class.getName(), Level.FINER),
                        }
                ),
        }));

        add(new LogCategory("Core", new LogCategory.Group[]{

                new LogCategory.Group(
                        "Router",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.transport.Router.class.getName(), Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "Registry",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel(org.fourthline.cling.registry.Registry.class.getName(), Level.FINER),
                        }
                ),

                new LogCategory.Group(
                        "Local service binding & invocation",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.binding.annotations", Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.model.meta.LocalService.class.getName(), Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.model.action", Level.FINER),
                                new LogCategory.LoggerLevel("org.fourthline.cling.model.state", Level.FINER),
                                new LogCategory.LoggerLevel(org.fourthline.cling.model.DefaultServiceManager.class.getName(), Level.FINER)
                        }
                ),

                new LogCategory.Group(
                        "Control Point interaction",
                        new LogCategory.LoggerLevel[]{
                                new LogCategory.LoggerLevel("org.fourthline.cling.controlpoint", Level.FINER),
                        }
                ),
        }));

    }

}
