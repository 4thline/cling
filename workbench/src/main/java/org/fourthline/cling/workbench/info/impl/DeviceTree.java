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