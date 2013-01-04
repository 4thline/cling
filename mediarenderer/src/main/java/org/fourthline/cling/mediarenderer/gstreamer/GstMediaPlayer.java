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

package org.fourthline.cling.mediarenderer.gstreamer;

import org.gstreamer.Bus;
import org.gstreamer.Caps;
import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.State;
import org.gstreamer.Structure;
import org.gstreamer.TagList;
import org.gstreamer.media.PlayBinMediaPlayer;
import org.gstreamer.media.event.DurationChangedEvent;
import org.gstreamer.media.event.EndOfMediaEvent;
import org.gstreamer.media.event.MediaListener;
import org.gstreamer.media.event.PauseEvent;
import org.gstreamer.media.event.PositionChangedEvent;
import org.gstreamer.media.event.StartEvent;
import org.gstreamer.media.event.StopEvent;
import org.gstreamer.swing.VideoComponent;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.net.URI;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class GstMediaPlayer extends PlayBinMediaPlayer {

    final private static Logger log = Logger.getLogger(GstMediaPlayer.class.getName());

    final private UnsignedIntegerFourBytes instanceId;
    final private LastChange avTransportLastChange;
    final private LastChange renderingControlLastChange;

    final private VideoComponent videoComponent = new VideoComponent();

    // We'll synchronize read/writes to these fields
    private volatile TransportInfo currentTransportInfo = new TransportInfo();
    private PositionInfo currentPositionInfo = new PositionInfo();
    private MediaInfo currentMediaInfo = new MediaInfo();
    private double storedVolume;

    public GstMediaPlayer(UnsignedIntegerFourBytes instanceId,
                          LastChange avTransportLastChange,
                          LastChange renderingControlLastChange) {
        super();
        this.instanceId = instanceId;
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;

        try {
            // Disconnect the old bus listener
            /* TODO: That doesn't work for some reason...
            getPipeline().getBus().disconnect(
                    (Bus.STATE_CHANGED) Reflections.getField(getClass(), "stateChanged").get(this)
            );
            */

            // Connect a fixed bus state listener
            getPipeline().getBus().connect(busStateChanged);

            // Connect a bus tag listener
            getPipeline().getBus().connect(busTag);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        addMediaListener(new GstMediaListener());

        setVideoSink(videoComponent.getElement());
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return instanceId;
    }

    public LastChange getAvTransportLastChange() {
        return avTransportLastChange;
    }

    public LastChange getRenderingControlLastChange() {
        return renderingControlLastChange;
    }

    public VideoComponent getVideoComponent() {
        return videoComponent;
    }

    // TODO: gstreamer-java has a broken implementation of getStreamInfo(), so we need to
    // do our best fishing for the stream type inside the playbin pipeline

    synchronized public boolean isDecodingStreamType(String prefix) {
        for (Element element : getPipeline().getElements()) {
            if (element.getName().matches("decodebin[0-9]+")) {
                for (Pad pad : element.getPads()) {
                    if (pad.getName().matches("src[0-9]+")) {
                        Caps caps = pad.getNegotiatedCaps();
                        Structure struct = caps.getStructure(0);
                        if (struct.getName().startsWith(prefix + "/"))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    synchronized public TransportInfo getCurrentTransportInfo() {
        return currentTransportInfo;
    }

    synchronized public PositionInfo getCurrentPositionInfo() {
        return currentPositionInfo;
    }

    synchronized public MediaInfo getCurrentMediaInfo() {
        return currentMediaInfo;
    }

    @Override
    synchronized public void setURI(URI uri) {
        stop();
        super.setURI(uri);
        currentMediaInfo = new MediaInfo(uri.toString(), "");
        currentPositionInfo = new PositionInfo(1, "", uri.toString());

        getAvTransportLastChange().setEventedValue(
                getInstanceId(),
                new AVTransportVariable.AVTransportURI(uri),
                new AVTransportVariable.CurrentTrackURI(uri)
        );

        transportStateChanged(TransportState.STOPPED);
    }

    @Override
    synchronized public void setVolume(double volume) {
        storedVolume = getVolume();
        super.setVolume(volume);

        ChannelMute switchedMute =
                (storedVolume == 0 && volume > 0) || (storedVolume > 0 && volume == 0)
                        ? new ChannelMute(Channel.Master, storedVolume > 0 && volume == 0)
                        : null;

        getRenderingControlLastChange().setEventedValue(
                getInstanceId(),
                new RenderingControlVariable.Volume(
                        new ChannelVolume(Channel.Master, (int) (volume * 100))
                ),
                switchedMute != null
                        ? new RenderingControlVariable.Mute(switchedMute)
                        : null
        );
    }

    synchronized public void setMute(boolean desiredMute) {
        if (desiredMute && getVolume() > 0) {
            log.fine("Switching mute ON");
            setVolume(0);
        } else if (!desiredMute && getVolume() == 0) {
            log.fine("Switching mute OFF, restoring: " + storedVolume);
            setVolume(storedVolume);
        }
    }

    // Because we don't have an automated state machine, we need to calculate the possible transitions here

    synchronized public TransportAction[] getCurrentTransportActions() {
        TransportState state = currentTransportInfo.getCurrentTransportState();
        TransportAction[] actions;

        switch (state) {
            case STOPPED:
                actions = new TransportAction[]{
                        TransportAction.Play
                };
                break;
            case PLAYING:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek
                };
                break;
            case PAUSED_PLAYBACK:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek,
                        TransportAction.Play
                };
                break;
            default:
                actions = null;
        }
        return actions;
    }

    // Can't disconnect the broken bus listener, these funny methods disable it

    protected void fireStartEvent(StartEvent ev) {
    }

    protected void fireStartEventFix(StartEvent ev) {
        for (MediaListener l : getMediaListeners()) {
            l.start(ev);
        }
    }

    protected void fireStopEvent(StopEvent ev) {

    }

    protected void fireStopEventFix(StopEvent ev) {
        for (MediaListener l : getMediaListeners()) {
            l.stop(ev);
        }
    }

    protected void firePauseEvent(PauseEvent ev) {

    }

    protected void firePauseEventFix(PauseEvent ev) {
        for (MediaListener l : getMediaListeners()) {
            l.pause(ev);
        }
    }

    // TODO: The gstreamer-java folks don't seem to understand their code very well, nobody knew what
    // I was talking about when I mentioned "transitioning" as a new callback for the listener... so yes,
    // this hack is still necessary.
    private final Bus.STATE_CHANGED busStateChanged = new Bus.STATE_CHANGED() {
        public void stateChanged(GstObject source, State old, State newState, State pending) {
            if (!source.equals(getPipeline())) return;
            log.fine("GST pipeline changed state from " + old + " to " + newState + ", pending: " + pending);
            final ClockTime position = getPipeline().queryPosition();
            if (newState.equals(State.PLAYING) && old.equals(State.PAUSED)) {
                fireStartEventFix(new StartEvent(GstMediaPlayer.this, old, newState, State.VOID_PENDING, position));
            } else if (newState.equals(State.PAUSED) && pending.equals(State.VOID_PENDING)) {
                firePauseEventFix(new PauseEvent(GstMediaPlayer.this, old, newState, State.VOID_PENDING, position));
            } else if (newState.equals(State.READY) && pending.equals(State.NULL)) {
                fireStopEventFix(new StopEvent(GstMediaPlayer.this, old, newState, pending, position));
            }

            // Anything else means we are transitioning!
            if (!pending.equals(State.VOID_PENDING) && !pending.equals(State.NULL))
                transportStateChanged(TransportState.TRANSITIONING);
        }
    };

    synchronized protected void transportStateChanged(TransportState newState) {
        TransportState currentTransportState = currentTransportInfo.getCurrentTransportState();
        log.fine("Current state is: " + currentTransportState + ", changing to new state: " + newState);
        currentTransportInfo = new TransportInfo(newState);

        getAvTransportLastChange().setEventedValue(
                getInstanceId(),
                new AVTransportVariable.TransportState(newState),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    protected class GstMediaListener implements MediaListener {

        public void pause(StopEvent evt) {
            transportStateChanged(TransportState.PAUSED_PLAYBACK);
        }

        public void start(StartEvent evt) {
            transportStateChanged(TransportState.PLAYING);
        }

        public void stop(StopEvent evt) {
            transportStateChanged(TransportState.STOPPED);
        }

        public void endOfMedia(EndOfMediaEvent evt) {
            log.fine("End Of Media event received, stopping media player backend");
            GstMediaPlayer.this.stop();
        }

        public void positionChanged(PositionChangedEvent evt) {
            log.fine("Position Changed event received: " + evt.getPosition());
            synchronized (GstMediaPlayer.this) {
                currentPositionInfo =
                        new PositionInfo(
                                1,
                                currentMediaInfo.getMediaDuration(),
                                currentMediaInfo.getCurrentURI(),
                                ModelUtil.toTimeString(evt.getPosition().toSeconds()),
                                ModelUtil.toTimeString(evt.getPosition().toSeconds())
                        );
            }
        }

        public void durationChanged(final DurationChangedEvent evt) {
            log.fine("Duration Changed event received: " + evt.getDuration());
            synchronized (GstMediaPlayer.this) {
                String newValue = ModelUtil.toTimeString(evt.getDuration().toSeconds());
                currentMediaInfo =
                        new MediaInfo(
                                currentMediaInfo.getCurrentURI(),
                                "",
                                new UnsignedIntegerFourBytes(1),
                                newValue,
                                StorageMedium.NETWORK
                        );

                getAvTransportLastChange().setEventedValue(
                        getInstanceId(),
                        new AVTransportVariable.CurrentTrackDuration(newValue),
                        new AVTransportVariable.CurrentMediaDuration(newValue)
                );
            }
        }
    }

    private final Bus.TAG busTag = new Bus.TAG() {
        public void tagsFound(GstObject source, TagList tags) {
            for (String tagName : tags.getTagNames()) {
                for (Object o : tags.getValues(tagName)) {
                    log.info("Media tag: " + tagName + " => " + o);
                }
            }
        }
    };

}

