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

package org.fourthline.cling.demo.osgi.device.basic;

import org.osgi.service.upnp.UPnPIcon;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Bruce Green
 */
public class UPnPBasicDeviceIcon implements UPnPIcon {
    @Override
    public String getMimeType() {
        return "image/png";
    }

    @Override
    public int getWidth() {
        return 22;
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public int getSize() {
        return 3980;
    }

    @Override
    public int getDepth() {
        return 32;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return UPnPBasicDeviceIcon.class.getResourceAsStream("icon.png");
    }
}
