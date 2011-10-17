/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
