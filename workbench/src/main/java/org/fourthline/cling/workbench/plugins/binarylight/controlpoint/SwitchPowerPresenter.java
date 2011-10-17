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

package org.fourthline.cling.workbench.plugins.binarylight.controlpoint;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.spi.ReconnectView;
import org.fourthline.cling.workbench.spi.ReconnectingPresenter;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class SwitchPowerPresenter extends ReconnectingPresenter implements SwitchPowerView.Presenter {

    private static Logger log = Logger.getLogger(SwitchPowerPresenter.class.getName());

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
                Workbench.log(new LogMessage(
                        "SwitchPower ControlPoint", "Target set to: " + (on ? "ON" : "OFF")
                ));
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                log.warning("Can't set target: " + defaultMsg);
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
                Workbench.log(
                        "SwitchPower ControlPoint",
                        "Received 'Status' value in event from service, switching to: " + on);
                view.toggleSwitch(on);
            }
        });
    }

}