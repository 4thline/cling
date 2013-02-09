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

package org.fourthline.cling.workbench.spi;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.workbench.Workbench;

import javax.inject.Inject;

/**
 * @author Christian Bauer
 */
public abstract class ReconnectingPresenter implements ReconnectView.Presenter {

    @Inject
    protected UpnpService upnpService;

    @Inject
    protected ReconnectView reconnectView;

    protected ServiceReference serviceReference;
    protected byte[] wakeOnLANBytes;

    public void init(Service service) {

        serviceReference = service.getReference();

        wakeOnLANBytes = service.getDevice() instanceof RemoteDevice
                ? ((RemoteDevice) service.getDevice()).getIdentity().getWakeOnLANBytes()
                : null;

        reconnectView.setPresenter(this);

        init(reconnectView);
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public ReconnectView getReconnectView() {
        return reconnectView;
    }

    public ServiceReference getServiceReference() {
        return serviceReference;
    }

    public byte[] getWakeOnLANBytes() {
        return wakeOnLANBytes;
    }

    @Override
    public void onConnectClicked() {
        connect(resolveService());
    }

    @Override
    public void onWakeupClicked() {
        try {
            upnpService.getRouter().broadcast(wakeOnLANBytes);
        } catch (RouterException ex) {
            Workbench.Log.MAIN.warning(
                "Broadcasting wakeup bytes on LAN failed: " + ex
            );
        }
    }

    protected Service resolveService() {
        Service service = upnpService.getRegistry().getService(serviceReference);
        if (service == null) {
            onConnectionFailure("Device service not registered/available");
            return null;
        }
        return service;
    }

    protected abstract void init(ReconnectView reconnectView);

    protected abstract void connect(Service service);

    protected abstract void setTitle(String title);

    protected abstract void setReconnectViewEnabled(boolean enabled);

    public void onConnectionFailure(String msg) {
        setTitle("Connection failed: " + msg);
        Workbench.Log.MAIN.warning("Connection failed: " + msg);
        setReconnectViewEnabled(true);
    }

    public void onConnected() {
        setTitle("Connected to service...");
        setReconnectViewEnabled(false);
    }

    public void onDisconnected() {
        setTitle("Disconnected from service!");
        setReconnectViewEnabled(true);
    }

}
