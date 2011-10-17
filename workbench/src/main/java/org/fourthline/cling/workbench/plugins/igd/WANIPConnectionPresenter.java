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

package org.fourthline.cling.workbench.plugins.igd;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.igd.callback.GetExternalIP;
import org.fourthline.cling.support.igd.callback.GetStatusInfo;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class WANIPConnectionPresenter implements WANIPConnectionView.Presenter {

    @Inject
    protected ControlPoint controlPoint;

    @Inject
    protected WANIPConnectionView view;

    @Inject
    protected PortMappingPresenter portMappingPresenter;

    protected Service service;

    public void init(Service service) {
        this.service = service;

        view.setPresenter(this);
        view.setTitle("WAN IP Connection on " + service.getDevice().getRoot().getDetails().getFriendlyName());

        portMappingPresenter.init(
                view.getPortMappingListView(),
                view.getPortMappingEditView(),
                service,
                this
        );

        updateConnectionInfo();
    }

    @PreDestroy
    public void destroy() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.dispose();
            }
        });
    }

    protected void updateConnectionInfo() {
        controlPoint.execute(
                new GetExternalIP(service) {
                    @Override
                    protected void success(final String externalIPAddress) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                view.setExternalIP(externalIPAddress);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "WANIPConnection ControlPoint",
                                "Can't retrieve external IP: " + defaultMsg
                        ));
                    }
                }
        );

        controlPoint.execute(
                new GetStatusInfo(service) {
                    @Override
                    protected void success(final Connection.StatusInfo statusInfo) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                view.setStatusInfo(statusInfo);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "WANIPConnection ControlPoint",
                                "Can't retrieve connection status: " + defaultMsg
                        ));
                    }
                }
        );
    }

}
