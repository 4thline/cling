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

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.*;
import org.gstreamer.ClockTime;
import org.gstreamer.State;
import org.seamless.http.HttpFetch;
import org.seamless.util.URIUtil;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class GstAVTransportService extends AbstractAVTransportService {

    final private static Logger log = Logger.getLogger(GstAVTransportService.class.getName());

    final private Map<UnsignedIntegerEightBytes, GstMediaPlayer> players;

    protected GstAVTransportService(LastChange lastChange, Map<UnsignedIntegerEightBytes, GstMediaPlayer> players) {
        super(lastChange);
        this.players = players;
    }

    protected Map<UnsignedIntegerEightBytes, GstMediaPlayer> getPlayers() {
        return players;
    }

    protected GstMediaPlayer getInstance(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        GstMediaPlayer player = getPlayers().get(instanceId);
        if (player == null) {
            throw new AVTransportException(AVTransportErrorCode.INVALID_INSTANCE_ID);
        }
        return player;
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerEightBytes instanceId,
                                  String currentURI,
                                  String currentURIMetaData) throws AVTransportException {
        URI uri;
        try {
            uri = new URI(currentURI);
        } catch (Exception ex) {
            throw new AVTransportException(
                    ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed"
            );
        }

        if (currentURI.startsWith("http:")) {
            try {
                HttpFetch.validate(URIUtil.toURL(uri));
            } catch (Exception ex) {
                throw new AVTransportException(
                        AVTransportErrorCode.RESOURCE_NOT_FOUND, ex.getMessage()
                );
            }
        } else if (!currentURI.startsWith("file:")) {
            throw new AVTransportException(
                    ErrorCode.INVALID_ARGS, "Only HTTP and file: resource identifiers are supported"
            );
        }

        // TODO: Check mime type of resource against supported types

        // TODO: DIDL fragment parsing and handling of currentURIMetaData

        getInstance(instanceId).setURI(uri);
    }

    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        return getInstance(instanceId).getCurrentMediaInfo();
    }

    @Override
    public TransportInfo getTransportInfo(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        return getInstance(instanceId).getCurrentTransportInfo();
    }

    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        return getInstance(instanceId).getCurrentPositionInfo();
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        getInstance(instanceId);
        return new DeviceCapabilities(new StorageMedium[]{StorageMedium.NETWORK});
    }

    @Override
    public TransportSettings getTransportSettings(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        getInstance(instanceId);
        return new TransportSettings(PlayMode.NORMAL);
    }

    @Override
    public void stop(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        getInstance(instanceId).stop();
    }

    @Override
    public void play(UnsignedIntegerEightBytes instanceId, String speed) throws AVTransportException {
        getInstance(instanceId).play();
    }

    @Override
    public void pause(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        getInstance(instanceId).pause();
    }

    @Override
    public void record(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        // Not implemented
        log.info("### TODO: Not implemented: Record");
    }

    @Override
    public void seek(UnsignedIntegerEightBytes instanceId, String unit, String target) throws AVTransportException {
        final GstMediaPlayer player = getInstance(instanceId);
        SeekMode seekMode;
        try {
            seekMode = SeekMode.valueOrExceptionOf(unit);

            if (!seekMode.equals(SeekMode.REL_TIME)) {
                throw new IllegalArgumentException();
            }

            final ClockTime ct = ClockTime.fromSeconds(ModelUtil.fromTimeString(target));
            if (player.getPipeline().getState().equals(State.PLAYING)) {
                player.pause();
                player.getPipeline().seek(ct);
                player.play();
            } else if (player.getPipeline().getState().equals(State.PAUSED)) {
                player.getPipeline().seek(ct);
            }

        } catch (IllegalArgumentException ex) {
            throw new AVTransportException(
                    AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + unit
            );
        }
    }

    @Override
    public void next(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        // Not implemented
        log.info("### TODO: Not implemented: Next");
    }

    @Override
    public void previous(UnsignedIntegerEightBytes instanceId) throws AVTransportException {
        // Not implemented
        log.info("### TODO: Not implemented: Previous");
    }

    @Override
    public void setNextAVTransportURI(UnsignedIntegerEightBytes instanceId,
                                      String nextURI,
                                      String nextURIMetaData) throws AVTransportException {
        log.info("### TODO: Not implemented: SetNextAVTransportURI");
        // Not implemented
    }

    @Override
    public void setPlayMode(UnsignedIntegerEightBytes instanceId, String newPlayMode) throws AVTransportException {
        // Not implemented
        log.info("### TODO: Not implemented: SetPlayMode");
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerEightBytes instanceId, String newRecordQualityMode) throws AVTransportException {
        // Not implemented
        log.info("### TODO: Not implemented: SetRecordQualityMode");
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerEightBytes instanceId) throws Exception {
        return getInstance(instanceId).getCurrentTransportActions();
    }

    @Override
    public UnsignedIntegerEightBytes[] getCurrentInstanceIds() {
        UnsignedIntegerEightBytes[] ids = new UnsignedIntegerEightBytes[getPlayers().size()];
        int i = 0;
        for (UnsignedIntegerEightBytes id : getPlayers().keySet()) {
            ids[i] = id;
            i++;
        }
        return ids;
    }
}
