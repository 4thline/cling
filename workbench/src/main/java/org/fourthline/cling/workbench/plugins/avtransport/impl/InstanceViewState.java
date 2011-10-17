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
