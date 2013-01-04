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

package org.fourthline.cling.osgi.upnp.test.device.simple.icons;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.upnp.UPnPIcon;

public class SimpleTestDeviceIcon implements UPnPIcon {
	private String mimeType;
	private int width;
	private int height;
	private int size;
	private int depth;
	private String location;
	
	public SimpleTestDeviceIcon(String mimeType, int width, int height, int depth, int size, String location) {
		this.mimeType = mimeType;
		this.width = width;
		this.height = height;
		this.size = size;
		this.depth = depth;
		this.location = location;
	}
	
	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return SimpleTestDeviceIcon.class.getResourceAsStream(location);
	}
}
