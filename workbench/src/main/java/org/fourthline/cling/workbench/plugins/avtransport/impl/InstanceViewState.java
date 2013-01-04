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

import org.fourthline.cling.support.model.TransportState;

import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public abstract class InstanceViewState {

    static final public Map<TransportState, Class<? extends InstanceViewState>> STATE_MAP =
            new HashMap<TransportState, Class<? extends InstanceViewState>>() {{
                put(TransportState.NO_MEDIA_PRESENT, NoMediaPresent.class);
                put(TransportState.STOPPED, Stopped.class);
                put(TransportState.PLAYING, Playing.class);
                put(TransportState.PAUSED_PLAYBACK, PausedPlay.class);
                put(TransportState.TRANSITIONING, Transitioning.class);
            }};

    private InstanceViewImpl view;

    public InstanceViewState(InstanceViewImpl view) {
        this.view = view;
    }

    public InstanceViewImpl getView() {
        return view;
    }

    public abstract void onEntry();

    public abstract void onExit();

    public abstract class ViewUpdate implements Runnable {
        protected ViewUpdate() {
            SwingUtilities.invokeLater(this);
        }

        public void run() {
            run(getView());
        }

        protected abstract void run(InstanceViewImpl view);
    }

}
