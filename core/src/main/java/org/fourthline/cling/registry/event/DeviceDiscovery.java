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

package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.Device;

/**
 * An observable event for CDI containers.
 *
 * @author Christian Bauer
 */
public class DeviceDiscovery<D extends Device> {

    protected D device;

    public DeviceDiscovery(D device) {
        this.device = device;
    }

    public D getDevice() {
        return device;
    }
}
