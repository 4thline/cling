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

package org.fourthline.cling.workbench.plugins.contentdirectory.impl;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.workbench.plugins.contentdirectory.ContentDirectoryControlPoint;
import org.fourthline.cling.workbench.plugins.contentdirectory.DetailView;
import org.seamless.util.MimeType;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class DetailPresenter implements DetailView.Presenter {

    public static final DeviceType SUPPORTED_MEDIA_RENDERER_TYPE = new UDADeviceType("MediaRenderer", 1);
    public static final ServiceType SUPPORTED_CONNECTION_MGR_TYPE = new UDAServiceType("ConnectionManager", 1);
    public static final ServiceType SUPPORTED_AV_TRANSPORT_TYPE = new UDAServiceType("AVTransport", 1);

    @Inject
    protected UpnpService upnpService;

    protected DetailView view;

    final protected Map<Device, List<ProtocolInfo>> availableRenderers = new HashMap<>();

    @Override
    public void init(DetailView view) {
        this.view = view;
        view.setPresenter(this);
        updateMediaRenderers();
    }

    @Override
    public void onContainerSelected(Container container) {
        view.showContainer(container);
    }

    @Override
    public void onItemSelected(Item item) {
        // Whenever an item is selected, update the media renders, maybe we have new ones or old ones dropped out
        updateMediaRenderers();

        view.showItem(item, getAvailableRenderers());
    }

    @Override
    public Service getMatchingAVTransportService(Device device, List<ProtocolInfo> infos, Res resource) {
        final Service avTransportService =
                device.findService(SUPPORTED_AV_TRANSPORT_TYPE);
        return avTransportService != null && isProtocolInfoMatch(infos, resource)
                ? avTransportService : null;
    }

    @Override
    public void onSendToMediaRenderer(int instanceId, Service avTransportService, String uri) {
        sendToMediaRenderer(instanceId, avTransportService, uri);
    }

    synchronized public Map<Device, List<ProtocolInfo>> getAvailableRenderers() {
        return availableRenderers;
    }

    synchronized protected void updateMediaRenderers() {
        ContentDirectoryControlPoint.LOGGER.fine("Updating media renderers");

        Collection<Device> foundMediaRenderers = upnpService.getRegistry().getDevices(SUPPORTED_MEDIA_RENDERER_TYPE);

        ContentDirectoryControlPoint.LOGGER.fine(
            "Mediarenderers found in local registry: " + foundMediaRenderers.size()
        );

        for (final Device foundMediaRenderer : foundMediaRenderers) {

            // Queue a GetProtocolInfo action that will add the renderer + protocol info to the available renderer map
            if (!availableRenderers.containsKey(foundMediaRenderer)) {

                ContentDirectoryControlPoint.LOGGER.fine(
                    "New media renderer, preparing to get protocol information: " + foundMediaRenderer
                );

                Service connectionManager =
                        foundMediaRenderer.findService(SUPPORTED_CONNECTION_MGR_TYPE);

                if (connectionManager == null) {
                    ContentDirectoryControlPoint.LOGGER.warning(
                        "MediaRenderer device has no ConnectionManager service: " + foundMediaRenderer
                    );
                    break;
                }

                GetProtocolInfo getProtocolInfoActionCallback =
                        new GetProtocolInfo(connectionManager) {

                            @Override
                            public void received(ActionInvocation actionInvocation,
                                                 ProtocolInfos sinkProtocolInfos,
                                                 ProtocolInfos sourceProtocolInfos) {
                                addMediaRendererInformation(foundMediaRenderer, sinkProtocolInfos);
                            }

                            @Override
                            public void failure(ActionInvocation invocation,
                                                UpnpResponse operation,
                                                String defaultMsg) {
                                addMediaRendererInformation(foundMediaRenderer, Collections.EMPTY_LIST);
                                updateStatusFailure(
                                        "Error retrieving protocol info from " +
                                                foundMediaRenderer.getDetails().getFriendlyName() + ". " + defaultMsg
                                );
                            }

                        };

                upnpService.getControlPoint().execute(getProtocolInfoActionCallback);
            }
        }

        // Remove renderers from the available renderers map if they are not in the registry anymore
        Iterator<Map.Entry<Device, List<ProtocolInfo>>> it = availableRenderers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Device, List<ProtocolInfo>> currentDeviceEntry = it.next();
            if (!foundMediaRenderers.contains(currentDeviceEntry.getKey())) {
                it.remove();
            }
        }

    }

    synchronized void addMediaRendererInformation(Device mediaRenderer, List<ProtocolInfo> protocolInfos) {
        availableRenderers.put(mediaRenderer, protocolInfos);
    }

    protected boolean isProtocolInfoMatch(List<ProtocolInfo> supportedProtocols, Res resource) {
        ProtocolInfo resourceProtocolInfo = resource.getProtocolInfo();
        if (!resourceProtocolInfo.getProtocol().equals(Protocol.HTTP_GET)) return false;

        MimeType resourceMimeType;
        try {
            resourceMimeType = resourceProtocolInfo.getContentFormatMimeType();
        } catch (IllegalArgumentException ex) {
            ContentDirectoryControlPoint.LOGGER.warning(
                "Illegal resource mime type: " + resourceProtocolInfo.getContentFormat()
            );
            return false;
        }

        for (ProtocolInfo supportedProtocol : supportedProtocols) {
            // We currently only support HTTP-GET
            if (!Protocol.HTTP_GET.equals(supportedProtocol.getProtocol())) continue;
            try {
                if (supportedProtocol.getContentFormatMimeType().equals(resourceMimeType)) {
                    return true;
                } else if (supportedProtocol.getContentFormatMimeType().isCompatible(resourceMimeType)) {
                    return true;
                }
            } catch (IllegalArgumentException ex) {
                ContentDirectoryControlPoint.LOGGER.warning(
                    "Illegal MediaRenderer supported mime type: " + supportedProtocol.getContentFormat()
                );
            }
        }
        return false;
    }

    protected void sendToMediaRenderer(final int instanceId, final Service avTransportService, String uri) {

        SetAVTransportURI setAVTransportURIActionCallback =
                new SetAVTransportURI(new UnsignedIntegerFourBytes(instanceId), avTransportService, uri) {

                    @Override
                    public void success(ActionInvocation invocation) {
                        updateStatus(
                                "Successfuly sent URI to: (Instance: " + instanceId + ") " +
                                        avTransportService.getDevice().getDetails().getFriendlyName()
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        updateStatusFailure(
                                "Failed to send URI: " + defaultMsg
                        );
                    }
                };

        upnpService.getControlPoint().execute(setAVTransportURIActionCallback);
    }

    protected void updateStatus(String statusMessage) {
        ContentDirectoryControlPoint.LOGGER.info(statusMessage);
    }

    protected void updateStatusFailure(String statusMessage) {
        ContentDirectoryControlPoint.LOGGER.severe(statusMessage);
    }

}
