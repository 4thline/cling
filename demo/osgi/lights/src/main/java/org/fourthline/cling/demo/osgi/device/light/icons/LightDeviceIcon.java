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

package org.fourthline.cling.demo.osgi.device.light.icons;

import org.osgi.service.upnp.UPnPIcon;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Bruce Green
 */
public class LightDeviceIcon implements UPnPIcon {

    @Override
    public String getMimeType() {
        return "image/png";
    }

    @Override
    public int getWidth() {
        return 24;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public int getSize() {
        return 1384;
    }

    @Override
    public int getDepth() {
        return 8;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return LightDeviceIcon.class.getResourceAsStream("images/lightbulb.png");
    }
}
