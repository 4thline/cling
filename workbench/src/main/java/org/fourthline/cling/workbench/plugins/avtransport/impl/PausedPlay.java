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

                view.getPlayerPanel().getPreviousButton().setEnabled(true);
                view.getPlayerPanel().getNextButton().setEnabled(true);
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
