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

package org.fourthline.cling.support.contentdirectory.ui;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;

/**
 * @author Christian Bauer
 */
public class ContentTreeExpandListener implements TreeWillExpandListener {

    final protected ControlPoint controlPoint;
    final protected Service service;
    final protected DefaultTreeModel treeModel;
    final protected ContentBrowseActionCallbackCreator actionCreator;

    public ContentTreeExpandListener(ControlPoint controlPoint,
                                     Service service,
                                     DefaultTreeModel treeModel,
                                     ContentBrowseActionCallbackCreator actionCreator) {
        this.controlPoint = controlPoint;
        this.service = service;
        this.treeModel = treeModel;
        this.actionCreator = actionCreator;
    }

    public void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

        // Remove all "old" children such as the loading/progress messages
        treeNode.removeAllChildren();
        treeModel.nodeStructureChanged(treeNode);

        // Perform the loading in a background thread
        ActionCallback callback =
                actionCreator.createContentBrowseActionCallback(
                        service, treeModel, treeNode
                );
        controlPoint.execute(callback);
    }

    public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {

    }

}
