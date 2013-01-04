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
 * @author Christian Bauer
 */
public enum TransportState {

    STOPPED,
    PLAYING,
    TRANSITIONING,
    PAUSED_PLAYBACK,
    PAUSED_RECORDING,
    RECORDING,
    NO_MEDIA_PRESENT,
    CUSTOM;

    String value;

    TransportState() {
        this.value = name();
    }

    public String getValue() {
        return value;
    }

    public TransportState setValue(String value) {
        this.value = value;
        return this;
    }

    public static TransportState valueOrCustomOf(String s) {
        try {
            return TransportState.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return TransportState.CUSTOM.setValue(s);
        }
    }
}
