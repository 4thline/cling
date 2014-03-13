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

package org.fourthline.cling.workbench.browser.impl;

import org.fourthline.cling.controlpoint.event.Search;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.event.LocalDeviceDiscovery;
import org.fourthline.cling.registry.event.Phase;
import org.fourthline.cling.registry.event.RemoteDeviceDiscovery;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.browser.BrowserView;
import org.fourthline.cling.workbench.browser.RootDeviceSelected;
import org.fourthline.cling.workbench.info.DeviceInfoSelectionChanged;
import org.seamless.swing.Application;
import org.seamless.util.MimeType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.*;

@ApplicationScoped
public class BrowserPresenter implements BrowserView.Presenter {

    @Inject
    protected Router router;

    @Inject
    protected Registry registry;

    @Inject
    protected Event<Search> searchEvent;

    @Inject
    protected Event<RootDeviceSelected> rootDeviceSelectedEvent;

    @Inject
    protected BrowserView view;

    public void init() {
        view.setPresenter(this);
        onRefreshDevices(); // Immediately send search message
    }

    @Override
    public void onRefreshDevices() {
        registry.removeAllRemoteDevices();
        searchEvent.fire(new Search(1));
    }

    public void onDeviceInfoSelectionChanged(@Observes DeviceInfoSelectionChanged change) {
        view.setSelected(change.device);
    }

    @Override
    public void onDeviceSelected(ImageIcon icon, Device device) {
        rootDeviceSelectedEvent.fire(new RootDeviceSelected(icon, device));
    }

    public void onRemoteDeviceComplete(@Observes @Phase.Complete RemoteDeviceDiscovery discovery) {

        RemoteDevice device = discovery.getDevice();

        Workbench.Log.MAIN.info("Remote device added: " + device);

        final DeviceItem deviceItem =
                new DeviceItem(
                        device,
                        device.getDetails().getFriendlyName(),
                        device.getDisplayString(),
                        "(REMOTE) " + device.getType().getDisplayString()
                );

        Icon usableIcon = findUsableIcon(device);

        if (usableIcon != null) {

            // We retrieve it using our own infrastructure, we know how that works and behaves

            final StreamRequestMessage iconRetrievalMsg =
                    new StreamRequestMessage(UpnpRequest.Method.GET, device.normalizeURI(usableIcon.getUri()));

            StreamResponseMessage responseMsg = null;
            try {
                responseMsg = router.send(iconRetrievalMsg);
            } catch (RouterException ex) {
                Workbench.Log.MAIN.warning("Icon retrieval failed: " + ex);
            }

            if (responseMsg != null && !responseMsg.getOperation().isFailed()) {

                ContentTypeHeader ctHeader =
                    responseMsg.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);

                if (ctHeader != null) {
                    // TODO: We could guess the content type without the header, depending on file extension
                    MimeType contentType = ctHeader.getValue();

                    if (isUsableImageType(contentType)) {
                        byte[] imageBody = (byte[]) responseMsg.getBody();
                        if (imageBody != null) {
                            ImageIcon imageIcon = new ImageIcon(imageBody);
                            deviceItem.setIcon(imageIcon);
                        } else {
                            Workbench.Log.MAIN.warning(
                                "Icon request did not return with response body '" + contentType + "': " + iconRetrievalMsg.getUri()
                            );
                        }
                    } else {
                        Workbench.Log.MAIN.warning(
                            "Icon was delivered with unsupported content type '" + contentType + "': " + iconRetrievalMsg.getUri()
                        );
                    }
                } else {
                    Workbench.Log.MAIN.warning(
                        "Icon was delivered without content type header in HTTP response': " + iconRetrievalMsg.getUri()
                    );
                }

            } else {
                if (responseMsg != null) {
                    Workbench.Log.MAIN.warning(
                        "Icon retrieval of '" + iconRetrievalMsg.getUri() + "' failed: " +
                                    responseMsg.getOperation().getResponseDetails()
                    );
                } else {
                    Workbench.Log.MAIN.warning(
                        "Icon retrieval of '" + iconRetrievalMsg.getUri() + "' failed, no response received."
                    );
                }
            }
        }

        if (deviceItem.getIcon() == null) {
            deviceItem.setIcon(getUnknownDeviceIcon(deviceItem.getLabel()[0]));
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.addDeviceItem(deviceItem);
            }
        });
    }

    public void onRemoteDeviceRemoved(@Observes @Phase.Byebye RemoteDeviceDiscovery discovery) {
        RemoteDevice device = discovery.getDevice();
        final DeviceItem display = new DeviceItem(device, device.getDisplayString());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.removeDeviceItem(display);
            }
        });
    }

    public void onLocalDeviceComplete(@Observes @Phase.Complete LocalDeviceDiscovery discovery) {
        LocalDevice device = discovery.getDevice();

        String[] labels =
                new String[]{
                        device.getDetails().getFriendlyName(),
                        device.getDisplayString(),
                        "(LOCAL) " + device.getType().getDisplayString()
                };

        final DeviceItem deviceItem = new DeviceItem(device, labels);

        Icon usableIcon = findUsableIcon(device);
        if (usableIcon != null) {
            ImageIcon imageIcon = new ImageIcon(usableIcon.getData());
            deviceItem.setIcon(imageIcon);
        } else {
            deviceItem.setIcon(getUnknownDeviceIcon(deviceItem.getLabel()[0]));
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.addDeviceItem(deviceItem);
            }
        });

    }

    public void onLocalDeviceRemoved(@Observes @Phase.Byebye LocalDeviceDiscovery discovery) {
        LocalDevice device = discovery.getDevice();
        final DeviceItem deviceItem = new DeviceItem(device, device.getDisplayString());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.removeDeviceItem(deviceItem);
            }
        });
    }

    protected Icon findUsableIcon(Device device) {
        // Needs to be certain format and size
        for (Object o : device.getIcons()) {
            Icon icon = (Icon) o;
            if (icon.getWidth() <= 64 && icon.getHeight() <= 64 && isUsableImageType(icon.getMimeType()))
                return icon;
        }
        return null;
    }

    protected boolean isUsableImageType(MimeType mt) {
        return mt.getType().equals("image") &&
                (mt.getSubtype().equals("png") || mt.getSubtype().equals("jpg") ||
                        mt.getSubtype().equals("jpeg") || mt.getSubtype().equals("gif"));
    }

    protected ImageIcon getUnknownDeviceIcon(String description) {
        return Application.createImageIcon(Workbench.class, "img/48/unknown_device.png", description);
    }

}
