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

import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.workbench.plugins.renderingcontrol.InstanceView;
import org.fourthline.cling.workbench.plugins.renderingcontrol.RenderingControlView;

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
public class RenderingControlViewImpl extends JDialog implements RenderingControlView {

    private final JTabbedPane tabs = new JTabbedPane();

    protected Presenter presenter;

    @Inject
    protected Instance<InstanceView> renderingControlViewInstance;

    final Map<Integer, InstanceView> controlViews = new LinkedHashMap<>();

    public void init(StateVariableAllowedValueRange volumeRange) {
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
            InstanceView controlView = renderingControlViewInstance.get();
            controlView.init(i, volumeRange);
            controlViews.put(i, controlView);
            tabs.addTab(Integer.toString(i), controlView.asUIComponent());
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
        for (InstanceView controlView : controlViews.values()) {
            controlView.setPresenter(presenter);
        }
    }

    @Override
    public InstanceView getInstanceView(int instanceId) {
        return controlViews.get(instanceId);
    }
}
