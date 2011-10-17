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

package org.fourthline.cling.workbench.spi;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.workbench.Workbench;

import javax.inject.Inject;
import java.util.logging.Level;

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
        upnpService.getRouter().broadcast(wakeOnLANBytes);
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
        Workbench.log(Level.WARNING, "Connection failed: " + msg);
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
