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

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;

/**
 * Notification of discovered device additions, removals, updates.
 * <p>
 * Add an instance of this interface to the registry to be notified when a device is
 * discovered on your UPnP network, or when it is updated, or when it disappears.
 * </p>
 * <p>
 * Implementations will be called concurrently by several threads, they should be thread-safe.
 * </p>
 * <p>
 * Listener methods are called in a separate thread, so you can execute
 * expensive procedures without spawning a new thread. The {@link #beforeShutdown(Registry)}
 * and {@link #afterShutdown()} methods are however called in the thread that is stopping
 * the registry and should not be blocking, unless you want to delay the shutdown procedure.
 * </p>
 *
 * @author Christian Bauer
 */
public interface RegistryListener {

    /**
     * Called as soon as possible after a device has been discovered.
     * <p>
     * This method will be called after SSDP notification datagrams of a new alive
     * UPnP device have been received and processed. The announced device XML descriptor
     * will be retrieved and parsed. The given {@link org.fourthline.cling.model.meta.RemoteDevice} metadata
     * is validated and partial {@link org.fourthline.cling.model.meta.Service} metadata is available. The
     * services are unhydrated, they have no actions or state variable metadata because the
     * service descriptors of the device model have not been retrieved at this point.
     * </p>
     * <p>
     * You typically do not use this method on a regular machine, this is an optimization
     * for slower UPnP hosts (such as Android handsets).
     * </p>
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with anemic service metadata.
     */
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device);

    /**
     * Called when service metadata couldn't be initialized.
     * <p>
     * If you override the {@link #remoteDeviceDiscoveryStarted(Registry, org.fourthline.cling.model.meta.RemoteDevice)}
     * method, you might want to override this method as well.
     * </p>
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with anemic service metadata.
     * @param ex       The reason why service metadata could not be initialized, or <code>null</code> if service
     *                 descriptors couldn't be retrieved at all.
     */
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex);

    /**
     * Called when complete metadata of a newly discovered device is available.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    public void remoteDeviceAdded(Registry registry, RemoteDevice device);

    /**
     * Called when a discovered device's expiration timestamp is updated.
     * <p>
     * This is a signal that a device is still alive and you typically don't have to react to this
     * event. You will be notified when a device disappears through timeout.
     * </p>
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device);

    /**
     * Called when a previously discovered device disappears.
     * <p>
     * This method will also be called when a discovered device did not update its expiration timeout
     * and has been been removed automatically by the local registry. This method will not be called
     * when the UPnP stack is shutting down.
     * </p>
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device);

    /**
     * Called after you add your own device to the {@link org.fourthline.cling.registry.Registry}.
     *
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   The local device added to the {@link org.fourthline.cling.registry.Registry}.
     */
    public void localDeviceAdded(Registry registry, LocalDevice device);

    /**
     * Called after you remove your own device from the {@link org.fourthline.cling.registry.Registry}.
     * <p>
     * This method will not be called when the UPnP stack is shutting down.
     * </p>
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     * @param device   The local device removed from the {@link org.fourthline.cling.registry.Registry}.
     */
    public void localDeviceRemoved(Registry registry, LocalDevice device);

    /**
     * Called after registry maintenance stops but before the registry is cleared.
     * <p>
     * This method should typically not block, it executes in the thread that shuts down the UPnP stack.
     * </p>
     * @param registry The Cling registry of all devices and services know to the local UPnP stack.
     */
    public void beforeShutdown(Registry registry);

    /**
     * Called after the registry has been cleared on shutdown.
     * <p>
     * This method should typically not block, it executes in the thread that shuts down the UPnP stack.
     * </p>
     */
    public void afterShutdown();

}
