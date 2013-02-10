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

package org.fourthline.cling.workbench.main.impl;

import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.main.WorkbenchToolbarView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Christian Bauer
 */
@Singleton
public class WorkbenchToolbarViewImpl extends JToolBar implements WorkbenchToolbarView {

    final protected JButton demoButton =
            new JButton("Create Demo Device", Application.createImageIcon(Workbench.class, "img/24/lightbulb.png"));

    final protected JButton enableNetworkButton =
            new JButton("Enable Network", Application.createImageIcon(Workbench.class, "img/24/run.png"));

    final protected JButton disableNetworkButton =
            new JButton("Disable Network", Application.createImageIcon(Workbench.class, "img/24/stop.png"));

    protected Presenter presenter;

    @PostConstruct
    public void init() {

        setFloatable(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onCreateDemoDevice();
            }
        });
        add(demoButton);

        enableNetworkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onEnableNetwork();
            }
        });
        enableNetworkButton.setVisible(false);
        add(enableNetworkButton);

        disableNetworkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onDisableNetwork();
            }
        });
        disableNetworkButton.setVisible(true);
        add(disableNetworkButton);
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
    public void onNetworkSwitch(boolean enabled) {
        enableNetworkButton.setVisible(!enabled);
        disableNetworkButton.setVisible(enabled);
    }
}
