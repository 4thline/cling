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

package org.fourthline.cling.osgi.upnp.test.device.simple.devices;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.service.upnp.UPnPDevice;

public abstract class BaseUPnPDevice implements UPnPDevice {
	private UPnPDevice parent;
	private List<String> children = new ArrayList();
	
	private Dictionary<String, Object> descriptions = new Hashtable<String, Object>();
	
	protected void setParent(BaseUPnPDevice parent) {
		this.parent = parent;
		if (parent == null) {
			getDescriptions(null).remove(UPnPDevice.PARENT_UDN);
		}
		else {
			getDescriptions(null).put(UPnPDevice.PARENT_UDN, (String) parent.getDescriptions(null).get(UPnPDevice.UDN));
		}
	}
	
	public void addChild(BaseUPnPDevice device) {
		device.setParent(this);
		children.add((String) device.getDescriptions(null).get(UPnPDevice.UDN));
		getDescriptions(null).put(UPnPDevice.CHILDREN_UDN, children.toArray(new String[children.size()]));
	}
	
	public void removeChild(BaseUPnPDevice device) {
		device.setParent(null);
		children.remove((String) device.getDescriptions(null).get(UPnPDevice.UDN));
		getDescriptions(null).put(UPnPDevice.CHILDREN_UDN, children.toArray(new String[children.size()]));
	}

	public void setDescriptions(Dictionary<String, Object> descriptions) {
		this.descriptions = descriptions;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Dictionary getDescriptions(String locale) {
		return descriptions;
	}
}
