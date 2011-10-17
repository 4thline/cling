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
