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

package org.fourthline.cling.support.connectionmanager;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.support.connectionmanager.callback.ConnectionComplete;
import org.fourthline.cling.support.connectionmanager.callback.PrepareForConnection;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

/**
 * Support for setup and teardown of an arbitrary number of connections with a manager peer.
 *
 * @author Christian Bauer
 * @author Alessio Gaeta
 */
public abstract class AbstractPeeringConnectionManagerService extends ConnectionManagerService {

    final private static Logger log = Logger.getLogger(AbstractPeeringConnectionManagerService.class.getName());

    protected AbstractPeeringConnectionManagerService(ConnectionInfo... activeConnections) {
        super(activeConnections);
    }

    protected AbstractPeeringConnectionManagerService(ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo,
                                                      ConnectionInfo... activeConnections) {
        super(sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }

    protected AbstractPeeringConnectionManagerService(PropertyChangeSupport propertyChangeSupport,
                                                      ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo,
                                                      ConnectionInfo... activeConnections) {
        super(propertyChangeSupport, sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }

    synchronized protected int getNewConnectionId() {
        int currentHighestID = -1;
        for (Integer key : activeConnections.keySet()) {
            if (key > currentHighestID) currentHighestID = key;
        }
        return ++currentHighestID;
    }

    synchronized protected void storeConnection(ConnectionInfo info) {
        CSV<UnsignedIntegerFourBytes> oldConnectionIDs = getCurrentConnectionIDs();
        activeConnections.put(info.getConnectionID(), info);
        log.fine("Connection stored, firing event: " + info.getConnectionID());
        CSV<UnsignedIntegerFourBytes> newConnectionIDs = getCurrentConnectionIDs();
        getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", oldConnectionIDs, newConnectionIDs);
    }

    synchronized protected void removeConnection(int connectionID) {
        CSV<UnsignedIntegerFourBytes> oldConnectionIDs = getCurrentConnectionIDs();
        activeConnections.remove(connectionID);
        log.fine("Connection removed, firing event: " + connectionID);
        CSV<UnsignedIntegerFourBytes> newConnectionIDs = getCurrentConnectionIDs();
        getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", oldConnectionIDs, newConnectionIDs);
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID", getterName = "getConnectionID"),
            @UpnpOutputArgument(name = "AVTransportID", stateVariable = "A_ARG_TYPE_AVTransportID", getterName = "getAvTransportID"),
            @UpnpOutputArgument(name = "RcsID", stateVariable = "A_ARG_TYPE_RcsID", getterName = "getRcsID")
    })
    synchronized public ConnectionInfo prepareForConnection(
            @UpnpInputArgument(name = "RemoteProtocolInfo", stateVariable = "A_ARG_TYPE_ProtocolInfo") ProtocolInfo remoteProtocolInfo,
            @UpnpInputArgument(name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager") ServiceReference peerConnectionManager,
            @UpnpInputArgument(name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") int peerConnectionId,
            @UpnpInputArgument(name = "Direction", stateVariable = "A_ARG_TYPE_Direction") String direction)
            throws ActionException {

        int connectionId = getNewConnectionId();

        ConnectionInfo.Direction dir;
        try {
            dir = ConnectionInfo.Direction.valueOf(direction);
        } catch (Exception ex) {
            throw new ConnectionManagerException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported direction: " + direction);
        }

        log.fine("Preparing for connection with local new ID " + connectionId + " and peer connection ID: " + peerConnectionId);

        ConnectionInfo newConnectionInfo = createConnection(
                connectionId,
                peerConnectionId,
                peerConnectionManager,
                dir,
                remoteProtocolInfo
        );

        storeConnection(newConnectionInfo);

        return newConnectionInfo;
    }

    @UpnpAction
    synchronized public void connectionComplete(@UpnpInputArgument(name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") int connectionID)
            throws ActionException {
        ConnectionInfo info = getCurrentConnectionInfo(connectionID);
        log.fine("Closing connection ID " + connectionID);
        closeConnection(info);
        removeConnection(connectionID);
    }

    /**
     * Generate a new local connection identifier, prepare the peer, store connection details.
     *
     * @return <code>-1</code> if the {@link #peerFailure(org.fourthline.cling.model.action.ActionInvocation, org.fourthline.cling.model.message.UpnpResponse, String)}
     *         method had to be called, otherwise the local identifier of the established connection.
     */
    synchronized public int createConnectionWithPeer(final ServiceReference localServiceReference,
                                                     final ControlPoint controlPoint,
                                                     final Service peerService,
                                                     final ProtocolInfo protInfo,
                                                     final ConnectionInfo.Direction direction) {

        // It is important that you synchronize the whole procedure, starting with getNewConnectionID(),
        // then preparing the connection on the peer, then storeConnection()

        final int localConnectionID = getNewConnectionId();

        log.fine("Creating new connection ID " + localConnectionID + " with peer: " + peerService);
        final boolean[] failed = new boolean[1];
        new PrepareForConnection(
                peerService,
                controlPoint,
                protInfo,
                localServiceReference,
                localConnectionID,
                direction
        ) {
            @Override
            public void received(ActionInvocation invocation, int peerConnectionID, int rcsID, int avTransportID) {
                ConnectionInfo info = new ConnectionInfo(
                        localConnectionID,
                        rcsID,
                        avTransportID,
                        protInfo,
                        peerService.getReference(),
                        peerConnectionID,
                        direction.getOpposite(), // If I prepared you for output, then I do input
                        ConnectionInfo.Status.OK
                );
                storeConnection(info);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                AbstractPeeringConnectionManagerService.this.peerFailure(
                        invocation, operation, defaultMsg
                );
                failed[0] = true;
            }
        }.run(); // Synchronous execution! We "reserved" a new connection ID earlier!

        return failed[0] ? -1 : localConnectionID;
    }

    /**
     * Close the connection with the peer, remove the connection details.
     */
    synchronized public void closeConnectionWithPeer(ControlPoint controlPoint,
                                                     Service peerService,
                                                     int connectionID) throws ActionException {
        closeConnectionWithPeer(controlPoint, peerService, getCurrentConnectionInfo(connectionID));
    }

    /**
     * Close the connection with the peer, remove the connection details.
     */
    synchronized public void closeConnectionWithPeer(final ControlPoint controlPoint,
                                                     final Service peerService,
                                                     final ConnectionInfo connectionInfo) throws ActionException {

        // It is important that you synchronize the whole procedure
        log.fine("Closing connection ID " + connectionInfo.getConnectionID() + " with peer: " + peerService);
        new ConnectionComplete(
                peerService,
                controlPoint,
                connectionInfo.getPeerConnectionID()
        ) {

            @Override
            public void success(ActionInvocation invocation) {
                removeConnection(connectionInfo.getConnectionID());
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                AbstractPeeringConnectionManagerService.this.peerFailure(
                        invocation, operation, defaultMsg
                );
            }
        }.run(); // Synchronous execution!
    }

    protected abstract ConnectionInfo createConnection(int connectionID,
                                                       int peerConnectionId, ServiceReference peerConnectionManager,
                                                       ConnectionInfo.Direction direction, ProtocolInfo protocolInfo) throws ActionException;

    protected abstract void closeConnection(ConnectionInfo connectionInfo);

    /**
     * Called when connection creation or closing with a peer failed.
     * <p>
     * This is the failure result of an action invocation on the peer's connection
     * management service. The execution of the {@link #createConnectionWithPeer(org.fourthline.cling.model.ServiceReference, org.fourthline.cling.controlpoint.ControlPoint, org.fourthline.cling.model.meta.Service, org.fourthline.cling.support.model.ProtocolInfo , org.fourthline.cling.support.model.ConnectionInfo.Direction)}
     * and {@link #closeConnectionWithPeer(org.fourthline.cling.controlpoint.ControlPoint, org.fourthline.cling.model.meta.Service, org.fourthline.cling.support.model.ConnectionInfo)}
     * methods will block until this method completes handling any failure.
     * </p>
     *
     * @param invocation The underlying action invocation of the remote connection manager service.
     * @param operation The network message response if there was a response, or <code>null</code>.
     * @param defaultFailureMessage A user-friendly error message generated from the invocation exception and response.
     */
    protected abstract void peerFailure(ActionInvocation invocation, UpnpResponse operation, String defaultFailureMessage);

}
