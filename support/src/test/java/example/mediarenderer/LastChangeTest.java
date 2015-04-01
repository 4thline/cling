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

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.lastchange.Event;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.RecordMediumWriteStatus;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;
import org.testng.annotations.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class LastChangeTest {

    @Test
    public void setFireGet() throws Exception {

        LastChange lc = new LastChange(new RenderingControlLastChangeParser());

        lc.setEventedValue(0, new RenderingControlVariable.PresetNameList("foo"));
        lc.setEventedValue(0, new RenderingControlVariable.PresetNameList("foobar")); // Double set!

        lc.setEventedValue(
                0,
                new RenderingControlVariable.Volume(
                        new ChannelVolume(Channel.Master, 123)
                )
        );

        lc.setEventedValue(1, new RenderingControlVariable.Brightness(new UnsignedIntegerTwoBytes(456)));

        final String[] lcValue = new String[1];
        PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        pcs.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if (ev.getPropertyName().equals("LastChange"))
                    lcValue[0] = (String) ev.getNewValue();
            }
        });
        lc.fire(pcs);

        // Check it's clear
        assertEquals(
                lc.getEventedValue(0, RenderingControlVariable.PresetNameList.class),
                null
        );
        assertEquals(lc.toString(), "");

        // Set something again, it's not fired, so it has no consequence on further assertions
        lc.setEventedValue(0, new RenderingControlVariable.PresetNameList("foo"));

        // Read the XML string instead
        lc = new LastChange(new RenderingControlLastChangeParser(), lcValue[0]);

        assertEquals(
                lc.getEventedValue(0, RenderingControlVariable.PresetNameList.class).getValue(),
                "foobar"
        );

        assertEquals(
                lc.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getChannel(),
                Channel.Master
        );
        assertEquals(
                lc.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getVolume(),
                new Integer(123)
        );

        assertEquals(
                lc.getEventedValue(1, RenderingControlVariable.Brightness.class).getValue(),
                new UnsignedIntegerTwoBytes(456)
        );

    }

    @Test
    public void parseLastChangeXML() throws Exception {

        LastChangeParser avTransportParser = new AVTransportLastChangeParser();

        Event event = avTransportParser.parseResource("org/fourthline/cling/test/support/lastchange/samples/avtransport-roku.xml");
        assertEquals(event.getInstanceIDs().size(), 1);
        UnsignedIntegerFourBytes instanceId = new UnsignedIntegerFourBytes(0);
        assertEquals(
                event.getEventedValue(instanceId, AVTransportVariable.TransportState.class).getValue(),
                TransportState.STOPPED
        );

        String trackMetaDataXML = event.getEventedValue(instanceId, AVTransportVariable.CurrentTrackMetaData.class).getValue();
        DIDLContent trackMetaData = new DIDLParser().parse(trackMetaDataXML);
        assertEquals(trackMetaData.getContainers().size(), 0);
        assertEquals(trackMetaData.getItems().size(), 1);
    }

    @Test
    public void getInitialEventAVTransport() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        final List<Boolean> testAssertions = new ArrayList<>();

        LocalDevice device = MediaRendererSampleData.createDevice();
        upnpService.getRegistry().addDevice(device);

        LocalService service = device.getServices()[0];

        SubscriptionCallback callback = new SubscriptionCallback(service, 600) {

            @Override
            public void established(GENASubscription sub) {
            }

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                throw new RuntimeException(defaultMsg, exception);
            }

            @Override
            public void ended(GENASubscription sub,
                              CancelReason reason,
                              UpnpResponse response) {
            }

            public void eventReceived(GENASubscription sub) {

                Map<String, StateVariableValue> values = sub.getCurrentValues();
                String lastChangeString = values.get("LastChange").toString();

                try {
                    LastChange lastChange = new LastChange(
                            new AVTransportLastChangeParser(),
                            lastChangeString
                    );
                    assertEquals(lastChange.getInstanceIDs().length, 1);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.AVTransportURI.class).getValue(), null);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.AVTransportURIMetaData.class).getValue(), null);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentMediaDuration.class).getValue(), "00:00:00");
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentPlayMode.class).getValue(), PlayMode.NORMAL);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentRecordQualityMode.class).getValue(), RecordQualityMode.NOT_IMPLEMENTED);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentTrack.class).getValue(), new UnsignedIntegerFourBytes(0));
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackDuration.class).getValue(), "00:00:00");
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackMetaData.class).getValue(), "NOT_IMPLEMENTED");
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackURI.class).getValue(), null);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.CurrentTransportActions.class).getValue(), new TransportAction[]{TransportAction.Stop});
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.NextAVTransportURI.class).getValue().toString(), "NOT_IMPLEMENTED"); // TODO: That's weird
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.NumberOfTracks.class).getValue(), new UnsignedIntegerFourBytes(0));
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.PossiblePlaybackStorageMedia.class).getValue(), new StorageMedium[]{StorageMedium.NETWORK});
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.PossibleRecordQualityModes.class).getValue(), new RecordQualityMode[]{RecordQualityMode.NOT_IMPLEMENTED});
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.PossibleRecordStorageMedia.class).getValue(), new StorageMedium[]{StorageMedium.NOT_IMPLEMENTED});
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.RecordMediumWriteStatus.class).getValue(), RecordMediumWriteStatus.NOT_IMPLEMENTED);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.RecordStorageMedium.class).getValue(), StorageMedium.NOT_IMPLEMENTED);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.TransportPlaySpeed.class).getValue(), "1");
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue(), TransportState.NO_MEDIA_PRESENT);
                    assertEquals(lastChange.getEventedValue(0, AVTransportVariable.TransportStatus.class).getValue(), TransportStatus.OK);

                    testAssertions.add(true);

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
            }

        };

        upnpService.getControlPoint().execute(callback);

        assertEquals(testAssertions.size(), 1);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }
    }

    @Test
    public void getInitialEventRenderingControl() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        final List<Boolean> testAssertions = new ArrayList<>();

        LocalDevice device = MediaRendererSampleData.createDevice();
        upnpService.getRegistry().addDevice(device);

        LocalService service = device.getServices()[1];

        SubscriptionCallback callback = new SubscriptionCallback(service, 600) {

            @Override
            public void established(GENASubscription sub) {
            }

            @Override
            protected void failed(GENASubscription subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                throw new RuntimeException(defaultMsg, exception);
            }

            @Override
            public void ended(GENASubscription sub,
                              CancelReason reason,
                              UpnpResponse response) {
            }

            public void eventReceived(GENASubscription sub) {

                Map<String, StateVariableValue> values = sub.getCurrentValues();
                String lastChangeString = values.get("LastChange").toString();

                try {
                    LastChange lastChange = new LastChange(
                            new RenderingControlLastChangeParser(),
                            lastChangeString
                    );
                    assertEquals(lastChange.getInstanceIDs().length, 1);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Mute.class).getValue().getChannel(), Channel.Master);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Mute.class).getValue().getMute(), Boolean.FALSE);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Loudness.class).getValue().getChannel(), Channel.Master);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Loudness.class).getValue().getLoudness(), Boolean.FALSE);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getChannel(), Channel.Master);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.Volume.class).getValue().getVolume(), new Integer(50));
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.VolumeDB.class).getValue().getChannel(), Channel.Master);
                    assertEquals(lastChange.getEventedValue(0, RenderingControlVariable.VolumeDB.class).getValue().getVolumeDB(), new Integer(0));

                    testAssertions.add(true);

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
            }

        };

        upnpService.getControlPoint().execute(callback);

        assertEquals(testAssertions.size(), 1);
        for (Boolean testAssertion : testAssertions) {
            assert testAssertion;
        }
    }
}
