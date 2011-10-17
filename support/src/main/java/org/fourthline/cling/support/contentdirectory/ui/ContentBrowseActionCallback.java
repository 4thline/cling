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

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.logging.Logger;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Updates a tree model after querying a backend <em>ContentDirectory</em> service.
 *
 * @author Christian Bauer
 */
public abstract class ContentBrowseActionCallback extends Browse {

    private static Logger log = Logger.getLogger(ContentBrowseActionCallback.class.getName());

    final protected DefaultTreeModel treeModel;
    final protected DefaultMutableTreeNode treeNode;

    public ContentBrowseActionCallback(Service service, DefaultTreeModel treeModel, DefaultMutableTreeNode treeNode) {
        super(service, ((Container) treeNode.getUserObject()).getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0, null, new SortCriterion(true, "dc:title"));
        this.treeModel = treeModel;
        this.treeNode = treeNode;
    }

    public ContentBrowseActionCallback(Service service, DefaultTreeModel treeModel, DefaultMutableTreeNode treeNode,
                                       String filter, long firstResult, long maxResults, SortCriterion... orderBy) {
        super(service, ((Container) treeNode.getUserObject()).getId(), BrowseFlag.DIRECT_CHILDREN, filter, firstResult, maxResults, orderBy);
        this.treeModel = treeModel;
        this.treeNode = treeNode;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public DefaultMutableTreeNode getTreeNode() {
        return treeNode;
    }

    public void received(final ActionInvocation actionInvocation, DIDLContent didl) {
        log.fine("Received browse action DIDL descriptor, creating tree nodes");
        final List<DefaultMutableTreeNode> childNodes = new ArrayList();

        try {

            // Containers first
            for (Container childContainer : didl.getContainers()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childContainer) {
                    @Override
                    public boolean isLeaf() {
                        return false;
                        /* TODO: UPNP VIOLATION: We can't trust the childcount attribute at all, some
                           servers return 0 even if there are children.

                        // The 'childCount' is optional, so we always have to assume that unless
                        // there is a non-zero child count, there are children and we don't know
                        // anything about them
                        Container container = ((Container) getUserObject());
                        Integer childCount = container.getChildCount();
                        return childCount != null && childCount <= 0;
                        */
                    }
                };
                childNodes.add(childNode);
            }

            // Now items
            for (Item childItem : didl.getItems()) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childItem) {
                    @Override
                    public boolean isLeaf() {
                        return true;
                    }
                };
                childNodes.add(childNode);
            }

        } catch (Exception ex) {
            log.fine("Creating DIDL tree nodes failed: " + ex);
            actionInvocation.setFailure(
                    new ActionException(ErrorCode.ACTION_FAILED, "Can't create tree child nodes: " + ex, ex)
            );
            failure(actionInvocation, null);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateTreeModel(childNodes);
            }
        });
    }

    public void updateStatus(final Status status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateStatusUI(status, treeNode, treeModel);
            }
        });
    }

    @Override
    public void failure(ActionInvocation invocation, UpnpResponse operation, final String defaultMsg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                failureUI(defaultMsg);
            }
        });
    }

    protected void updateTreeModel(final List<DefaultMutableTreeNode> childNodes) {
        log.fine("Adding nodes to tree: " + childNodes.size());
        // Remove all "old" children such as the loading/progress messages
        removeChildren();

        // Insert new children
        for (DefaultMutableTreeNode childNode : childNodes) {
            insertChild(childNode);
        }
    }

    protected void removeChildren() {
        treeNode.removeAllChildren();
        treeModel.nodeStructureChanged(treeNode);
    }

    protected void insertChild(MutableTreeNode childNode) {
        int index = treeNode.getChildCount() <= 0 ? 0 : treeNode.getChildCount();
        treeModel.insertNodeInto(childNode, treeNode, index);
    }

    public abstract void updateStatusUI(Status status, DefaultMutableTreeNode treeNode, DefaultTreeModel treeModel);

    public abstract void failureUI(String failureMessage);
}
