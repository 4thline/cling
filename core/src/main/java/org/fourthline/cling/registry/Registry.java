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

package org.fourthline.cling.registry;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.ProtocolFactory;

import java.net.URI;
import java.util.Collection;

/**
 * The core of the UPnP stack, keeping track of known devices and resources.
 * <p>
 * A running UPnP stack has one <code>Registry</code>. Any discovered device is added
 * to this registry, as well as any exposed local device. The registry then maintains
 * these devices continuously (see {@link RegistryMaintainer}) and when needed refreshes
 * their announcements on the network or removes them when they have expired. The registry
 * also keeps track of GENA event subscriptions.
 * </p>
 * <p>
 * UPnP client applications typically monitor activity of the registry
 * via {@link RegistryListener}, they are inherently asynchronous.
 * </p>
 * <p>
 * The registry has to be {@link #shutdown()} properly, so it can notify all participants
 * on the network that local devices will no longer be available and cancel all
 * GENA subscriptions.
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 *
 * @author Christian Bauer
 */
public interface Registry {

    public UpnpService getUpnpService();
    public UpnpServiceConfiguration getConfiguration();
    public ProtocolFactory getProtocolFactory();

    // #################################################################################################

    /**
     * Typically called internally when the UPnP stack is stopping.
     * <p>
     * Unsubscribe all local devices and GENA subscriptions.
     * </p>
     */
    public void shutdown();

    /**
     * Stops background maintenance (thread) of registered items.
     * <p>
     * When paused, the registry will no longer remove expired remote devices if their
     * discovery announcements stop for some reason (device was turned off). Your local
     * control point will now see potentially unavailable remote devices. Outbound
     * GENA subscriptions from your local control point to remote services will not
     * be renewed automatically anymore, a remote service might drop your subscriptions
     * if you don't resume maintenance within the subscription's expiration timeout.
     * </p>
     * <p>
     * Local devices and services will not be announced periodically anymore to remote
     * control points, only when they are manually added are removed from the registry.
     * The registry will also no longer remove expired inbound GENA subscriptions to
     * local service from remote control points, if that control point for some reason
     * stops sending subscription renewal messages.
     * </p>
     */
    public void pause();

    /**
     * Resumes background maintenance (thread) of registered items.
     * <p>
     * A local control point has to handle the following situations when resuming
     * registry maintenance:
     * <p>
     * A remote device registration might have expired. This is the case when the remote
     * device stopped sending announcements while the registry was paused (maybe because
     * the device was switched off) and the registry was paused longer than the device
     * advertisement's maximum age. The registry will not know if the device is still
     * available when it resumes maintenance. However, it will simply assume that the
     * remote device is still available and restart its expiration check cycle. That means
     * a device will finally be removed from the registry, if no further announcements
     * from the device are received, when the maximum age of the device has elapsed
     * after the registry resumed operation.
     * </p>
     * <p>
     * Secondly, a remote device registration might not have expired but some of your
     * outbound GENA subscriptions to its services have not been renewed within the expected renewal
     * period. Therefore your outbound subscriptions might be invalid, because the remote
     * service can drop subscriptions when you don't renew them. On resume, the registry
     * will attempt to send renewals for all outbound GENA subscriptions that require
     * renewal, on devices that still haven't expired. If renewal fails, your subscription will
     * end with {@link org.fourthline.cling.model.gena.CancelReason#RENEWAL_FAILED}. Although
     * you then might conclude that the remote device is no longer available, a GENA renewal
     * can also fail for other reasons. The remote device will be kept and maintained in the
     * registry until it announces itself or it expires, even after a failed GENA renewal.
     * </p>
     * <p>
     * If you are providing local devices and services, resuming registry maintenance has
     * the following effects:
     * </p>
     * <p>
     * Local devices and their services are announced again immediately if the registry
     * has been paused for longer than half of the device's maximum age. Remote control
     * points will either see this as a new device advertisement (if they have dropped
     * your device while you paused maintenance) or as a regular update if you didn't
     * pause longer than the device's maximum age/expiration timeout.
     * </p>
     * <p>
     * Inbound GENA subscriptions to your local services are active, even in
     * paused state - remote control points should continue renewing the subscription.
     * If a remote control point stopped renewing a subscription without unsubscribing
     * (hard power off), an outdated inbound subscription will be detected when you
     * resume maintenance. This subscription will be cleaned up immediately on resume.
     * </p>
     */
    public void resume();

    /**
     * @return <code>true</code> if the registry has currently no running background
     *         maintenance (thread).
     */
    public boolean isPaused();

    // #################################################################################################

    public void addListener(RegistryListener listener);

    public void removeListener(RegistryListener listener);

    public Collection<RegistryListener> getListeners();

    /**
     * Called internally by the UPnP stack when the discovery protocol starts.
     * <p>
     * The registry will notify all registered listeners of this event, unless the
     * given device was already in the registry.
     * </p>
     *
     * @param device The half-hydrated (without services) metadata of the discovered device.
     * @return <code>false</code> if the device was already registered.
     */
    public boolean notifyDiscoveryStart(RemoteDevice device);

    /**
     * Called internally by the UPnP stack when the discovery protocol stopped abnormally.
     * <p>
     * The registry will notify all registered listeners of this event.
     * </p>
     *
     * @param device The half-hydrated (without services) metadata of the discovered device.
     * @param ex The cause for the interruption of the discovery protocol.
     */
    public void notifyDiscoveryFailure(RemoteDevice device, Exception ex);

    // #################################################################################################

    /**
     * Call this method to add your local device metadata.
     *
     * @param localDevice The device to add and maintain.
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
    public void addDevice(LocalDevice localDevice) throws RegistrationException;

    /**
     * Call this method to add your local device metadata.
     *
     * @param localDevice The device to add and maintain.
     * @param options Immediately effective when this device is registered.
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
    public void addDevice(LocalDevice localDevice, DiscoveryOptions options) throws RegistrationException;

    /**
     * Change the active {@link DiscoveryOptions} for the given (local device) UDN.
     *
     * @param options Set to <code>null</code> to disable any options.
     */
    public void setDiscoveryOptions(UDN udn, DiscoveryOptions options);

    /**
     * Get the currently active {@link DiscoveryOptions} for the given (local device) UDN.
     *
     * @return <code>null</code> if there are no active discovery options for the given UDN.
     */
    public DiscoveryOptions getDiscoveryOptions(UDN udn);

    /**
     * Called internally by the UPnP discovery protocol.
     *
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
    public void addDevice(RemoteDevice remoteDevice) throws RegistrationException;

    /**
     * Called internally by the UPnP discovery protocol.
     */
    public boolean update(RemoteDeviceIdentity rdIdentity);

    /**
     * Call this to remove your local device metadata.
     *
     * @return <code>true</code> if the device was registered and has been removed.
     */
    public boolean removeDevice(LocalDevice localDevice);

    /**
     * Called internally by the UPnP discovery protocol.
     */
    public boolean removeDevice(RemoteDevice remoteDevice);

    /**
     * Call this to remove any device metadata with the given UDN.
     *
     * @return <code>true</code> if the device was registered and has been removed.
     */
    public boolean removeDevice(UDN udn);

    /**
     * Clear the registry of all locally registered device metadata.
     */
    public void removeAllLocalDevices();

    /**
     * Clear the registry of all discovered remote device metadata.
     */
    public void removeAllRemoteDevices();

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
    public Device getDevice(UDN udn, boolean rootOnly);

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
    public LocalDevice getLocalDevice(UDN udn, boolean rootOnly);

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
    public RemoteDevice getRemoteDevice(UDN udn, boolean rootOnly);

    /**
     * @return All locally registered device metadata, in no particular order, or an empty collection.
     */
    public Collection<LocalDevice> getLocalDevices();

    /**
     * @return All discovered remote device metadata, in no particular order, or an empty collection.
     */
    public Collection<RemoteDevice> getRemoteDevices();

    /**
     * @return All device metadata, in no particular order, or an empty collection.
     */
    public Collection<Device> getDevices();

    /**
     * @return All device metadata of devices which implement the given type, in no particular order,
     *         or an empty collection.
     */
    public Collection<Device> getDevices(DeviceType deviceType);

    /**
     * @return All device metadata of devices which have a service that implements the given type,
     *         in no particular order, or an empty collection.
     */
    public Collection<Device> getDevices(ServiceType serviceType);

    /**
     * @return Complete service metadata for a service reference or <code>null</code> if no service
     *         for the given reference has been registered.
     */
    public Service getService(ServiceReference serviceReference);

    // #################################################################################################

    /**
     * Stores an arbitrary resource in the registry.
     *
     * @param resource The resource to maintain indefinitely (until it is manually removed).
     */
    public void addResource(Resource resource);

    /**
     * Stores an arbitrary resource in the registry.
     * <p>
     * Call this method repeatedly to refresh and prevent expiration of the resource.
     * </p>
     *
     * @param resource The resource to maintain.
     * @param maxAgeSeconds The time after which the registry will automatically remove the resource.
     */
    public void addResource(Resource resource, int maxAgeSeconds);

    /**
     * Removes a resource from the registry.
     *
     * @param resource The resource to remove.
     * @return <code>true</code> if the resource was registered and has been removed.
     */
    public boolean removeResource(Resource resource);

    /**
     * @param pathQuery The path and optional query string of the resource's
     *                  registration URI (e.g. <code>/dev/somefile.xml?param=value</code>)
     * @return Any registered resource that matches the given URI path.
     * @throws IllegalArgumentException If the given URI was absolute, only path and query are allowed.
     */
    public Resource getResource(URI pathQuery) throws IllegalArgumentException;

    /**
     * @param <T> The required subtype of the {@link org.fourthline.cling.model.resource.Resource}.
     * @param pathQuery The path and optional query string of the resource's
     *                  registration URI (e.g. <code>/dev/somefile.xml?param=value</code>)
     * @param resourceType The required subtype of the {@link org.fourthline.cling.model.resource.Resource}.
     * @return Any registered resource that matches the given URI path and subtype.
     * @throws IllegalArgumentException If the given URI was absolute, only path and query are allowed.
     */
    public <T extends Resource> T getResource(Class<T> resourceType, URI pathQuery) throws IllegalArgumentException;

    /**
     * @return All registered resources, in no particular order, or an empty collection.
     */
    public Collection<Resource> getResources();

    /**
     * @param <T> The required subtype of the {@link org.fourthline.cling.model.resource.Resource}.
     * @param resourceType The required subtype of the {@link org.fourthline.cling.model.resource.Resource}.
     * @return Any registered resource that matches the given subtype.
     */
    public <T extends Resource> Collection<T> getResources(Class<T> resourceType);

    // #################################################################################################

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public void addLocalSubscription(LocalGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public LocalGENASubscription getLocalSubscription(String subscriptionId);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public boolean updateLocalSubscription(LocalGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public boolean removeLocalSubscription(LocalGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public void addRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public RemoteGENASubscription getRemoteSubscription(String subscriptionId);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public void updateRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
    public void removeRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * When subscribing with a remote host, the remote host might send the
     * initial event message faster than the response for the subscription
     * request. This method register that the subscription procedure is
     * executing.
     * </p>
     */
    public void registerPendingRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * Notify that the subscription procedure has terminated.
     * </p>
     */
    public void unregisterPendingRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * Get a remote subscription from its subscriptionId. If the subscription can't be found,
     * wait for one of the pending remote subscription procedures from the registry background
     * maintainer to terminate, until the subscription has been found or until there are no
     * more pending subscription procedures.
     * </p>
     */
    public RemoteGENASubscription getWaitRemoteSubscription(String subscriptionId);

    // #################################################################################################

    /**
     * Manually trigger advertisement messages for all local devices.
     * <p>
     * No messages will be send for devices with disabled advertisements, see
     * {@link DiscoveryOptions}!
     * </p>
     */
    public void advertiseLocalDevices();

}
