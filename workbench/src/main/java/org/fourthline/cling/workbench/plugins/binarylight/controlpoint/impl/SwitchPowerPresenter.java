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

package org.fourthline.cling.workbench.plugins.binarylight.controlpoint.impl;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.workbench.plugins.binarylight.controlpoint.SwitchPowerControlPoint;
import org.fourthline.cling.workbench.plugins.binarylight.controlpoint.SwitchPowerView;
import org.fourthline.cling.workbench.spi.ReconnectView;
import org.fourthline.cling.workbench.spi.ReconnectingPresenter;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.*;

/**
 * @author Christian Bauer
 */
public class SwitchPowerPresenter extends ReconnectingPresenter implements SwitchPowerView.Presenter {

    protected SubscriptionCallback callback;

    @Inject
    protected SwitchPowerView view;

    @Override
    protected void init(ReconnectView reconnectView) {
        view.setPresenter(this);
        view.setReconnectView(reconnectView);

        // Connect immediately to the service
        connect(resolveService());
    }

    @Override
    protected void connect(Service service) {
        if (service == null) return;
        callback = new SwitchPowerSubscriptionCallback(service, this);
        upnpService.getControlPoint().execute(callback);
    }

    @Override
    protected void setTitle(String title) {
        view.setTitle(title);
    }

    @Override
    protected void setReconnectViewEnabled(boolean enabled) {
        view.setReconnectViewEnabled(enabled);
    }

    @Override
    public void onSwitchToggle(final boolean on) {
        Service service = resolveService();
        if (service == null) return;
        getUpnpService().getControlPoint().execute(new SetTarget(service, on) {
            @Override
            public void success(ActionInvocation invocation) {
                SwitchPowerControlPoint.LOGGER.info(
                    "Target set to: " + (on ? "ON" : "OFF")
                );
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                SwitchPowerControlPoint.LOGGER.warning(
                    "Can't set target: " + defaultMsg
                );
            }
        });
    }

    @Override
    public void onViewDisposed() {
        if (callback != null)
            callback.end(); // End subscription with service
    }

    @PreDestroy
    public void destroy() {
        if (callback != null)
            callback.end(); // End subscription with service

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.dispose();
            }
        });
    }

    public void onStatusChange(final boolean on) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SwitchPowerControlPoint.LOGGER.info(
                    "Received 'Status' value in event from service, switching to: " + on
                );
                view.toggleSwitch(on);
            }
        });
    }

}