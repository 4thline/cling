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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.workbench.plugins.avtransport.AVTransportView;
import org.fourthline.cling.workbench.plugins.avtransport.InstanceView;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class AVTransportViewImpl extends JDialog implements AVTransportView {

    private final JTabbedPane tabs = new JTabbedPane();

    protected Presenter presenter;

    @Inject
    protected Instance<InstanceView> avTransportViewInstance;

    final Map<Integer, InstanceView> transportViews = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        dispose();
                        presenter.onViewDisposed();
                    }
                }
        );

        tabs.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        for (int i = 0; i < SUPPORTED_INSTANCES; i++) {
            InstanceView transportView = avTransportViewInstance.get();
            transportView.init(i);
            transportViews.put(i, transportView);
            tabs.addTab(Integer.toString(i), transportView.asUIComponent());
        }

        getContentPane().add(tabs, BorderLayout.CENTER);
        setResizable(false);
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
        for (InstanceView transportView : transportViews.values()) {
            transportView.setPresenter(presenter);
        }
    }

    @Override
    public InstanceView getInstanceView(int instanceId) {
        return transportViews.get(instanceId);
    }

    @Override
    public void setSelectionEnabled(boolean enabled) {
        for (int i = 0; i < SUPPORTED_INSTANCES; i++) {
            transportViews.get(i).setSelectionEnabled(enabled);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < SUPPORTED_INSTANCES; i++) {
            transportViews.get(i).dispose();
        }
        super.dispose();
    }
}
