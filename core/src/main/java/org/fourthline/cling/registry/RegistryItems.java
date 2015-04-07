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

import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
abstract class RegistryItems<D extends Device, S extends GENASubscription> {

    protected final RegistryImpl registry;

    protected final Set<RegistryItem<UDN, D>> deviceItems = new HashSet<>();
    protected final Set<RegistryItem<String, S>> subscriptionItems = new HashSet<>();

    RegistryItems(RegistryImpl registry) {
        this.registry = registry;
    }

    Set<RegistryItem<UDN, D>> getDeviceItems() {
        return deviceItems;
    }

    Set<RegistryItem<String, S>> getSubscriptionItems() {
        return subscriptionItems;
    }

    abstract void add(D device);
    abstract boolean remove(final D device);
    abstract void removeAll();

    abstract void maintain();
    abstract void shutdown();

    /**
     * Returns root and embedded devices registered under the given UDN.
     *
     * @param udn A unique device name.
     * @param rootOnly Set to true if only root devices (no embedded) should be searched
     * @return Any registered root or embedded device under the given UDN, <tt>null</tt> if
     *         no device with the given UDN has been registered.
     */
    D get(UDN udn, boolean rootOnly) {
        for (RegistryItem<UDN, D> item : deviceItems) {
            D device = item.getItem();
            if (device.getIdentity().getUdn().equals(udn)) {
                return device;
            }
            if (!rootOnly) {
                D foundDevice = (D)item.getItem().findDevice(udn);
                if (foundDevice != null) return foundDevice;
            }
        }
        return null;
    }

    /**
     * Returns all devices (root or embedded) with a compatible type.
     * <p>
     * This routine will check compatible versions, as described by the UDA.
     * </p>
     *
     * @param deviceType The minimum device type required.
     * @return Any registered root or embedded device with a compatible type.
     */
    Collection<D> get(DeviceType deviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {
            D[] d = (D[])item.getItem().findDevices(deviceType);
            if (d != null) {
                devices.addAll(Arrays.asList(d));
            }
        }
        return devices;
    }

    /**
     * Returns all devices (root or embedded) which have at least one matching service.
     *
     * @param serviceType The type of service to search for.
     * @return Any registered root or embedded device with at least one matching service.
     */
    Collection<D> get(ServiceType serviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {

            D[] d = (D[])item.getItem().findDevices(serviceType);
            if (d != null) {
                devices.addAll(Arrays.asList(d));
            }
        }
        return devices;
    }

    Collection<D> get() {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {
            devices.add(item.getItem());
        }
        return devices;
    }

    boolean contains(D device) {
        return contains(device.getIdentity().getUdn());
    }

    boolean contains(UDN udn) {
        return deviceItems.contains(new RegistryItem<UDN, D>(udn));
    }

    void addSubscription(S subscription) {

        RegistryItem<String, S> subscriptionItem =
                new RegistryItem<>(
                        subscription.getSubscriptionId(),
                        subscription,
                        subscription.getActualDurationSeconds()
                );

        subscriptionItems.add(subscriptionItem);
    }

    boolean updateSubscription(S subscription) {
        if (removeSubscription(subscription)) {
            addSubscription(subscription);
            return true;
        }
        return false;
    }

    boolean removeSubscription(S subscription) {
        return subscriptionItems.remove(new RegistryItem<String, S>(subscription.getSubscriptionId()));
    }

    S getSubscription(String subscriptionId) {
        for (RegistryItem<String, S> registryItem : subscriptionItems) {
            if (registryItem.getKey().equals(subscriptionId)) {
                return registryItem.getItem();
            }
        }
        return null;
    }

    Resource[] getResources(Device device) throws RegistrationException {
        try {
            return registry.getConfiguration().getNamespace().getResources(device);
        } catch (ValidationException ex) {
            throw new RegistrationException("Resource discover error: " + ex.toString(), ex);
        }
    }
}
