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
package example.igd;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.support.model.PortMapping;

/**
 * @author Christian Bauer
 */
public class IGDSampleData {

    public static LocalService readService(Class<?> serviceClass) throws Exception {
        LocalService service = new AnnotationLocalServiceBinder().read(serviceClass);
        service.setManager(
                new DefaultServiceManager(service, serviceClass)
        );
        return service;
    }

    public static LocalDevice createIGDevice(Class<?> serviceClass) throws Exception {
        return createIGDevice(
                null,
                new LocalDevice[]{
                        createWANDevice(
                                null,
                                new LocalDevice[]{
                                        createWANConnectionDevice(new LocalService[]{readService(serviceClass)}, null)
                                }
                        )
                });
    }

    public static LocalDevice createIGDevice(LocalService[] services, LocalDevice[] embedded) throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("1111")),
                new UDADeviceType("InternetGatewayDevice", 1),
                new DeviceDetails("Example Router"),
                services,
                embedded
        );
    }

    public static LocalDevice createWANDevice(LocalService[] services, LocalDevice[] embedded) throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("2222")),
                new UDADeviceType("WANDevice", 1),
                new DeviceDetails("Example WAN Device"),
                services,
                embedded
        );
    }

    public static LocalDevice createWANConnectionDevice(LocalService[] services, LocalDevice[] embedded) throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("3333")),
                new UDADeviceType("WANConnectionDevice", 1),
                new DeviceDetails("Example WAN Connection Device"),
                services,
                embedded
        );
    }

    @UpnpService(
            serviceId = @UpnpServiceId("WANIPConnection"),
            serviceType = @UpnpServiceType("WANIPConnection")
    )
    @UpnpStateVariables({
            @UpnpStateVariable(name = "RemoteHost", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "ExternalPort", datatype = "ui2", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingProtocol", datatype = "string", sendEvents = false, allowedValuesEnum = PortMapping.Protocol.class),
            @UpnpStateVariable(name = "InternalPort", datatype = "ui2", sendEvents = false),
            @UpnpStateVariable(name = "InternalClient", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingEnabled", datatype = "boolean", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingDescription", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingLeaseDuration", datatype = "ui4", sendEvents = false),
            @UpnpStateVariable(name = "ConnectionStatus", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "LastConnectionError", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "Uptime", datatype = "ui4", sendEvents = false),
            @UpnpStateVariable(name = "ExternalIPAddress", datatype = "string", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingIndex", datatype = "ui2", sendEvents = false)

    })
    public static class WANIPConnectionService {

        @UpnpAction
        public void addPortMapping(
                @UpnpInputArgument(name = "NewRemoteHost", stateVariable = "RemoteHost") String remoteHost,
                @UpnpInputArgument(name = "NewExternalPort", stateVariable = "ExternalPort") UnsignedIntegerTwoBytes externalPort,
                @UpnpInputArgument(name = "NewProtocol", stateVariable = "PortMappingProtocol") String protocol,
                @UpnpInputArgument(name = "NewInternalPort", stateVariable = "InternalPort") UnsignedIntegerTwoBytes internalPort,
                @UpnpInputArgument(name = "NewInternalClient", stateVariable = "InternalClient") String internalClient,
                @UpnpInputArgument(name = "NewEnabled", stateVariable = "PortMappingEnabled") Boolean enabled,
                @UpnpInputArgument(name = "NewPortMappingDescription", stateVariable = "PortMappingDescription") String description,
                @UpnpInputArgument(name = "NewLeaseDuration", stateVariable = "PortMappingLeaseDuration") UnsignedIntegerFourBytes leaseDuration
        ) throws ActionException {
            try {
                addPortMapping(new PortMapping(
                        enabled,
                        leaseDuration,
                        remoteHost,
                        externalPort,
                        internalPort,
                        internalClient,
                        PortMapping.Protocol.valueOf(protocol),
                        description
                ));
            } catch (Exception ex) {
                throw new ActionException(ErrorCode.ACTION_FAILED, "Can't convert port mapping: " + ex.toString(), ex);
            }
        }

        @UpnpAction
        public void deletePortMapping(
                @UpnpInputArgument(name = "NewRemoteHost", stateVariable = "RemoteHost") String remoteHost,
                @UpnpInputArgument(name = "NewExternalPort", stateVariable = "ExternalPort") UnsignedIntegerTwoBytes externalPort,
                @UpnpInputArgument(name = "NewProtocol", stateVariable = "PortMappingProtocol") String protocol
        ) throws ActionException {
            try {
                deletePortMapping(new PortMapping(
                        remoteHost,
                        externalPort,
                        PortMapping.Protocol.valueOf(protocol)
                ));
            } catch (Exception ex) {
                throw new ActionException(ErrorCode.ACTION_FAILED, "Can't convert port mapping: " + ex.toString(), ex);
            }
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewRemoteHost", stateVariable = "RemoteHost", getterName = "getRemoteHost"),
                @UpnpOutputArgument(name = "NewExternalPort", stateVariable = "ExternalPort", getterName = "getExternalPort"),
                @UpnpOutputArgument(name = "NewProtocol", stateVariable = "PortMappingProtocol", getterName = "getProtocol"),
                @UpnpOutputArgument(name = "NewInternalPort", stateVariable = "InternalPort", getterName = "getInternalPort"),
                @UpnpOutputArgument(name = "NewInternalClient", stateVariable = "InternalClient", getterName = "getInternalClient"),
                @UpnpOutputArgument(name = "NewEnabled", stateVariable = "PortMappingEnabled", getterName = "isEnabled"),
                @UpnpOutputArgument(name = "NewPortMappingDescription", stateVariable = "PortMappingDescription", getterName = "getDescription"),
                @UpnpOutputArgument(name = "NewLeaseDuration", stateVariable = "PortMappingLeaseDuration", getterName = "getLeaseDurationSeconds")
        })
        public PortMapping getGenericPortMappingEntry(
                @UpnpInputArgument(name = "NewPortMappingIndex", stateVariable = "PortMappingIndex") UnsignedIntegerTwoBytes index
        ) throws ActionException {
            return null;
        }

        protected void addPortMapping(PortMapping portMapping) {
        }

        protected void deletePortMapping(PortMapping portMapping) {
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewConnectionStatus", stateVariable = "ConnectionStatus", getterName = "getStatus"),
                @UpnpOutputArgument(name = "NewLastConnectionError", stateVariable = "LastConnectionError", getterName = "getLastError"),
                @UpnpOutputArgument(name = "NewUptime", stateVariable = "Uptime", getterName = "getUptime")
        })
        public Connection.StatusInfo getStatusInfo() {
            return null;
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewExternalIPAddress", stateVariable = "ExternalIPAddress")
        })
        public String getExternalIPAddress() {
            return null;
        }

    }

}
