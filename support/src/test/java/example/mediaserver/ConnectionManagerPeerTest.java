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
package example.mediaserver;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.connectionmanager.AbstractPeeringConnectionManagerService;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.testng.Assert.assertEquals;


/**
 * Managing connections between peers
 * <p>
 * You'd probably agree that the <em>ConnectionManager</em> is unnecessary when the
 * media player <strong>pulls</strong> the media data with a HTTP GET request on the provided URL.
 * Understand that the UPnP <em>MediaServer</em> device provides the URL; if it also serves the
 * file named in the URL, that is outside of the scope of UPnP although a common system architecture.
 * </p>
 * <p>
 * Then again, when the source of the media data has to <strong>push</strong> the data to
 * the player, or prepare the connection with the player beforehand, the <em>ConnectionManager</em>
 * service becomes useful. In this situation two connection managers would first negotiate a
 * connection with the <code>PrepareForConnection</code> action - which side initiates this is
 * up to you. Once the media finished playing, one of the connection managers will then
 * call the <code>ConnectionComplete</code> action. A connection has a unique identifier and
 * some associated protocol information, the connection managers handle the connection as peers.
 * </p>
 * <p>
 * Cling Support provides an <code>AbstractPeeringConnectionManagerService</code> that will do
 * all the heavy lifting for you, all you have to do is implement the creation and closing of
 * connections. Although we are still discussing this in the context of a media server, this
 * peer negotiation of a connection naturally also has to be implemented on the media renderer/player
 * side. The following examples are therefore also relevant for the connection manager of
 * a <em>MediaRenderer</em>.
 * </p>
 * <p>
 * First, implement how you want to manage the connection on both ends of the connection
 * (this is just one side):
 * </p>
 * <a class="citation" href="javadoc://example.mediaserver.ConnectionManagerPeerTest.PeeringConnectionManager" style="read-title: false;"/>
 * <p>
 * Let's create a connection between two connection manager peers. First, create the service
 * acting as the source (let's also assume that this is the media server representing the source
 * of the media data):
 * </p>
 * <a class="citation" href="javacode://this#createDestroyConnections" style="include: INC1;"/>
 * <p>
 * You can see that it provides media metadata with several protocols. The sink (or
 * media renderer) is the peer connection manager:
 * </p>
 * <a class="citation" href="javacode://this#createDestroyConnections" style="include: INC2;" id="conmgr_sink"/>
 * <p>
 * It plays only one particular protocol.
 * </p>
 * <p>
 * The <code>createService()</code> method is simply setting the connection manager
 * instance on the service, after reading the service metadata from (already provided) annotations:
 * </p>
 * <a class="citation" href="javacode://this#createService(example.mediaserver.ConnectionManagerPeerTest.PeeringConnectionManager)"/>
 * <p>
 * Now one of the peers has to initiate the connection. It has to create a connection identifier, store this
 * identifier ("managing" the connection), and call the <code>PrepareForConnection</code> service of the
 * other peer. All of this is provided and encapsulated in the <code>createConnectionWithPeer()</code>
 * method:
 * </p>
 * <a class="citation" href="javacode://this#createDestroyConnections" style="include: INC3;" id="conmgr_prepare"/>
 * <p>
 * You have to provide a reference to the local service, a <code>ControlPoint</code>
 * to execute the action, and the protocol information you want to use for this connection. The
 * direction (<code>Input</code> in this case) is how the remote peer should handle the data
 * transmitted on this connection (again, we assume the peer is the data sink). The method returns
 * the identifer of the new connection. You can use this identifier to obtain
 * more information about the connection, for example the identifier of the connection assigned by
 * the other peer, or the logical service identifier for the AV Transport service, also assigned
 * by the remote peer.
 * </p>
 * <p>
 * When you are done with the connection, close it with the peer:
 * </p>
 * <a class="citation" href="javacode://this#createDestroyConnections" style="include: INC4;" id="conmgr_close"/>
 * <p>
 * The <code>peerFailure()</code> method shown earlier will be called when
 * an invocation of <code>createConnectionWithPeer()</code> or
 * <code>closeConnectionWithPeer()</code> fails.
 * </p>
 */
public class ConnectionManagerPeerTest {

    @Test
    public void createDestroyConnections() throws Exception {

        // Ignore this
        ControlPoint controlPoint = null;

        CountingListener listener = new CountingListener();

        PeeringConnectionManager peerOne =                                                      // DOC: INC1
            new PeeringConnectionManager(
                    new ProtocolInfos("http-get:*:video/mpeg:*,http-get:*:audio/mpeg:*"),
                    null
            );
        LocalService<PeeringConnectionManager> peerOneService = createService(peerOne);        // DOC: INC1

        peerOne.getPropertyChangeSupport().addPropertyChangeListener(listener);
        createDevice("MEDIASERVER-AAA-AAA-AAA", "MediaServer", peerOneService);

        PeeringConnectionManager peerTwo =                                                     // DOC: INC2
            new PeeringConnectionManager(
                    null,
                    new ProtocolInfos("http-get:*:video/mpeg:*")
            );
        LocalService<PeeringConnectionManager> peerTwoService = createService(peerTwo);     // DOC: INC2

        peerTwo.getPropertyChangeSupport().addPropertyChangeListener(listener);
        createDevice("MEDIARENDERER-BBB-BBB-BBB", "MediaRenderer", peerTwoService);

        int peerOneConnectionID = peerOne.createConnectionWithPeer(                                  // DOC: INC3
            peerOneService.getReference(),
            controlPoint,
            peerTwoService,
            new ProtocolInfo("http-get:*:video/mpeg:*"),
            ConnectionInfo.Direction.Input
        );

        if (peerOneConnectionID == -1) {
            // Connection establishment failed, the peerFailure()
            // method has been called already. It's up to you
            // how you'd like to continue at this point.
        }
        
        int peerTwoConnectionID =
                peerOne.getCurrentConnectionInfo(peerOneConnectionID) .getPeerConnectionID();

        int peerTwoAVTransportID =
                peerOne.getCurrentConnectionInfo(peerOneConnectionID).getAvTransportID();           // DOC: INC3

        assertEquals(peerOne.getCurrentConnectionIDs().size(), 1);
        assertEquals(peerTwo.getCurrentConnectionIDs().size(), 1);

        assertEquals(peerOne.getCurrentConnectionInfo(peerOneConnectionID).getDirection(), ConnectionInfo.Direction.Output);
        assertEquals(peerTwo.getCurrentConnectionInfo(peerTwoConnectionID).getDirection(), ConnectionInfo.Direction.Input);

        assertEquals(peerOne.getCurrentConnectionInfo(peerOneConnectionID).getRcsID(), 111);
        assertEquals(peerTwo.getCurrentConnectionInfo(peerTwoConnectionID).getRcsID(), 111);
        assertEquals(peerOne.getCurrentConnectionInfo(peerOneConnectionID).getAvTransportID(), 333);
        assertEquals(peerTwo.getCurrentConnectionInfo(peerTwoConnectionID).getAvTransportID(), 333);

        assertEquals(peerOne.getCurrentConnectionInfo(peerOneConnectionID).getConnectionStatus(), ConnectionInfo.Status.OK);
        assertEquals(peerTwo.getCurrentConnectionInfo(peerTwoConnectionID).getConnectionStatus(), ConnectionInfo.Status.OK);

        // Another connection
        int anotherID = peerOne.createConnectionWithPeer(
            peerOneService.getReference(),
            controlPoint,
            peerTwoService,
            new ProtocolInfo("http-get:*:video/mpeg:*"),
            ConnectionInfo.Direction.Input
        );
        int anotherPeerID = peerOne.getCurrentConnectionInfo(anotherID).getPeerConnectionID();

        assertEquals(peerOne.getCurrentConnectionIDs().size(), 2);
        assertEquals(peerTwo.getCurrentConnectionIDs().size(), 2);

        assertEquals(peerOne.getCurrentConnectionInfo(anotherID).getRcsID(), 222);
        assertEquals(peerTwo.getCurrentConnectionInfo(anotherPeerID).getRcsID(), 222);
        assertEquals(peerOne.getCurrentConnectionInfo(anotherID).getAvTransportID(), 444);
        assertEquals(peerTwo.getCurrentConnectionInfo(anotherPeerID).getAvTransportID(), 444);

        // Close one
        peerOne.closeConnectionWithPeer(                                            // DOC: INC4
                controlPoint,
                peerTwoService,
                peerOneConnectionID
        );                                                                          // DOC: INC4

        assertEquals(peerOne.getCurrentConnectionIDs().size(), 1);
        assertEquals(peerTwo.getCurrentConnectionIDs().size(), 1);

        // The other is still there
        assertEquals(peerOne.getCurrentConnectionInfo(anotherID).getPeerConnectionID(), 1);

        // Should have 2 + 2 + 2 (connect, connect, disconnect on both connectionmanagers) events
        assertEquals(listener.count, 6);
    }

    public LocalDevice createDevice(String udn, String type, LocalService service) throws ValidationException {
        return new LocalDevice(
                new DeviceIdentity(new UDN(udn)),
                new UDADeviceType(type),
                new DeviceDetails(type),
                service
        );
    }

    public LocalService<PeeringConnectionManager> createService(final PeeringConnectionManager peer) {

        LocalService<PeeringConnectionManager> service =
                new AnnotationLocalServiceBinder().read(
                        AbstractPeeringConnectionManagerService.class
                );

        service.setManager(
                new DefaultServiceManager<PeeringConnectionManager>(service, null) {
                    @Override
                    protected PeeringConnectionManager createServiceInstance() throws Exception {
                        return peer;
                    }
                }
        );
        return service;
    }

    /**
     * <a class="citation" href="javacode://this" style="exclude: EXC1"/>
     * <p>
     * In the <code>createConnection()</code> method you have to provide the identifiers of your
     * Rendering Control and A/V Transport logical service, responsible for the created connection.
     * The connection ID has already been stored for you, so all you have to do is return the
     * connection information with these identifiers.
     * </p>
     * <p>
     * The <code>closeConnection()</code> method is the counterpart, here you would tear down
     * your logical services for this connection, or do whatever cleanup is necessary.
     * </p>
     * <p>
     * The <code>peerFailure()</code> message is not related to the two previous messages. It is
     * only used by a connection manager that invokes the actions, not on the receiving side.
     * </p>
     */
    public class PeeringConnectionManager extends AbstractPeeringConnectionManagerService {

        PeeringConnectionManager(ProtocolInfos sourceProtocolInfo,
                                 ProtocolInfos sinkProtocolInfo) {
            super(sourceProtocolInfo, sinkProtocolInfo);
        }

        @Override
        protected ConnectionInfo createConnection(int connectionID,
                                                  int peerConnectionId,
                                                  ServiceReference peerConnectionManager,
                                                  ConnectionInfo.Direction direction,
                                                  ProtocolInfo protocolInfo)
                throws ActionException {

            // Create the connection on "this" side with the given ID now...
            ConnectionInfo con = new ConnectionInfo(
                    connectionID,
                    123, // Logical Rendering Control service ID
                    456, // Logical AV Transport service ID
                    protocolInfo,
                    peerConnectionManager,
                    peerConnectionId,
                    direction,
                    ConnectionInfo.Status.OK
            );

            // DOC: EXC1
            con = new ConnectionInfo(
                    connectionID,
                    connectionID == 0 ? 111 : 222,
                    connectionID == 0 ? 333 : 444,
                    protocolInfo,
                    peerConnectionManager,
                    peerConnectionId,
                    direction,
                    ConnectionInfo.Status.OK
            );
            // DOC: EXC1
            return con;
        }

        @Override
        protected void closeConnection(ConnectionInfo connectionInfo) {
            // Close the connection
        }

        @Override
        protected void peerFailure(ActionInvocation invocation,
                                   UpnpResponse operation,
                                   String defaultFailureMessage) {
            System.err.println("Error managing connection with peer: " + defaultFailureMessage);
        }
    }

    class CountingListener implements PropertyChangeListener {
        int count = 0;

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("CurrentConnectionIDs")) {
                count++;
            }
        }
    }

}
