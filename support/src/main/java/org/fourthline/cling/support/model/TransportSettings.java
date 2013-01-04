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

package org.fourthline.cling.support.model;

/**
 *
 */
public class TransportSettings {

    private PlayMode playMode = PlayMode.NORMAL;
    private RecordQualityMode recQualityMode = RecordQualityMode.NOT_IMPLEMENTED;

    public TransportSettings() {
    }

    public TransportSettings(PlayMode playMode) {
        this.playMode = playMode;
    }

    public TransportSettings(PlayMode playMode, RecordQualityMode recQualityMode) {
        this.playMode = playMode;
        this.recQualityMode = recQualityMode;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public RecordQualityMode getRecQualityMode() {
        return recQualityMode;
    }
}
