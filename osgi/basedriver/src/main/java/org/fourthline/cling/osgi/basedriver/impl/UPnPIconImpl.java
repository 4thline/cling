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

package org.fourthline.cling.osgi.basedriver.impl;

import org.osgi.service.upnp.UPnPIcon;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteDevice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Bruce Green
 */
public class UPnPIconImpl implements UPnPIcon {
    private Icon icon;

    public UPnPIconImpl(Icon icon) {
        this.icon = icon;
    }

    @Override
    public String getMimeType() {
        return icon.getMimeType().toString();
    }

    @Override
    public int getWidth() {
        return icon.getWidth();
    }

    @Override
    public int getHeight() {
        return icon.getHeight();
    }

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public int getDepth() {
        return icon.getDepth();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream in = null;
        Device device = icon.getDevice();

        if (device instanceof RemoteDevice) {
            URL url = ((RemoteDevice) icon.getDevice()).normalizeURI(icon.getUri());

            in = url.openStream();
        }

        return in;
    }

}
