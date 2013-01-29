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

import org.fourthline.cling.support.contentdirectory.ui.ContentTree;
import org.fourthline.cling.support.contentdirectory.ui.ContentTreeCellRenderer;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.plugins.contentdirectory.ContentDirectoryControlPoint;
import org.fourthline.cling.workbench.plugins.contentdirectory.TreeDetailPresenter;
import org.fourthline.cling.workbench.plugins.contentdirectory.TreeView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Customizes the tree icons and how status updates are displayed.
 * <p>
 * Pulls data on-demand using the given control point and ContentDirectory service.
 * </p>
 *
 * @author Christian Bauer
 */
public class TreeViewImpl extends ContentTree implements TreeView {

    protected TreeDetailPresenter presenter;

    @PostConstruct
    public void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setFocusable(false);

        // Well, we can disable that
        setRootVisible(false);

        // Large icons make it easier on touchscreens
        setRowHeight(36);
        Application.increaseFontSize(this);
        setToggleClickCount(1);

        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = getSelectedNode();
                if (node == null) return;

                if (node.getUserObject() instanceof Container) {
                    presenter.onContainerSelected((Container) node.getUserObject());
                } else if (node.getUserObject() instanceof Item) {
                    presenter.onItemSelected((Item) node.getUserObject());
                }
            }
        });
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(TreeDetailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected DefaultTreeCellRenderer createContainerTreeCellRenderer() {
        return new ContentTreeCellRenderer() {

            @Override
            protected Icon getContainerOpenIcon() {
                return Application.createImageIcon(Workbench.class, "img/32/folder_grey_open.png");
            }

            @Override
            protected Icon getContainerClosedIcon() {
                return Application.createImageIcon(Workbench.class, "img/32/folder_grey.png");
            }

            @Override
            protected Icon getItemIcon(Item item, String upnpClass) {
                if (upnpClass != null) {
                    if (upnpClass.startsWith("object.item.audioItem")) {
                        return Application.createImageIcon(Workbench.class, "img/32/audio.png");
                    }
                    if (upnpClass.startsWith("object.item.videoItem")) {
                        return Application.createImageIcon(Workbench.class, "img/32/video.png");
                    }
                }
                return Application.createImageIcon(Workbench.class, "img/32/misc.png");
            }

            @Override
            protected Icon getInfoIcon() {
                return Application.createImageIcon(Workbench.class, "img/32/info.png");
            }
        };
    }

    /*
        // You don't have to do this if you are happy with the message _inside_ the tree...
        @Override
        public void updateStatus(ContentBrowseActionCallback.Status status,
                                 DefaultMutableTreeNode treeNode,
                                 DefaultTreeModel treeModel) {
            super.updateStatus(status, treeNode, treeModel);
            //controller.getStatusPanel().setStatusMessage(status.getDefaultMessage());
        }
    */

    public void failure(String message) {
        ContentDirectoryControlPoint.LOGGER.severe(message);
    }

}
