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

package org.fourthline.cling.registry;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;

/**
 * Convenience class, provides empty implementations of all methods.
 * <p>
 * Also unifies local and remote device additions and removals with
 * {@link #deviceAdded(Registry, org.fourthline.cling.model.meta.Device)} and
 * {@link #deviceRemoved(Registry, org.fourthline.cling.model.meta.Device)} methods.
 * </p>
 *
 * @author Christian Bauer
 */
public class DefaultRegistryListener implements RegistryListener {

    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {

    }

    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {

    }

    /**
     * Calls the {@link #deviceAdded(Registry, org.fourthline.cling.model.meta.Device)} method.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(registry, device);
    }

    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

    }

    /**
     * Calls the {@link #deviceRemoved(Registry, org.fourthline.cling.model.meta.Device)} method.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(registry, device);
    }

    /**
     * Calls the {@link #deviceAdded(Registry, org.fourthline.cling.model.meta.Device)} method.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   The local device added to the {@link org.fourthline.cling.registry.Registry}.
     */
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(registry, device);
    }

    /**
     * Calls the {@link #deviceRemoved(Registry, org.fourthline.cling.model.meta.Device)} method.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   The local device removed from the {@link org.fourthline.cling.registry.Registry}.
     */
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(registry, device);
    }

    public void deviceAdded(Registry registry, Device device) {
        
    }

    public void deviceRemoved(Registry registry, Device device) {

    }

    public void beforeShutdown(Registry registry) {

    }

    public void afterShutdown() {

    }
}
