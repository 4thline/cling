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
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.workbench.info.InfoItem;
import org.seamless.util.io.HexBin;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * @author Christian Bauer
 */
public class DeviceTreeModel extends DefaultTreeModel {

    public DeviceTreeModel(Namespace namespace, Device device) {
        super(null);
        setRoot(createNodes(new DefaultMutableTreeNode("ROOT"), namespace, device));
    }

    protected DefaultMutableTreeNode createNodes(DefaultMutableTreeNode currentNode, Namespace namespace, Device device) {

        DefaultMutableTreeNode deviceNode = new DefaultMutableTreeNode(device);
        currentNode.add(deviceNode);

        addIfNotNull(deviceNode, "UDN: ", device.getIdentity().getUdn());
        addIfNotNull(deviceNode, "Device Type: ", device.getType().toString());
        if (device.getDetails().getDlnaDocs() != null) {
            for (DLNADoc dlnaDoc : device.getDetails().getDlnaDocs()) {
                addIfNotNull(deviceNode, "DLNA Doc: ", dlnaDoc);
            }
        }
        addIfNotNull(deviceNode, "DLNA Caps: ", device.getDetails().getDlnaCaps());

        if (device instanceof RemoteDevice) {
            addIfNotNull(deviceNode, "Descriptor URL: ", ((RemoteDevice) device).getIdentity().getDescriptorURL(), true);
        } else if (device instanceof LocalDevice) {
            addIfNotNull(deviceNode, "Descriptor URI: ", namespace.getDescriptorPath(device));
        }

        addIfNotNull(deviceNode, "Manufacturer: ", device.getDetails().getManufacturerDetails().getManufacturer());
        addIfNotNull(deviceNode, "Manufacturer URL/URI: ", device.getDetails().getManufacturerDetails().getManufacturerURI(), device);
        addIfNotNull(deviceNode, "Model Name: ", device.getDetails().getModelDetails().getModelName());
        addIfNotNull(deviceNode, "Model #: ", device.getDetails().getModelDetails().getModelNumber());
        addIfNotNull(deviceNode, "Model Description: ", device.getDetails().getModelDetails().getModelDescription());
        addIfNotNull(deviceNode, "Model URL/URI: ", device.getDetails().getModelDetails().getModelURI(), device);
        addIfNotNull(deviceNode, "Serial #: ", device.getDetails().getSerialNumber());
        addIfNotNull(deviceNode, "Universal Product Code: ", device.getDetails().getUpc());
        addIfNotNull(deviceNode, "Presentation URI: ", device.getDetails().getPresentationURI(), device);

        if (device instanceof RemoteDevice && ((RemoteDevice) device).getIdentity().getInterfaceMacAddress() != null)
            addIfNotNull(deviceNode, "MAC Ethernet Address: ", HexBin.bytesToString(((RemoteDevice) device).getIdentity().getInterfaceMacAddress(), ":"));

        if (device.hasIcons()) {
            for (Icon icon : device.getIcons()) {
                deviceNode.add(new DefaultMutableTreeNode(icon));
            }
        }

        if (device.hasServices()) {

            for (Service service : device.getServices()) {
                DefaultMutableTreeNode serviceNode = new DefaultMutableTreeNode(service);
                deviceNode.add(serviceNode);

                addIfNotNull(serviceNode, "Service Type: ", service.getServiceType().toString());
                addIfNotNull(serviceNode, "Service ID: ", service.getServiceId().toString());

                if (service instanceof LocalService) {
                    LocalService ls = (LocalService) service;
                    addIfNotNull(serviceNode, "Descriptor URI: ", namespace.getDescriptorPath(ls));
                    addIfNotNull(serviceNode, "Control URI: ", namespace.getControlPath(ls));
                    addIfNotNull(serviceNode, "Event Subscription URI: ", namespace.getEventSubscriptionPath(ls));
                    addIfNotNull(serviceNode, "Local Event Callback URI: ", namespace.getEventCallbackPath(ls));
                } else if (service instanceof RemoteService) {
                    RemoteService rs = (RemoteService) service;
                    addIfNotNull(serviceNode, "Descriptor URL: ", rs.getDevice().normalizeURI(rs.getDescriptorURI()), true);
                    addIfNotNull(serviceNode, "Control URL: ", rs.getDevice().normalizeURI(rs.getControlURI()), true);
                    addIfNotNull(serviceNode, "Event Subscription URL: ", rs.getDevice().normalizeURI(rs.getEventSubscriptionURI()), true);
                }

                for (Action action : service.getActions()) {

                    if (action instanceof QueryStateVariableAction) continue; // Skip that

                    DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action);
                    serviceNode.add(actionNode);

                    int i = 0;
                    for (ActionArgument actionArgument : action.getArguments()) {
                        DefaultMutableTreeNode actionArgumentNode = new DefaultMutableTreeNode(actionArgument);
                        actionNode.add(actionArgumentNode);

                        addIfNotNull(actionArgumentNode, i++ + " Direction: ", actionArgument.getDirection());
                        addIfNotNull(actionArgumentNode, "Related State Variable: ", actionArgument.getRelatedStateVariableName());
                        addIfNotNull(actionArgumentNode, "Datatype: ", actionArgument.getDatatype().getDisplayString());
                    }
                }

                for (StateVariable stateVariable : service.getStateVariables()) {
                    DefaultMutableTreeNode stateVariableNode = new DefaultMutableTreeNode(stateVariable);
                    serviceNode.add(stateVariableNode);

                    addIfNotNull(stateVariableNode, "Datatype: ", stateVariable.getTypeDetails().getDatatype().getDisplayString());
                    addIfNotNull(stateVariableNode, "Default Value: ", stateVariable.getTypeDetails().getDefaultValue());

                    if (stateVariable.getTypeDetails().getAllowedValues() != null) {
                        for (String allowedValue : stateVariable.getTypeDetails().getAllowedValues()) {
                            addIfNotNull(stateVariableNode, "Allowed Value: ", allowedValue);
                        }
                    }

                    if (stateVariable.getTypeDetails().getAllowedValueRange() != null) {
                        StateVariableAllowedValueRange range = stateVariable.getTypeDetails().getAllowedValueRange();
                        addIfNotNull(stateVariableNode, "Allowed Value Range Minimum: ", range.getMinimum());
                        addIfNotNull(stateVariableNode, "Allowed Value Range Maximum: ", range.getMaximum());
                        addIfNotNull(stateVariableNode, "Allowed Value Range Step: ", range.getStep());
                    }

                }
            }
        }

        if (device.hasEmbeddedDevices()) {
            for (Device embedded : device.getEmbeddedDevices()) {
                createNodes(deviceNode, namespace, embedded);
            }
        }

        return currentNode;
    }

    protected void addIfNotNull(DefaultMutableTreeNode parent, String info, URI uri, Device device) {
        if (device instanceof RemoteDevice) {
            try {
                addIfNotNull(parent, info, uri != null ? ((RemoteDevice) device).normalizeURI(uri) : null, true);
            } catch (IllegalArgumentException ex) {
                if (ex.getCause() != null && ex.getCause() instanceof MalformedURLException) {
                    // If it's an unsupported URL (which in Java is pretty much anything but HTTP) show the plain text
                    addIfNotNull(parent, info, uri, false);
                } else {
                    throw ex;
                }
            }
        } else if (device instanceof LocalDevice) {
            addIfNotNull(parent, info, uri, false);
        }
    }

    protected void addIfNotNull(DefaultMutableTreeNode parent, String info, Object data) {
        addIfNotNull(parent, info, data, false);
    }

    protected void addIfNotNull(DefaultMutableTreeNode parent, String info, Object data, boolean isUrl) {
        if (data != null) {
            parent.add(new DefaultMutableTreeNode(
                new InfoItem(info, data, isUrl)
            ));
        }
    }
}
