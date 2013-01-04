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

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.info.InfoItem;
import org.seamless.swing.Application;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 * @author Christian Bauer
 */
public class DeviceTreeCellRenderer extends DefaultTreeCellRenderer {

    protected ImageIcon rootDeviceIcon;

    public DeviceTreeCellRenderer(ImageIcon rootDeviceIcon) {
        this.rootDeviceIcon = rootDeviceIcon;
    }

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

        if (node.getUserObject() instanceof InfoItem) {
            setToolTipText(null);
            setIcon(Application.createImageIcon(Workbench.class, "img/24/info.png"));
        }

        if (node.getUserObject() instanceof Device) {
            Device nodeDevice = (Device) node.getUserObject();

            if (nodeDevice.isRoot()) {
                if (rootDeviceIcon != null) {
                    setIcon(new ImageIcon(rootDeviceIcon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH)));
                } else {
                    setIcon(Application.createImageIcon(Workbench.class, "img/24/device.png"));
                }
            } else {
                setIcon(Application.createImageIcon(Workbench.class, "img/24/device_embedded.png"));
            }

            setToolTipText(
                    nodeDevice.getDisplayString()
                            + " (UPnP Version: " + nodeDevice.getVersion().getMajor() + "." + nodeDevice.getVersion().getMinor() + ")"
            );
            setText(nodeDevice.getDetails().getFriendlyName());
        }

        if (node.getUserObject() instanceof Icon) {
            // TODO: Can't copy the URL to clipboard...
            Icon nodeIcon = (Icon) node.getUserObject();

            String uri = nodeIcon.getDevice() instanceof RemoteDevice
                    ? ((RemoteDevice) nodeIcon.getDevice()).normalizeURI(nodeIcon.getUri()).toString()
                    : nodeIcon.getUri().toString();

            setIcon(Application.createImageIcon(Workbench.class, "img/24/device_icon.png"));
            setToolTipText(uri);
            setText(uri
                            + " (" + nodeIcon.getMimeType()
                            + " " + nodeIcon.getWidth()
                            + "x" + nodeIcon.getHeight() + ")");
        }

        if (node.getUserObject() instanceof Service) {
            Service serviceNode = (Service) node.getUserObject();

            setIcon(Application.createImageIcon(Workbench.class, "img/24/service.png"));
            setToolTipText(serviceNode.getServiceId().toString());
            setText(serviceNode.getServiceType().getType());
        }

        if (node.getUserObject() instanceof Action) {
            Action nodeAction = (Action) node.getUserObject();

            setIcon(Application.createImageIcon(Workbench.class, "img/24/action.png"));
            int numOfArguments = nodeAction.getArguments().length;
            setToolTipText(numOfArguments + " argument" + (numOfArguments > 1 ? "s" : ""));
            setText(nodeAction.getName());
        }

        if (node.getUserObject() instanceof ActionArgument) {
            ActionArgument nodeActionArgument = (ActionArgument) node.getUserObject();

            if (nodeActionArgument.getDirection().equals(ActionArgument.Direction.IN)) {
                setIcon(Application.createImageIcon(Workbench.class, "img/24/argument_in.png"));
            } else {
                setIcon(Application.createImageIcon(Workbench.class, "img/24/argument_out.png"));
            }
            setToolTipText(nodeActionArgument.getRelatedStateVariableName() + ", " + nodeActionArgument.getDatatype().getDisplayString());
            setText(nodeActionArgument.getName());
        }

        if (node.getUserObject() instanceof StateVariable) {
            StateVariable nodeStateVariable = (StateVariable) node.getUserObject();

            setIcon(Application.createImageIcon(Workbench.class, "img/24/statevariable.png"));
            setToolTipText(nodeStateVariable.getTypeDetails().getDatatype().getDisplayString());
            setText(nodeStateVariable.getName() + (nodeStateVariable.getEventDetails().isSendEvents() ? " (Sends Events)" : ""));
        }

        return this;
    }
}
