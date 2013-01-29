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

package org.fourthline.cling.workbench.plugins.igd.impl;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.igd.callback.GetExternalIP;
import org.fourthline.cling.support.igd.callback.GetStatusInfo;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.workbench.plugins.igd.PortMappingPresenter;
import org.fourthline.cling.workbench.plugins.igd.WANIPConnectionControlPoint;
import org.fourthline.cling.workbench.plugins.igd.WANIPConnectionView;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.*;

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
                        WANIPConnectionControlPoint.LOGGER.info(
                            "Can't retrieve external IP: " + defaultMsg
                        );
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
                        WANIPConnectionControlPoint.LOGGER.info(
                            "Can't retrieve connection status: " + defaultMsg
                        );
                    }
                }
        );
    }

}
