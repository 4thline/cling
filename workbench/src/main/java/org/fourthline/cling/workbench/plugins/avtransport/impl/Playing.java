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

import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;

import javax.swing.*;

/**
 * @author Christian Bauer
 */
public class Playing extends InstanceViewState {

    final protected PositionUpdater positionUpdater;

    public Playing(InstanceViewImpl view) {
        super(view);
        positionUpdater = new PositionUpdater(getView());
    }

    public void onEntry() {

        AVTransportControlPoint.LOGGER.info(
            "Entering Playing state, starting to poll PositionInfo in " +
            "background every " + positionUpdater.getSleepIntervalMillis() + "ms..."
        );
        synchronized (positionUpdater) {
            positionUpdater.breakLoop();
            positionUpdater.notifyAll();
            new Thread(positionUpdater).start();
        }

        new ViewUpdate() {
            protected void run(InstanceViewImpl view) {
                view.getPlayerPanel().setBorder(BorderFactory.createTitledBorder("PLAYING"));
                view.getPlayerPanel().setAllButtons(true);
                view.getPlayerPanel().togglePause();
                view.getProgressPanel().getPositionSlider().setEnabled(true);
            }
        };
    }

    public void onExit() {

        AVTransportControlPoint.LOGGER.info(
            "Exiting Playing state, stopping background PositionInfo polling..."
        );
        synchronized (positionUpdater) {
            positionUpdater.breakLoop();
            positionUpdater.notifyAll();
        }

        new ViewUpdate() {
            protected void run(InstanceViewImpl view) {
                view.getPlayerPanel().togglePause();
                view.getProgressPanel().getPositionSlider().setEnabled(false);
            }
        };
    }

    static public class PositionUpdater implements Runnable {

        private final InstanceViewImpl view;
        private volatile boolean stopped = false;

        protected PositionUpdater(InstanceViewImpl  view) {
            this.view = view;
        }

        public int getSleepIntervalMillis() {
            return 2000;
        }

        public void breakLoop() {
            AVTransportControlPoint.LOGGER.fine("Setting stopped status on thread");
            stopped = true;
        }

        public void run() {
            stopped = false;
            AVTransportControlPoint.LOGGER.fine(
                "Running position updater loop every milliseconds: " + getSleepIntervalMillis()
            );
            while (!stopped) {
                try {
                    // TODO: Well, we could do this once and then just increment the seconds instead of querying...
                    view.getPresenter().onUpdatePositionInfo(view.getInstanceId());
                    synchronized (this) {
                        this.wait(getSleepIntervalMillis());
                    }

                } catch (Exception ex) {
                    breakLoop();
                    AVTransportControlPoint.LOGGER.fine(
                        "Failed updating position info, polling stopped: " + ex
                    );
                }
            }
            AVTransportControlPoint.LOGGER.fine(
                "Stopped status on thread received, ending position updater loop"
            );
        }
    }

}
