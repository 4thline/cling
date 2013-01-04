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

package org.fourthline.cling.workbench.info.impl;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.info.DeviceView;
import org.fourthline.cling.workbench.info.InfoItem;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeviceViewImpl extends JPanel implements DeviceView {

    final protected JToolBar toolbar = new JToolBar();

    final protected JButton copyButton =
            new JButton("Copy to clipboard", Application.createImageIcon(Workbench.class, "img/16/copyclipboard.png"));

    final protected JButton queryButton =
            new JButton("Query Variable", Application.createImageIcon(Workbench.class, "img/16/querystatevar.png"));

    final protected JButton invokeButton =
            new JButton("Invoke Action", Application.createImageIcon(Workbench.class, "img/16/execute.png"));

    final protected JButton useButton =
            new JButton("Use Service", Application.createImageIcon(Workbench.class, "img/16/service.png"));

    final protected JButton monitorButton =
            new JButton("Monitor Service", Application.createImageIcon(Workbench.class, "img/16/monitor.png"));

    final protected JButton closeButton =
            new JButton("Close", Application.createImageIcon(Workbench.class, "img/16/close.png"));

    final protected DeviceTree tree = new DeviceTree();

    protected Device device;
    protected Presenter presenter;

    @PostConstruct
    public void init() {

        toolbar.setFloatable(false);

        useButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onUseService(tree.getSelectedService());
            }
        });
        useButton.setPreferredSize(new Dimension(500, 25));
        useButton.setFocusable(false);
        toolbar.add(useButton);

        monitorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onMonitorService(tree.getSelectedService());
            }
        });
        monitorButton.setPreferredSize(new Dimension(500, 25));
        monitorButton.setFocusable(false);
        toolbar.add(monitorButton);

        invokeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onActionInvoke((Action) tree.getSelectedUserObject());
            }
        });
        invokeButton.setPreferredSize(new Dimension(500, 25));
        invokeButton.setFocusable(false);
        toolbar.add(invokeButton);

        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onQueryStateVariable((StateVariable) tree.getSelectedUserObject());
            }
        });
        queryButton.setPreferredSize(new Dimension(500, 25));
        queryButton.setFocusable(false);
        toolbar.add(queryButton);

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onCopyInfoItem((InfoItem) tree.getSelectedUserObject());
            }
        });
        copyButton.setPreferredSize(new Dimension(500, 25));
        copyButton.setFocusable(false);
        toolbar.add(copyButton);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onDeviceViewClosed(DeviceViewImpl.this);
            }
        });
        closeButton.setPreferredSize(new Dimension(500, 25));
        closeButton.setFocusable(false);
        toolbar.add(closeButton);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(
                new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent event) {
                        Object item = tree.getSelectedUserObject();
                        if (item == null) return;
                        itemSelected(item, tree.getSelectedService());
                    }
                }
        );

        resetToolbar();

        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    @Override
    public String getTitle() {
        return device != null ? device.getDetails().getFriendlyName() : "(NO DEVICE)";
    }

    @Override
    public void setDevice(Namespace namespace, ImageIcon rootDeviceIcon, Device device) {
        this.device = device;
        tree.setModel(new DeviceTreeModel(namespace, device));
        tree.setCellRenderer(new DeviceTreeCellRenderer(rootDeviceIcon));
        // Expand first node (root device)
        tree.expandRow(0);
    }

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    protected void resetToolbar() {
        copyButton.setEnabled(false);
        invokeButton.setEnabled(false);
        queryButton.setEnabled(false);
        useButton.setEnabled(false);
        monitorButton.setEnabled(false);
    }

    protected void itemSelected(Object selectedItem, Service selectedService) {
        resetToolbar();

        if (selectedItem instanceof InfoItem) {
            copyButton.setEnabled(true);
        } else if (selectedItem instanceof Action) {
            invokeButton.setEnabled(true);
        } else if (selectedItem instanceof StateVariable) {
            queryButton.setEnabled(true);
        }

        if (selectedService != null) {
            useButton.setEnabled(true);
            monitorButton.setEnabled(true);
        }
    }
}
