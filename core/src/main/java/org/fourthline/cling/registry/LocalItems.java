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

import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.SendingAsync;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
class LocalItems extends RegistryItems<LocalDevice, LocalGENASubscription> {

    private static Logger log = Logger.getLogger(Registry.class.getName());
    
    protected Map<UDN, DiscoveryOptions> discoveryOptions = new HashMap<>();
    protected long lastAliveIntervalTimestamp = 0;

    LocalItems(RegistryImpl registry) {
        super(registry);
    }

    protected void setDiscoveryOptions(UDN udn, DiscoveryOptions options) {
        if (options != null)
            this.discoveryOptions.put(udn, options);
        else
            this.discoveryOptions.remove(udn);
    }

    protected DiscoveryOptions getDiscoveryOptions(UDN udn) {
        return this.discoveryOptions.get(udn);
    }

    protected boolean isAdvertised(UDN udn) {
        // Defaults to true
        return getDiscoveryOptions(udn) == null || getDiscoveryOptions(udn).isAdvertised();
    }

    protected boolean isByeByeBeforeFirstAlive(UDN udn) {
        // Defaults to false
        return getDiscoveryOptions(udn) != null && getDiscoveryOptions(udn).isByeByeBeforeFirstAlive();
    }

    void add(LocalDevice localDevice) throws RegistrationException {
        add(localDevice, null);
    }

    void add(final LocalDevice localDevice, DiscoveryOptions options) throws RegistrationException {

        // Always set/override the options, even if we don't end up adding the device
        setDiscoveryOptions(localDevice.getIdentity().getUdn(), options);

        if (registry.getDevice(localDevice.getIdentity().getUdn(), false) != null) {
            log.fine("Ignoring addition, device already registered: " + localDevice);
            return;
        }

        log.fine("Adding local device to registry: " + localDevice);

        for (Resource deviceResource : getResources(localDevice)) {

            if (registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }

            registry.addResource(deviceResource);
            log.fine("Registered resource: " + deviceResource);

        }

        log.fine("Adding item to registry with expiration in seconds: " + localDevice.getIdentity().getMaxAgeSeconds());

        RegistryItem<UDN, LocalDevice> localItem = new RegistryItem<>(
                localDevice.getIdentity().getUdn(),
                localDevice,
                localDevice.getIdentity().getMaxAgeSeconds()
        );

        getDeviceItems().add(localItem);
        log.fine("Registered local device: " + localItem);

        if (isByeByeBeforeFirstAlive(localItem.getKey()))
            advertiseByebye(localDevice, true);

        if (isAdvertised(localItem.getKey()))
             advertiseAlive(localDevice);

        for (final RegistryListener listener : registry.getListeners()) {
            registry.getConfiguration().getRegistryListenerExecutor().execute(
                new Runnable() {
                    public void run() {
                        listener.localDeviceAdded(registry, localDevice);
                    }
                }
            );
        }

    }

    Collection<LocalDevice> get() {
        Set<LocalDevice> c = new HashSet<>();
        for (RegistryItem<UDN, LocalDevice> item : getDeviceItems()) {
            c.add(item.getItem());
        }
        return Collections.unmodifiableCollection(c);
    }

    boolean remove(final LocalDevice localDevice) throws RegistrationException {
        return remove(localDevice, false);
    }

    boolean remove(final LocalDevice localDevice, boolean shuttingDown) throws RegistrationException {

        LocalDevice registeredDevice = get(localDevice.getIdentity().getUdn(), true);
        if (registeredDevice != null) {

            log.fine("Removing local device from registry: " + localDevice);

            setDiscoveryOptions(localDevice.getIdentity().getUdn(), null);
            getDeviceItems().remove(new RegistryItem(localDevice.getIdentity().getUdn()));

            for (Resource deviceResource : getResources(localDevice)) {
                if (registry.removeResource(deviceResource)) {
                    log.fine("Unregistered resource: " + deviceResource);
                }
            }

            // Active subscriptions
            Iterator<RegistryItem<String, LocalGENASubscription>> it = getSubscriptionItems().iterator();
            while (it.hasNext()) {
                final RegistryItem<String, LocalGENASubscription> incomingSubscription = it.next();

                UDN subscriptionForUDN =
                        incomingSubscription.getItem().getService().getDevice().getIdentity().getUdn();

                if (subscriptionForUDN.equals(registeredDevice.getIdentity().getUdn())) {
                    log.fine("Removing incoming subscription: " + incomingSubscription.getKey());
                    it.remove();
                    if (!shuttingDown) {
                        registry.getConfiguration().getRegistryListenerExecutor().execute(
                                new Runnable() {
                                    public void run() {
                                        incomingSubscription.getItem().end(CancelReason.DEVICE_WAS_REMOVED);
                                    }
                                }
                        );
                    }
                }
            }

            if (isAdvertised(localDevice.getIdentity().getUdn()))
         		advertiseByebye(localDevice, !shuttingDown);

            if (!shuttingDown) {
                for (final RegistryListener listener : registry.getListeners()) {
                    registry.getConfiguration().getRegistryListenerExecutor().execute(
                            new Runnable() {
                                public void run() {
                                    listener.localDeviceRemoved(registry, localDevice);
                                }
                            }
                    );
                }
            }

            return true;
        }

        return false;
    }

    void removeAll() {
        removeAll(false);
    }

    void removeAll(boolean shuttingDown) {
        LocalDevice[] allDevices = get().toArray(new LocalDevice[get().size()]);
        for (LocalDevice device : allDevices) {
            remove(device, shuttingDown);
        }
    }

    /* ############################################################################################################ */

    public void advertiseLocalDevices() {
        for (RegistryItem<UDN, LocalDevice> localItem : deviceItems) {
            if (isAdvertised(localItem.getKey()))
                advertiseAlive(localItem.getItem());
        }
    }

    /* ############################################################################################################ */
    
    void maintain() {

    	if(getDeviceItems().isEmpty()) return ;

        Set<RegistryItem<UDN, LocalDevice>> expiredLocalItems = new HashSet<>();

        // "Flooding" is enabled, check if we need to send advertisements for all devices
        int aliveIntervalMillis = registry.getConfiguration().getAliveIntervalMillis();
        if(aliveIntervalMillis > 0) {
        	long now = System.currentTimeMillis();
        	if(now - lastAliveIntervalTimestamp > aliveIntervalMillis) {
        		lastAliveIntervalTimestamp = now;
                for (RegistryItem<UDN, LocalDevice> localItem : getDeviceItems()) {
                    if (isAdvertised(localItem.getKey())) {
                        log.finer("Flooding advertisement of local item: " + localItem);
                        expiredLocalItems.add(localItem);
                    }
                }
        	}
        } else {
            // Reset, the configuration might dynamically switch the alive interval
            lastAliveIntervalTimestamp = 0;

            // Alive interval is not enabled, regular expiration check of all devices
            for (RegistryItem<UDN, LocalDevice> localItem : getDeviceItems()) {
                if (isAdvertised(localItem.getKey()) && localItem.getExpirationDetails().hasExpired(true)) {
                    log.finer("Local item has expired: " + localItem);
                    expiredLocalItems.add(localItem);
                }
            }
        }

        // Now execute the advertisements
        for (RegistryItem<UDN, LocalDevice> expiredLocalItem : expiredLocalItems) {
            log.fine("Refreshing local device advertisement: " + expiredLocalItem.getItem());
            advertiseAlive(expiredLocalItem.getItem());
            expiredLocalItem.getExpirationDetails().stampLastRefresh();
        }

        // Expire incoming subscriptions
        Set<RegistryItem<String, LocalGENASubscription>> expiredIncomingSubscriptions = new HashSet<>();
        for (RegistryItem<String, LocalGENASubscription> item : getSubscriptionItems()) {
            if (item.getExpirationDetails().hasExpired(false)) {
                expiredIncomingSubscriptions.add(item);
            }
        }
        for (RegistryItem<String, LocalGENASubscription> subscription : expiredIncomingSubscriptions) {
            log.fine("Removing expired: " + subscription);
            removeSubscription(subscription.getItem());
            subscription.getItem().end(CancelReason.EXPIRED);
        }

    }

    void shutdown() {
        log.fine("Clearing all registered subscriptions to local devices during shutdown");
        getSubscriptionItems().clear();

        log.fine("Removing all local devices from registry during shutdown");
        removeAll(true);
    }

    /* ############################################################################################################ */

    protected Random randomGenerator = new Random();

    protected void advertiseAlive(final LocalDevice localDevice) {
        registry.executeAsyncProtocol(new Runnable() {
            public void run() {
                try {
                    log.finer("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
                    Thread.sleep(randomGenerator.nextInt(100));
                } catch (InterruptedException ex) {
                    log.severe("Background execution interrupted: " + ex.getMessage());
                }
                registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
            }
        });
    }

    protected void advertiseByebye(final LocalDevice localDevice, boolean asynchronous) {
        final SendingAsync prot = registry.getProtocolFactory().createSendingNotificationByebye(localDevice);
        if (asynchronous) {
            registry.executeAsyncProtocol(prot);
        } else {
            prot.run();
        }
    }

}
