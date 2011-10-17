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

    final Map<Integer, InstanceView> transportViews = new LinkedHashMap();

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
