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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPointAdapter;
import org.seamless.swing.Application;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

/**
 * @author Christian Bauer
 */
public class PlayerPanel extends JPanel {

    // Actions
    public static String[] ACTION_PLAY = {"Play", "avTransportPlay"};
    public static String[] ACTION_PAUSE = {"Pause", "avTransportPause"};
    public static String[] ACTION_STOP = {"Stop", "avTransportStop"};
    public static String[] ACTION_SKIP_FW = {"Skip +15s", "avTransportSkipFW"};
    public static String[] ACTION_SKIP_REW = {"Skip -15s", "avTransportSkipREW"};

    class PlayerButton extends JButton {
        PlayerButton(String text, String icon) {
            super(text, Application.createImageIcon(AVTransportControlPointAdapter.class, icon));
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setFocusable(false);
        }
    }

    final private PlayerButton rewButton = new PlayerButton(ACTION_SKIP_REW[0], "img/32/player_rew.png");
    final private PlayerButton pauseButton = new PlayerButton(ACTION_PAUSE[0], "img/32/player_pause.png");
    final private PlayerButton playButton = new PlayerButton(ACTION_PLAY[0], "img/32/player_play.png");
    final private PlayerButton stopButton= new PlayerButton(ACTION_STOP[0], "img/32/player_stop.png");
    final private PlayerButton fwdButton = new PlayerButton(ACTION_SKIP_FW[0], "img/32/player_fwd.png");

    public PlayerPanel() {
        super();

        setBorder(BorderFactory.createTitledBorder("READY"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalGlue());

        add(rewButton);
        add(stopButton);
        add(pauseButton);
        add(playButton);
        add(fwdButton);

        add(Box.createHorizontalGlue());

        pauseButton.setVisible(false);
    }

    public void setAllButtons(boolean enabled) {
        rewButton.setEnabled(enabled);
        pauseButton.setEnabled(enabled);
        playButton.setEnabled(enabled);
        stopButton.setEnabled(enabled);
        fwdButton.setEnabled(enabled);
    }

    public void togglePause() {
        playButton.setVisible(!playButton.isVisible());
        pauseButton.setVisible(!pauseButton.isVisible());
    }

    public JButton getRewButton() {
        return rewButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getFwdButton() {
        return fwdButton;
    }
}
