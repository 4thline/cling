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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class PortMapping {

    public enum Protocol {
        UDP,
        TCP
    }

    private boolean enabled;
    private UnsignedIntegerFourBytes leaseDurationSeconds;
    private String remoteHost;
    private UnsignedIntegerTwoBytes externalPort;
    private UnsignedIntegerTwoBytes internalPort;
    private String internalClient;
    private Protocol protocol;
    private String description;

    public PortMapping() {
    }


    public PortMapping(Map<String, ActionArgumentValue<Service>> map) {
        this(
                (Boolean) map.get("NewEnabled").getValue(),
                (UnsignedIntegerFourBytes) map.get("NewLeaseDuration").getValue(),
                (String) map.get("NewRemoteHost").getValue(),
                (UnsignedIntegerTwoBytes) map.get("NewExternalPort").getValue(),
                (UnsignedIntegerTwoBytes) map.get("NewInternalPort").getValue(),
                (String) map.get("NewInternalClient").getValue(),
                Protocol.valueOf(map.get("NewProtocol").toString()),
                (String) map.get("NewPortMappingDescription").getValue()
        );
    }

    public PortMapping(int port, String internalClient, Protocol protocol) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                null,
                new UnsignedIntegerTwoBytes(port),
                new UnsignedIntegerTwoBytes(port),
                internalClient,
                protocol,
                null
        );
    }

    public PortMapping(int port, String internalClient, Protocol protocol, String description) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                null,
                new UnsignedIntegerTwoBytes(port),
                new UnsignedIntegerTwoBytes(port),
                internalClient,
                protocol,
                description
        );
    }

    public PortMapping(String remoteHost, UnsignedIntegerTwoBytes externalPort, Protocol protocol) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                remoteHost,
                externalPort,
                null,
                null,
                protocol,
                null
        );
    }

    public PortMapping(boolean enabled, UnsignedIntegerFourBytes leaseDurationSeconds, String remoteHost, UnsignedIntegerTwoBytes externalPort,
                       UnsignedIntegerTwoBytes internalPort, String internalClient, Protocol protocol, String description) {
        this.enabled = enabled;
        this.leaseDurationSeconds = leaseDurationSeconds;
        this.remoteHost = remoteHost;
        this.externalPort = externalPort;
        this.internalPort = internalPort;
        this.internalClient = internalClient;
        this.protocol = protocol;
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UnsignedIntegerFourBytes getLeaseDurationSeconds() {
        return leaseDurationSeconds;
    }

    public void setLeaseDurationSeconds(UnsignedIntegerFourBytes leaseDurationSeconds) {
        this.leaseDurationSeconds = leaseDurationSeconds;
    }

    public boolean hasRemoteHost() {
        return remoteHost != null && remoteHost.length() > 0;
    }

    public String getRemoteHost() {
        return remoteHost == null ? "-" : remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost == null || remoteHost.equals("-") || remoteHost.length() == 0 ? null : remoteHost;
    }

    public UnsignedIntegerTwoBytes getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(UnsignedIntegerTwoBytes externalPort) {
        this.externalPort = externalPort;
    }

    public UnsignedIntegerTwoBytes getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(UnsignedIntegerTwoBytes internalPort) {
        this.internalPort = internalPort;
    }

    public String getInternalClient() {
        return internalClient;
    }

    public void setInternalClient(String internalClient) {
        this.internalClient = internalClient;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public String getDescription() {
        return description == null ? "-" : description;
    }

    public void setDescription(String description) {
        this.description = description == null || description.equals("-") || description.length() == 0 ? null : description;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Protocol: " + getProtocol() + ", " + getExternalPort() + " => " + getInternalClient();
    }
}
