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

import org.fourthline.cling.model.meta.Service;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Expects that tree nodes are <code>DefaultMutableTreeNode</code> with a user object!
 *
 * @author Christian Bauer
 */
public class DeviceTree extends JTree {

    public DeviceTree() {
        setRootVisible(false);
        setShowsRootHandles(true);
        putClientProperty("JTree.lineStyle", "None");
        setRowHeight(26);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public Object getSelectedUserObject() {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null) return null;
        return node.getUserObject();
    }

    public Service getSelectedService() {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null || node.getUserObject() == null) return null;

        if (node.getUserObject() instanceof Service)
            return (Service)node.getUserObject();

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
        if (parentNode == null || parentNode.getUserObject() == null) return null;

        if (parentNode.getUserObject() instanceof Service)
            return (Service)parentNode.getUserObject();

        return null;
    }

    protected DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode) getLastSelectedPathComponent();
    }

}