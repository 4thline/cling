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

package org.fourthline.cling.workbench.plugins.avtransport;

import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.shared.View;

/**
 * @author Christian Bauer
 */
public interface InstanceView extends View<InstanceView.Presenter> {

    public interface Presenter {

        void onSetAVTransportURISelected(int instanceId, final String uri);

        void onPauseSelected(int instanceId);

        void onPlaySelected(int instanceId);

        void onStopSelected(int instanceId);

        void onSeekSelected(int instanceId, String target);

        void onSeekSelected(int instanceId, int deltaSeconds, boolean forwards);

        void onPreviousSelected(int instanceId);

        void onNextSelected(int instanceId);

        void onUpdatePositionInfo(int instanceId);

        void onSetPlayModeSelected(int instanceId, PlayMode playMode);
    }

    void init(int instanceId);

    void setState(TransportState state);

    void setPlayMode(PlayMode playMode);

    PositionInfo getProgress();

    void setProgress(PositionInfo positionInfo);

    void setCurrentTrackURI(String uri);

    void setSelectionEnabled(boolean enabled);

    void dispose();

}
