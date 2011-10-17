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

package org.fourthline.cling.workbench.plugins.contentdirectory;

import org.fourthline.cling.support.contentdirectory.ui.ContentTree;
import org.fourthline.cling.support.contentdirectory.ui.ContentTreeCellRenderer;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.Application;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PostConstruct;
import javax.swing.Icon;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.util.logging.Level;

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
        Workbench.log(new LogMessage(Level.SEVERE, "ContentDirectory ControlPoint", message));
    }

}
