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

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequest;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestDeviceType;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestRootDevice;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestServiceType;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestUDN;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.protocol.SendingAsync;
import org.fourthline.cling.transport.RouterException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Sending notification messages for a registered local device.
 * <p>
 * Sends all required (dozens) of messages three times, waits between 0 and 150
 * milliseconds between each bulk sending procedure.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class SendingNotification extends SendingAsync {

    final private static Logger log = Logger.getLogger(SendingNotification.class.getName());

    private LocalDevice device;

    public SendingNotification(UpnpService upnpService, LocalDevice device) {
        super(upnpService);
        this.device = device;
    }

    public LocalDevice getDevice() {
        return device;
    }

    protected void execute() throws RouterException {

        List<NetworkAddress> activeStreamServers =
            getUpnpService().getRouter().getActiveStreamServers(null);
        if (activeStreamServers.size() == 0) {
            log.fine("Aborting notifications, no active stream servers found (network disabled?)");
            return;
        }

        // Prepare it once, it's the same for each repetition
        List<Location> descriptorLocations = new ArrayList<>();
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            descriptorLocations.add(
                    new Location(
                            activeStreamServer,
                            getUpnpService().getConfiguration().getNamespace().getDescriptorPathString(getDevice())
                    )
            );
        }

        for (int i = 0; i < getBulkRepeat(); i++) {
            try {

                for (Location descriptorLocation : descriptorLocations) {
                    sendMessages(descriptorLocation);
                }

                // UDA 1.0 is silent about this but UDA 1.1 recomments "a few hundred milliseconds"
                log.finer("Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(getBulkIntervalMilliseconds());

            } catch (InterruptedException ex) {
                log.warning("Advertisement thread was interrupted: " + ex);
            }
        }
    }

    protected int getBulkRepeat() {
        return 3; // UDA 1.0 says maximum 3 times for alive messages, let's just do it for all
    }

    protected int getBulkIntervalMilliseconds() {
        return 150;
    }

    public void sendMessages(Location descriptorLocation) throws RouterException {
        log.finer("Sending root device messages: " + getDevice());
        List<OutgoingNotificationRequest> rootDeviceMsgs =
                createDeviceMessages(getDevice(), descriptorLocation);
        for (OutgoingNotificationRequest upnpMessage : rootDeviceMsgs) {
            getUpnpService().getRouter().send(upnpMessage);
        }

        if (getDevice().hasEmbeddedDevices()) {
            for (LocalDevice embeddedDevice : getDevice().findEmbeddedDevices()) {
                log.finer("Sending embedded device messages: " + embeddedDevice);
                List<OutgoingNotificationRequest> embeddedDeviceMsgs =
                        createDeviceMessages(embeddedDevice, descriptorLocation);
                for (OutgoingNotificationRequest upnpMessage : embeddedDeviceMsgs) {
                    getUpnpService().getRouter().send(upnpMessage);
                }
            }
        }

        List<OutgoingNotificationRequest> serviceTypeMsgs =
                createServiceTypeMessages(getDevice(), descriptorLocation);
        if (serviceTypeMsgs.size() > 0) {
            log.finer("Sending service type messages");
            for (OutgoingNotificationRequest upnpMessage : serviceTypeMsgs) {
                getUpnpService().getRouter().send(upnpMessage);
            }
        }
    }

    protected List<OutgoingNotificationRequest> createDeviceMessages(LocalDevice device,
                                                                     Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();

        // See the tables in UDA 1.0 section 1.1.2

        if (device.isRoot()) {
            msgs.add(
                    new OutgoingNotificationRequestRootDevice(
                            descriptorLocation,
                            device,
                            getNotificationSubtype()
                    )
            );
        }

        msgs.add(
                new OutgoingNotificationRequestUDN(
                        descriptorLocation, device, getNotificationSubtype()
                )
        );
        msgs.add(
                new OutgoingNotificationRequestDeviceType(
                        descriptorLocation, device, getNotificationSubtype()
                )
        );

        return msgs;
    }

    protected List<OutgoingNotificationRequest> createServiceTypeMessages(LocalDevice device,
                                                                          Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();

        for (ServiceType serviceType : device.findServiceTypes()) {
            msgs.add(
                    new OutgoingNotificationRequestServiceType(
                            descriptorLocation, device,
                            getNotificationSubtype(), serviceType
                    )
            );
        }

        return msgs;
    }

    protected abstract NotificationSubtype getNotificationSubtype();

}
