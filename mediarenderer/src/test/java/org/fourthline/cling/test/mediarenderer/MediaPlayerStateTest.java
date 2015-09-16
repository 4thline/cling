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

package org.fourthline.cling.test.mediarenderer;

import org.gstreamer.Gst;
import org.gstreamer.State;
import org.fourthline.cling.mediarenderer.gstreamer.GstMediaPlayer;
import org.fourthline.cling.mediarenderer.gstreamer.GstMediaPlayers;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
/**
 * @author Christian Bauer
 */
public class MediaPlayerStateTest {

    @BeforeTest
    public void initGStreamer() {
        if (System.getProperty("jna.library.path") == null) {
            System.setProperty("jna.library.path", "/opt/local/lib");
            Gst.init();
        }
    }

    @Test
    public void lastChangePropagation() throws Exception {

        UnsignedIntegerFourBytes instanceId = new UnsignedIntegerFourBytes(0);

        LastChange avTransportLastChange = new LastChange(new AVTransportLastChangeParser());
        LastChange renderingControlLastChange = new LastChange(new RenderingControlLastChangeParser());

        GstMediaPlayers mps = new GstMediaPlayers(1, avTransportLastChange, renderingControlLastChange);
        GstMediaPlayer mp = mps.get(instanceId);

        assertEquals(mp.getPipeline().getState(), State.NULL);
        assertEquals(mp.isPlaying(), false);

        URI resourceURI = Thread.currentThread().getContextClassLoader().getResource("sample.ogv").toURI();
        mp.setURI(resourceURI);
        String lastChangeExpected =
            "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                    "<InstanceID val=\"0\">" +
                    "<AVTransportURI val=\""+resourceURI+"\"/>" +
                    "<CurrentTrackURI val=\""+resourceURI+"\"/>" +
                    "<TransportState val=\"STOPPED\"/>" +
                    "<CurrentTransportActions val=\"Play\"/>" +
                    "</InstanceID>" +
            "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();

        mp.play();
        Thread.sleep(2000);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                        "<InstanceID val=\"0\">" +
                        "<TransportState val=\"PLAYING\"/>" +
                        "<CurrentTransportActions val=\"Stop,Pause,Seek\"/>" +
                        "<CurrentTrackDuration val=\"00:00:04\"/>" +
                        "<CurrentMediaDuration val=\"00:00:04\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();

        mp.pause();
        Thread.sleep(500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                        "<InstanceID val=\"0\">" +
                        "<TransportState val=\"PAUSED_PLAYBACK\"/>" +
                        "<CurrentTransportActions val=\"Stop,Pause,Seek,Play\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();

        mp.play();
        Thread.sleep(500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                        "<InstanceID val=\"0\">" +
                        "<TransportState val=\"PLAYING\"/>" +
                        "<CurrentTransportActions val=\"Stop,Pause,Seek\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();

        mp.stop();
        Thread.sleep(200);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                        "<InstanceID val=\"0\">" +
                        "<TransportState val=\"STOPPED\"/>" +
                        "<CurrentTransportActions val=\"Play\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();
        
        mp.play();
        Thread.sleep(500);

        assertEquals(renderingControlLastChange.toString(), "");
        mp.setVolume(0.50);
        Thread.sleep(500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0//RCS/\">" +
                        "<InstanceID val=\"0\">" +
                        "<Volume channel=\"Master\" val=\"50\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(renderingControlLastChange.toString(), lastChangeExpected);
        renderingControlLastChange.reset();

        assertEquals(renderingControlLastChange.toString(), "");
        mp.setMute(true);
        Thread.sleep(500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0//RCS/\">" +
                        "<InstanceID val=\"0\">" +
                        "<Volume channel=\"Master\" val=\"0\"/>" +
                        "<Mute channel=\"Master\" val=\"1\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(renderingControlLastChange.toString(), lastChangeExpected);
        renderingControlLastChange.reset();

        assertEquals(renderingControlLastChange.toString(), "");
        mp.setMute(false);
        Thread.sleep(500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/RCS/\">" +
                        "<InstanceID val=\"0\">" +
                        "<Volume channel=\"Master\" val=\"50\"/>" +
                        "<Mute channel=\"Master\" val=\"0\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(renderingControlLastChange.toString(), lastChangeExpected);
        renderingControlLastChange.reset();

        Thread.sleep(3500);
        lastChangeExpected =
                "<Event xmlns=\"urn:schemas-upnp-org:metadata-1-0/AVT/\">" +
                        "<InstanceID val=\"0\">" +
                        "<TransportState val=\"STOPPED\"/>" +
                        "<CurrentTransportActions val=\"Play\"/>" +
                        "</InstanceID>" +
                "</Event>";
        assertEquals(avTransportLastChange.toString(), lastChangeExpected);
        avTransportLastChange.reset();

    }

}
