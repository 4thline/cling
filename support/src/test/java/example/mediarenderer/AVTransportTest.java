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
package example.mediarenderer;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.support.avtransport.callback.GetCurrentTransportActions;
import org.fourthline.cling.support.avtransport.callback.GetDeviceCapabilities;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Creating a renderer from scratch
 * <p>
 * Cling Support provides a state machine for managing the current state of your
 * playback engine. This feature simplifies writing a media player with a UPnP
 * renderer control interface. There are several steps involved
 * </p>
 * <div class="section">
 * <div class="title">Defining the states of the player</div>
 * <div class="content">
 * <p>
 * First, define your state machine and what states are supported by your player:
 * </p>
 * <a class="citation" href="javacode://example.mediarenderer.MyRendererStateMachine"/>
 * <p>
 * This is a very simple player with only three states: The initial state when no
 * media is present, and the Playing and Stopped states. You can also support
 * additional states, such as Paused and Recording but we want to keep this example
 * as simple as possible. (Also compare the "Theory of Operation" chapter and state
 * chart in the <em>AVTransport:1</em> specification document, section 2.5.)
 * </p>
 * <p>
 * Next, implement the states and the actions that trigger a transition from one
 * state to the other.
 * </p>
 * <a class="citation" href="javadoc://example.mediarenderer.MyRendererNoMediaPresent" style="read-title: false;"/>
 * <a class="citation" href="javadoc://example.mediarenderer.MyRendererStopped" style="read-title: false;"/>
 * <a class="citation" href="javadoc://example.mediarenderer.MyRendererPlaying" style="read-title: false;"/>
 * <p>
 * So far there wasn't much UPnP involved in writing your player - Cling just provided
 * a state machine for you and a way to signal state changes to clients through
 * the <code>LastEvent</code> interface.
 * </p>
 * </div>
 * </div>
 * <div class="section">
 * <div class="title">Registering the AVTransportService</div>
 * <div class="content">
 * <p>
 * Your next step is wiring the state machine into the UPnP service, so you can add the
 * service to a device and finally the Cling registry. First, bind the service and define
 * how the service manager will obtain an instance of your player:
 * </p>
 * <a class="citation" href="javacode://example.mediarenderer.MediaRendererSampleData#createAVTransportService()" style="include: INC1;"/>
 * <p>
 * The constructor takes two classes, one is your state machine definition, the other the
 * initial state of the machine after it has been created.
 * </p>
 * <p>
 * That's it - you are ready to add this service to a <em>MediaRenderer:1</em> device and
 * control points will see it and be able to call actions.
 * </p>
 * <p>
 * However, there is one more detail you have to consider: Propagation of <code>LastChange</code>
 * events. Whenever any player state or transition adds a "change" to <code>LastChange</code>, this
 * data will be accumulated. It will <em>not</em> be send to GENA subscribers immediately or
 * automatically! It's up to you how and when you want to flush all accumulated changes to
 * control points. A common approach would be a background thread that executes this operation every
 * second (or even more frequently):
 * </p>
 * <a class="citation" id="avtransport_flushlastchange" href="javacode://this#testCustomPlayer" style="include: INC2;"/>
 * <p>
 * Finally, note that the <em>AVTransport:1</em> specification also defines "logical"
 * player instances. For examle, a renderer that can play two URIs simultaneously would have
 * two <em>AVTransport</em> instances, each with its own identifier. The reserved identifier
 * "0" is the default for a renderer that only supports playback of a single URI at a time.
 * In Cling, each logical <em>AVTransport</em> instance is represented by one instance of a
 * state machine (with all its states) associated with one instance of the <code>AVTransport</code>
 * type. All of these objects are never shared, and they are not thread-safe. Read the documentation and
 * code of the <code>AVTransportService</code> class for more information on this feature -
 * by default it supports only a single transport instance with ID "0", you have to override
 * the <code>findInstance()</code> methods to create and support several parallel playback
 * instances.
 * </p>
 * </div>
 * </div>
 */
public class AVTransportTest {

    /**
     * Controlling a renderer
     * <p>
     * Cling Support provides several action callbacks that simplify creating a control
     * point for the <em>AVTransport</em> service. This is the client side of your player,
     * the remote control.
     * </p>
     * <p>
     * This is how you set an URI for playback:
     * </p>
     * <a class="citation" id="avtransport_ctrl1" href="javacode://this#testCustomPlayer" style="include: CTRL1;"/>
     * <p>
     * This is how you actually start playback:
     * </p>
     * <a class="citation" id="avtransport_ctrl2" href="javacode://this#testCustomPlayer" style="include: CTRL2;"/>
     * <p>
     * Explore the package <code>org.fourthline.cling.support.avtransport.callback</code> for more options.
     * </p>
     * <p>
     * Your control point can also subscribe with the service and listen for <code>LastChange</code>
     * events. Cling provides a parser so you get the same types and classes on the control point
     * as are available on the server - it's the same for sending and receiving the event data.
     * When you receive the "last change" string in your <code>SubscriptionCallback</code> you
     * can transform it, for example, this event could have been sent by the service after the
     * player transitioned from NoMediaPresent to Stopped state:
     * </p>
     * <a class="citation" id="avtransport_ctrl3" href="javacode://this#testCustomPlayer" style="include: CTRL3;"/>
     */
    @Test
    public void testCustomPlayer() throws Exception {

        LocalService<AVTransportService> service = MediaRendererSampleData.createAVTransportService();

        // Yes, it's a bit awkward to get the LastChange without a controlpoint
        final String[] lcValue = new String[1];
        PropertyChangeSupport pcs = service.getManager().getPropertyChangeSupport();
        pcs.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if (ev.getPropertyName().equals("LastChange"))
                    lcValue[0] = (String) ev.getNewValue();
            }
        });

        final boolean[] assertions = new boolean[5];

        ActionCallback getDeviceCapsAction =
                new GetDeviceCapabilities(service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, DeviceCapabilities caps) {
                        assertEquals(caps.getPlayMedia()[0].toString(), "NETWORK");
                        assertEquals(caps.getRecMedia()[0].toString(), "NOT_IMPLEMENTED");
                        assertEquals(caps.getRecQualityModes()[0].toString(), "NOT_IMPLEMENTED");
                        assertions[0] = true;
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        getDeviceCapsAction.run();

        ActionCallback setAVTransportURIAction = // DOC: CTRL1
                new SetAVTransportURI(service, "http://10.0.0.1/file.mp3", "NO METADATA") {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                }; // DOC: CTRL1
        setAVTransportURIAction.run();

        ActionCallback getTransportInfo =
                new GetTransportInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                        assertEquals(transportInfo.getCurrentTransportState(), TransportState.STOPPED);
                        assertions[1] = true;
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        getTransportInfo.run();

        ActionCallback getMediaInfoAction =
                new GetMediaInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                        assertEquals(mediaInfo.getCurrentURI(), "http://10.0.0.1/file.mp3");
                        assertEquals(mediaInfo.getCurrentURIMetaData(), "NO METADATA");
                        assertions[2] = true;
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        getMediaInfoAction.run();
        
        ActionCallback getPositionInfoAction =
                new GetPositionInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                        assertEquals(positionInfo.getTrackURI(), "http://10.0.0.1/file.mp3");
                        assertEquals(positionInfo.getTrackMetaData(), "NO METADATA");
                        assertions[3] = true;
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                        System.err.println(defaultMsg);
                    }
                };
        getPositionInfoAction.run();

        ActionCallback getCurrentTransportActions =
                new GetCurrentTransportActions(service) {
                    @Override
                    public void received(ActionInvocation invocation, TransportAction[] actions) {
                        List<TransportAction> currentActions = Arrays.asList(actions);
                        assert currentActions.contains(TransportAction.Play);
                        assert currentActions.contains(TransportAction.Stop);
                        assert currentActions.contains(TransportAction.Seek);
                        assertions[4] = true;
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        getCurrentTransportActions.run();

        LastChangeAwareServiceManager manager = (LastChangeAwareServiceManager)service.getManager();    // DOC:INC2
        manager.fireLastChange();                                                                       // DOC:INC2


        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange( // DOC:CTRL3
                new AVTransportLastChangeParser(),
                lastChangeString
        );
        assertEquals(
                lastChange.getEventedValue(
                        0, // Instance ID!
                        AVTransportVariable.AVTransportURI.class
                ).getValue(),
                URI.create("http://10.0.0.1/file.mp3")
        );
        assertEquals(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.CurrentTrackURI.class
                ).getValue(),
                URI.create("http://10.0.0.1/file.mp3")
        );
        assertEquals(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                ).getValue(),
                TransportState.STOPPED
        );// DOC:CTRL3

        ActionCallback playAction = // DOC:CTRL2
                new Play(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                }; // DOC:CTRL2
        playAction.run();

        manager.fireLastChange();

        lastChangeString = lcValue[0];
        lastChange = new LastChange(
                new AVTransportLastChangeParser(),
                lastChangeString
        );
        assertEquals(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                ).getValue(),
                TransportState.PLAYING
        );

        ActionCallback stopAction =
                new Stop(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        stopAction.run();

        manager.fireLastChange();

        lastChangeString = lcValue[0];
        lastChange = new LastChange(
                new AVTransportLastChangeParser(),
                lastChangeString
        );
        assertEquals(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                ).getValue(),
                TransportState.STOPPED
        );

        for (boolean assertion : assertions) {
            assertEquals(assertion, true);
        }

    }

}
