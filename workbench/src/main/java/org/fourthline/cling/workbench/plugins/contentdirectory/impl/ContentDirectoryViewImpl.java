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

package org.fourthline.cling.workbench.plugins.contentdirectory.impl;

import org.fourthline.cling.workbench.plugins.contentdirectory.ContentDirectoryView;
import org.fourthline.cling.workbench.plugins.contentdirectory.DetailView;
import org.fourthline.cling.workbench.plugins.contentdirectory.TreeView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class ContentDirectoryViewImpl extends JDialog implements ContentDirectoryView {

    @Inject
    protected TreeView treeView;

    @Inject
    protected DetailView detailView;

    protected Presenter presenter;

    @PostConstruct
    public void init() {
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        dispose();
                    }
                }
        );

        JScrollPane containerTreePane;
        containerTreePane = new JScrollPane(treeView.asUIComponent());
        containerTreePane.setMinimumSize(new Dimension(180, 200));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        splitPane.setLeftComponent(containerTreePane);
        splitPane.setRightComponent(detailView.asUIComponent());
        splitPane.setResizeWeight(0.65);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(700, 500));
        setMinimumSize(new Dimension(500, 250));

        add(splitPane, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public TreeView getTreeView() {
        return treeView;
    }

    @Override
    public DetailView getDetailView() {
        return detailView;
    }
}
