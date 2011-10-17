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

package org.fourthline.cling.mediarenderer.display;

import org.fourthline.cling.mediarenderer.MediaRenderer;
import org.fourthline.cling.mediarenderer.gstreamer.GstMediaPlayer;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class WindowedDisplayHandler implements DisplayHandler {

    final private static Logger log = Logger.getLogger(WindowedDisplayHandler.class.getName());

    final protected Map<UnsignedIntegerFourBytes, JFrame> playerWindows = new HashMap();

    public WindowedDisplayHandler() {
        MediaRenderer.APP.log(Level.INFO, "Enabling window handler for each player instance");
    }

    public void onPlay(final GstMediaPlayer player) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                boolean isPlayingVideo = player.isDecodingStreamType("video");
                boolean isPlayingAudio = player.isDecodingStreamType("audio");
                if (!isPlayingVideo && isPlayingAudio) {
                    MediaRenderer.APP.log(Level.INFO, "Playing audio only on instance: " + player.getInstanceId());
                    return;
                }

                if (!isPlayingVideo && !isPlayingAudio) {
                    MediaRenderer.APP.log(Level.INFO, "Playing unknown content on instance: " + player.getInstanceId());
                    return;
                }

                // Well, we are playing video, so we have to open a video window
                JFrame playerWindow;
                if ((playerWindow = playerWindows.get(player.getInstanceId())) == null) {
                    log.fine("No window exists, creating new window for player: " + player.getInstanceId());

                    String title = player.getInstanceId().toString() + ": " + player.getCurrentMediaInfo().getCurrentURI();
                    playerWindow = new JFrame(title);

                    playerWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent windowEvent) {
                            log.fine("Window closing, stopping backend player");
                            player.stop();
                        }
                    });

                    playerWindow.getContentPane().add(player.getVideoComponent());

                    playerWindow.setMinimumSize(new Dimension(320, 240));
                    playerWindow.pack();
                    playerWindow.setVisible(true);

                    playerWindows.put(player.getInstanceId(), playerWindow);

                } else {
                    log.finer("Window exists, setting it to front for player: " + player.getInstanceId());
                    playerWindow.toFront();
                }
            }
        });
    }

    public void onStop(final GstMediaPlayer player) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame playerWindow;
                if ((playerWindow = playerWindows.get(player.getInstanceId())) != null) {
                    log.fine("Window exists, closing it after player stopped: " + player.getInstanceId());
                    playerWindows.remove(player.getInstanceId());
                    playerWindow.dispose();
                }
            }
        });
    }
}
