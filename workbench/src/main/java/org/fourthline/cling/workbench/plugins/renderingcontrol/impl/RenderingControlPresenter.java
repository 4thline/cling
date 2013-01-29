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

package org.fourthline.cling.workbench.plugins.renderingcontrol.impl;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.fourthline.cling.workbench.plugins.renderingcontrol.RenderingControlPoint;
import org.fourthline.cling.workbench.plugins.renderingcontrol.RenderingControlView;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.*;

/**
 * @author Christian Bauer
 */
public class RenderingControlPresenter implements RenderingControlView.Presenter {

    @Inject
    protected ControlPoint controlPoint;

    @Inject
    protected RenderingControlView view;

    protected Service service;
    protected RenderingControlCallback eventCallback;

    public void init(Service service) {
        this.service = service;

        eventCallback = new RenderingControlCallback(service) {
            @Override
            protected void onDisconnect(final CancelReason reason) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String title = "DISCONNECTED: " + (reason != null ? reason.toString() : "");
                        view.setTitle(title);
                        for (int i = 0; i < RenderingControlView.SUPPORTED_INSTANCES; i++) {
                            view.getInstanceView(i).setSelectionEnabled(false);
                        }
                    }
                });
            }

            @Override
            protected void onMasterVolumeChanged(final int instanceId, final int newVolume) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        view.getInstanceView(instanceId).setVolume(newVolume);
                    }
                });
            }
        };

        StateVariableAllowedValueRange volumeRange = null;
        if (service.getStateVariable("Volume") != null) {
            volumeRange = service.getStateVariable("Volume").getTypeDetails().getAllowedValueRange();
        }
        view.init(volumeRange);
        view.setPresenter(this);
        view.setTitle(service.getDevice().getDetails().getFriendlyName());

        // Register with the service for LAST CHANGE events
        controlPoint.execute(eventCallback);

        // TODO: The initial event should contain values, section 2.3.1 rendering control spec
        RenderingControlPoint.LOGGER.info(
            "Querying initial state of RenderingControl service"
        );
        for (int i = 0; i < RenderingControlView.SUPPORTED_INSTANCES; i++) {
            updateVolume(i);
        }
    }

    @Override
    public void onViewDisposed() {
        if (eventCallback != null)
            eventCallback.end();
    }

    @PreDestroy
    public void destroy() {
        if (eventCallback != null)
            eventCallback.end();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.dispose();
            }
        });
    }

    @Override
    public void onMuteSelected(int instanceId, final boolean desiredMute) {
        controlPoint.execute(new SetMute(new UnsignedIntegerFourBytes(instanceId), service, desiredMute) {

            @Override
            public void success(ActionInvocation invocation) {
                RenderingControlPoint.LOGGER.info(
                    "Service mute set to: " + (desiredMute ? "ON" : "OFF")
                );
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                RenderingControlPoint.LOGGER.warning(
                    "Can't set mute: " + defaultMsg
                );
            }
        });
    }

    @Override
    public void onVolumeSelected(int instanceId, final int newVolume) {
        controlPoint.execute(new SetVolume(new UnsignedIntegerFourBytes(instanceId), service, newVolume) {
            @Override
            public void success(ActionInvocation invocation) {
                RenderingControlPoint.LOGGER.info(
                    "Service volume set to: " + newVolume
                );
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                RenderingControlPoint.LOGGER.warning(
                    "Can't set volume: " + defaultMsg
                );
            }
        });
    }

    protected void updateVolume(final int instanceId) {
        controlPoint.execute(new GetVolume(new UnsignedIntegerFourBytes(instanceId), service) {
            @Override
            public void received(ActionInvocation actionInvocation, final int currentVolume) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        view.getInstanceView(instanceId).setVolume(currentVolume);
                        view.getInstanceView(instanceId).setSelectionEnabled(true);
                    }
                });
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                RenderingControlPoint.LOGGER.warning(
                    "Instance ID " + instanceId + " failed, can't retrieve initial volume: " + defaultMsg
                );
                view.getInstanceView(instanceId).setSelectionEnabled(false);
            }
        });
    }

}
