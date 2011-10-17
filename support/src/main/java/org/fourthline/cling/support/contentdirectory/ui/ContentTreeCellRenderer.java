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

package org.fourthline.cling.support.contentdirectory.ui;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 * @author Christian Bauer
 */
public class ContentTreeCellRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node.getUserObject() instanceof Container) {

            Container container = (Container) node.getUserObject();
            setText(container.getTitle());
            setIcon(expanded ? getContainerOpenIcon() : getContainerClosedIcon());

        } else if (node.getUserObject() instanceof Item) {

            Item item = (Item) node.getUserObject();
            setText(item.getTitle());

            DIDLObject.Class upnpClass = item.getClazz();
            setIcon(getItemIcon(item, upnpClass != null ? upnpClass.getValue() : null));

        } else if (node.getUserObject() instanceof String) {
            setIcon(getInfoIcon());
        }

        onCreate();
        return this;
    }

    protected void onCreate() {

    }

    protected Icon getContainerOpenIcon() {
        return null;
    }

    protected Icon getContainerClosedIcon() {
        return null;
    }

    protected Icon getItemIcon(Item item, String upnpClass) {
        return null;
    }

    protected Icon getInfoIcon() {
        return null;
    }

}
