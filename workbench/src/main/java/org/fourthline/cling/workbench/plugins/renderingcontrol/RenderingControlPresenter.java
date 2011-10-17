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

package org.fourthline.cling.workbench.plugins.renderingcontrol;

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
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class RenderingControlPresenter implements RenderingControlView.Presenter {

    final private static Logger log = Logger.getLogger(RenderingControlPresenter.class.getName());

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
        log.info("Querying initial state of RenderingControl service");
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
                Workbench.log(new LogMessage(
                        "RenderingControl ControlPoint", "Service mute set to: " + (desiredMute ? "ON" : "OFF")
                ));
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                log.warning("Can't set mute: " + defaultMsg);
            }
        });
    }

    @Override
    public void onVolumeSelected(int instanceId, final int newVolume) {
        controlPoint.execute(new SetVolume(new UnsignedIntegerFourBytes(instanceId), service, newVolume) {
            @Override
            public void success(ActionInvocation invocation) {
                Workbench.log(new LogMessage(
                        "RenderingControl ControlPoint", "Service volume set to: " + newVolume
                ));
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                log.warning("Can't set volume: " + defaultMsg);
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
                log.warning("Instance ID " + instanceId + " failed, can't retrieve initial volume: " + defaultMsg);
                view.getInstanceView(instanceId).setSelectionEnabled(false);
            }
        });
    }

}
