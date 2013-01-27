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
package org.fourthline.cling.mock;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.UpnpStream;

import javax.enterprise.inject.Alternative;
import java.net.InetAddress;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Alternative
public class MockRouter implements Router {

    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    protected NetworkAddressFactory networkAddressFactory;

    public MockRouter() {
    }

    public MockRouter(UpnpServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public MockRouter(UpnpServiceConfiguration configuration,
                      ProtocolFactory protocolFactory) {
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
    }

    public MockRouter(UpnpServiceConfiguration configuration,
                      ProtocolFactory protocolFactory,
                      NetworkAddressFactory networkAddressFactory) {
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        this.networkAddressFactory = networkAddressFactory;
    }

    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    @Override
    public NetworkAddressFactory getNetworkAddressFactory() {
        return networkAddressFactory;
    }

    @Override
    public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) {
        return null;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void received(IncomingDatagramMessage msg) {
    }

    @Override
    public void received(UpnpStream stream) {

    }

    @Override
    public void send(OutgoingDatagramMessage msg) {
    }

    @Override
    public StreamResponseMessage send(StreamRequestMessage msg) throws InterruptedException {
        return null;
    }

    @Override
    public void broadcast(byte[] bytes) {
    }
}
