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

package org.fourthline.cling.bridge.ui;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.ui.backend.Bridge;
import org.seamless.swing.AbstractController;
import org.seamless.swing.Controller;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class ConfigureBridgeController extends AbstractController<JFrame> {

    // Dependencies
    final protected UpnpService upnpService;
    final protected Bridge bridge;
    final protected SettingsController settingsController;
    final protected EndpointController endpointController;

    // View

    public ConfigureBridgeController(Controller parentController, UpnpService upnpService, Bridge bridge) {
        super(new JFrame("WAN Bridge"), parentController);

        this.upnpService = upnpService;
        this.bridge = bridge;

        settingsController = new SettingsController(this, upnpService, bridge);
        endpointController = new EndpointController(this, bridge);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(settingsController.getView(), BorderLayout.NORTH);
        mainPanel.add(endpointController.getView(), BorderLayout.CENTER);

        getView().add(mainPanel);
        getView().setMinimumSize(new Dimension(600, 300));
        getView().pack();

        getView().addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        ConfigureBridgeController.this.dispose();
                    }
                }
        );
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public Bridge getBridge() {
        return bridge;
    }

}
