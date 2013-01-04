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

package org.fourthline.cling.mediarenderer.display;

import org.fourthline.cling.mediarenderer.MediaRenderer;
import org.fourthline.cling.mediarenderer.gstreamer.GstMediaPlayer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class FullscreenDisplayHandler implements DisplayHandler {

    final GraphicsDevice gfxDevice;
    final protected DisplayMode displayMode;
    final protected GridPanelFrame frame;

    public FullscreenDisplayHandler() {
        gfxDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        displayMode = gfxDevice.getDisplayMode();
        frame = new GridPanelFrame(displayMode.getWidth(), displayMode.getHeight());
        MediaRenderer.APP.log(
                Level.INFO,
                "Enabling fullscreen handler (press ESC to exit) with resolution: "
                + displayMode.getWidth() + "x" + displayMode.getHeight()
        );

        frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
                    MediaRenderer.APP.getMediaRenderer().stopAllMediaPlayers();
            }
            public void keyReleased(KeyEvent keyEvent) {
            }
            public void keyTyped(KeyEvent keyEvent) {
            }
        });
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
                
                if (frame.getContentPane().getComponentCount() == 0) {
                    gfxDevice.setFullScreenWindow(frame);
                }
                frame.addGridComponent(player.getVideoComponent());
            }
        });
    }

    public void onStop(final GstMediaPlayer player) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.removeGridComponent(player.getVideoComponent());
                if (frame.getContentPane().getComponentCount() == 0) {
                    frame.dispose();
                }
            }
        });
    }

    static protected class GridPanelFrame extends JFrame {

        GridPanelFrame(int width, int height) throws HeadlessException {
            setUndecorated(true);
            setResizable(false);
            setPreferredSize(new Dimension(width, height));
            getContentPane().setBackground(Color.BLACK);
            getContentPane().setLayout(new GridLayout(1,1));
        }

        public void addGridComponent(Component component) {
            GridLayout l = (GridLayout) getContentPane().getLayout();

            // Make more sections if necessary
            int availableSections = l.getColumns() * l.getRows();
            if (availableSections == getContentPane().getComponentCount()) {
                getContentPane().setLayout(new GridLayout(l.getColumns() + 1, l.getRows() + 1));
            }

            getContentPane().add(component);
            validate();
        }

        public void removeGridComponent(Component component) {
            GridLayout l = (GridLayout) getContentPane().getLayout();

            // Remove sections if necessary
            int threshold = (l.getColumns() - 1) * (l.getRows() - 1);
            if (threshold > 0 && threshold == getContentPane().getComponentCount() - 1) {
                getContentPane().setLayout(new GridLayout(l.getColumns() - 1, l.getRows() - 1));
            }

            getContentPane().remove(component);
            validate();
        }
    }
}
