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

import javax.swing.BorderFactory;

/**
 * @author Christian Bauer
 */
public class PausedPlay extends InstanceViewState {

    public PausedPlay(InstanceViewImpl view) {
        super(view);
    }

    public void onEntry() {
        new ViewUpdate() {
            protected void run(InstanceViewImpl view) {
                view.getPlayerPanel().setBorder(BorderFactory.createTitledBorder("PAUSED PLAY"));

                view.getPlayerPanel().setAllButtons(false);
                // TODO controller.togglePlayPauseAction();
                view.getPlayerPanel().getStopButton().setEnabled(true);
                view.getPlayerPanel().getPlayButton().setEnabled(true);
                view.getProgressPanel().getPositionSlider().setEnabled(true);
            }
        };
    }

    public void onExit() {
        new ViewUpdate() {
            protected void run(InstanceViewImpl view) {
                // TODO controller.togglePlayPauseAction();
                view.getProgressPanel().getPositionSlider().setEnabled(false);
            }
        };
    }
}
