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

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.profile.DeviceDetailsProvider;
import org.fourthline.cling.model.resource.DeviceDescriptorResource;
import org.fourthline.cling.model.resource.IconResource;
import org.fourthline.cling.model.resource.ServiceControlResource;
import org.fourthline.cling.model.resource.ServiceDescriptorResource;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The metadata of a device created on this host, by application code.
 *
 * @author Christian Bauer
 */
public class LocalDevice extends Device<DeviceIdentity, LocalDevice, LocalService> {

    final private DeviceDetailsProvider deviceDetailsProvider;

    public LocalDevice(DeviceIdentity identity) throws ValidationException {
        super(identity);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       LocalService service) throws ValidationException {
        super(identity, type, details, null, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       LocalService service) throws ValidationException {
        super(identity, type, null, null, new LocalService[]{service});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, null, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       LocalService[] services) throws ValidationException {
        super(identity, type, details, null, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService service) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService[] services) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Icon icon, LocalService[] services) throws ValidationException {
        super(identity, type, null, new Icon[]{icon}, services);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, new Icon[]{icon}, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon[] icons, LocalService service) throws ValidationException {
        super(identity, type, details, icons, new LocalService[]{service});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon[] icons, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Icon[] icons, LocalService service, LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, icons, new LocalService[]{service}, new LocalDevice[]{embeddedDevice});
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon[] icons, LocalService[] services) throws ValidationException {
        super(identity, type, details, icons, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details,
                       Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Icon[] icons, LocalService[] services, LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, null, icons, services, embeddedDevices);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public DeviceDetailsProvider getDeviceDetailsProvider() {
        return deviceDetailsProvider;
    }

    @Override
    public DeviceDetails getDetails(RemoteClientInfo info) {
        if (getDeviceDetailsProvider() != null) {
            return getDeviceDetailsProvider().provide(info);
        }
        return this.getDetails();
    }

    @Override
    public LocalService[] getServices() {
        return this.services != null ? this.services : new LocalService[0];
    }

    @Override
    public LocalDevice[] getEmbeddedDevices() {
        return this.embeddedDevices != null ? this.embeddedDevices : new LocalDevice[0];
    }

    @Override
    public LocalDevice newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details,
                                   Icon[] icons, LocalService[] services, List<LocalDevice> embeddedDevices)
            throws ValidationException {
        return new LocalDevice(
                new DeviceIdentity(udn, getIdentity().getMaxAgeSeconds()),
                version, type, details, icons,
                services,
                embeddedDevices.size() > 0 ? embeddedDevices.toArray(new LocalDevice[embeddedDevices.size()]) : null
        );
    }

    @Override
    public LocalService newInstance(ServiceType serviceType, ServiceId serviceId,
                                    URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
                                    Action<LocalService>[] actions, StateVariable<LocalService>[] stateVariables) throws ValidationException {
        return new LocalService(
                serviceType, serviceId,
                actions, stateVariables
        );
    }

    @Override
    public LocalDevice[] toDeviceArray(Collection<LocalDevice> col) {
        return col.toArray(new LocalDevice[col.size()]);
    }

    @Override
    public LocalService[] newServiceArray(int size) {
        return new LocalService[size];
    }

    @Override
    public LocalService[] toServiceArray(Collection<LocalService> col) {
        return col.toArray(new LocalService[col.size()]);
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(super.validate());

        // We have special rules for local icons, the URI must always be a relative path which will
        // be added to the device base URI!
        if (hasIcons()) {
            for (Icon icon : getIcons()) {
                if (icon.getUri().isAbsolute()) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI can not be absolute: " + icon.getUri()
                    ));
                }
                if (icon.getUri().toString().contains("../")) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI must not contain '../': " + icon.getUri()
                    ));
                }
                if (icon.getUri().toString().startsWith("/")) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI must not start with '/': " + icon.getUri()
                    ));
                }
            }
        }

        return errors;
    }

    @Override
    public Resource[] discoverResources(Namespace namespace) {
        List<Resource> discovered = new ArrayList<>();

        // Device
        if (isRoot()) {
            // This should guarantee that each logical local device tree (with all its embedded devices) has only
            // one device descriptor resource - because only one device in the tree isRoot().
            discovered.add(new DeviceDescriptorResource(namespace.getDescriptorPath(this), this));
        }

        // Services
        for (LocalService service : getServices()) {

            discovered.add(
                    new ServiceDescriptorResource(namespace.getDescriptorPath(service), service)
            );

            // Control
            discovered.add(
                    new ServiceControlResource(namespace.getControlPath(service), service)
            );

            // Event subscription
            discovered.add(
                    new ServiceEventSubscriptionResource(namespace.getEventSubscriptionPath(service), service)
            );

        }

        // Icons
        for (Icon icon : getIcons()) {
            discovered.add(new IconResource(namespace.prefixIfRelative(this, icon.getUri()), icon));
        }

        // Embedded devices
        if (hasEmbeddedDevices()) {
            for (Device embeddedDevice : getEmbeddedDevices()) {
                discovered.addAll(Arrays.asList(embeddedDevice.discoverResources(namespace)));
            }
        }

        return discovered.toArray(new Resource[discovered.size()]);
    }

    @Override
    public LocalDevice getRoot() {
        if (isRoot()) return this;
        LocalDevice current = this;
        while (current.getParentDevice() != null) {
            current = current.getParentDevice();
        }
        return current;
    }

    @Override
    public LocalDevice findDevice(UDN udn) {
        return find(udn, this);
    }

}
