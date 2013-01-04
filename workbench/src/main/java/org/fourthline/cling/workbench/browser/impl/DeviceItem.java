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

package org.fourthline.cling.workbench.browser.impl;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDN;

import javax.swing.ImageIcon;

/**
 * Wraps a <tt>Device</tt> for display with icon and label.
 * <p/>
 * Equality is implemented with UDN comparison.
 * </p>
 *
 * @author Christian Bauer
 */
public class DeviceItem {

    private UDN udn;
    private Device device;
    private String[] label;
    private ImageIcon icon;

    public DeviceItem(Device device) {
        this.udn = device.getIdentity().getUdn();
        this.device = device;
    }

    public DeviceItem(Device device, String... label) {
        this.udn = device.getIdentity().getUdn();
        this.device = device;
        this.label = label;
    }

    public UDN getUdn() {
        return udn;
    }

    public Device getDevice() {
        return device;
    }

    public String[] getLabel() {
        return label;
    }

    public void setLabel(String[] label) {
        this.label = label;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceItem that = (DeviceItem) o;

        if (!udn.equals(that.udn)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return udn.hashCode();
    }
}
