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

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.lastchange.LastChange;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class GstMediaPlayers extends ConcurrentHashMap<UnsignedIntegerFourBytes, GstMediaPlayer> {

    final private static Logger log = Logger.getLogger(GstMediaPlayers.class.getName());

    final protected LastChange avTransportLastChange;
    final protected LastChange renderingControlLastChange;

    public GstMediaPlayers(int numberOfPlayers,
                           LastChange avTransportLastChange,
                           LastChange renderingControlLastChange) {
        super(numberOfPlayers);
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;

        for (int i = 0; i < numberOfPlayers; i++) {

            GstMediaPlayer player =
                    new GstMediaPlayer(
                            new UnsignedIntegerFourBytes(i),
                            avTransportLastChange,
                            renderingControlLastChange
                    ) {
                        @Override
                        protected void transportStateChanged(TransportState newState) {
                            super.transportStateChanged(newState);
                            if (newState.equals(TransportState.PLAYING)) {
                                onPlay(this);
                            } else if (newState.equals(TransportState.STOPPED)) {
                                onStop(this);
                            }
                        }
                    };
            put(player.getInstanceId(), player);
        }
    }

    protected void onPlay(GstMediaPlayer player) {
        log.fine("Player is playing: " + player.getInstanceId());
    }

    protected void onStop(GstMediaPlayer player) {
        log.fine("Player is stopping: " + player.getInstanceId());
    }
}
