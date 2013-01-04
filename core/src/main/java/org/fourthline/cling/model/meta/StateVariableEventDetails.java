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

package org.fourthline.cling.model.meta;

/**
 * Eventing options of a state variable, including moderation settings.
 *
 * @author Christian Bauer
 */
public class StateVariableEventDetails {

    final private boolean sendEvents;
    final private int eventMaximumRateMilliseconds;
    final private int eventMinimumDelta;

    public StateVariableEventDetails() {
        this(true, 0, 0);
    }

    public StateVariableEventDetails(boolean sendEvents) {
        this(sendEvents, 0, 0);
    }

    public StateVariableEventDetails(boolean sendEvents, int eventMaximumRateMilliseconds, int eventMinimumDelta) {
        this.sendEvents = sendEvents;
        this.eventMaximumRateMilliseconds = eventMaximumRateMilliseconds;
        this.eventMinimumDelta = eventMinimumDelta;
    }

    public boolean isSendEvents() {
        return sendEvents;
    }

    public int getEventMaximumRateMilliseconds() {
        return eventMaximumRateMilliseconds;
    }

    public int getEventMinimumDelta() {
        return eventMinimumDelta;
    }

}
