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
