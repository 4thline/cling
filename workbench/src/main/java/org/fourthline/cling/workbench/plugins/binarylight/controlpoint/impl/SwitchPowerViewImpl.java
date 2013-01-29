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

import org.fourthline.cling.workbench.plugins.binarylight.controlpoint.SwitchPowerControlPoint;
import org.fourthline.cling.workbench.plugins.binarylight.controlpoint.SwitchPowerView;
import org.fourthline.cling.workbench.spi.ReconnectView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class SwitchPowerViewImpl extends JDialog implements SwitchPowerView {

    final protected ImageIcon ICON_ON =
            Application.createImageIcon(SwitchPowerControlPoint.class, "img/switch_down.png");

    final protected ImageIcon ICON_OFF =
            Application.createImageIcon(SwitchPowerControlPoint.class, "img/switch_up.png");

    final protected JToggleButton toggleButton = new JToggleButton(ICON_OFF);

    protected Presenter presenter;

    @PostConstruct
    public void init() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
                presenter.onViewDisposed();
            }
        });

        toggleButton.setBorderPainted(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setPreferredSize(new Dimension(128, 128));

        toggleButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    toggleButton.setIcon(ICON_ON);
                } else {
                    toggleButton.setIcon(ICON_OFF);
                }
            }
        });

        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.onSwitchToggle(toggleButton.isSelected());
            }
        });

        // Initial state is disabled, until we receive the initial event
        toggleButton.setEnabled(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toggleButton, BorderLayout.CENTER);
        mainPanel.setBackground(Color.WHITE);

        add(mainPanel);

        setPreferredSize(new Dimension(250, 250));
        setMinimumSize(new Dimension(250, 250));
        pack();
        setVisible(true);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setReconnectView(ReconnectView reconnectView) {
        setGlassPane(reconnectView.asUIComponent());
    }

    @Override
    public void setReconnectViewEnabled(boolean enabled) {
        getGlassPane().setVisible(enabled);
        toggleButton.setEnabled(!enabled);
    }

    @Override
    public void toggleSwitch(boolean on) {
        toggleButton.setSelected(on);

    }
}
