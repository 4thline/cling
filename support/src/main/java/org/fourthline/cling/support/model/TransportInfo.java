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

import org.fourthline.cling.model.action.ActionArgumentValue;

import java.util.Map;

/**
 *
 */
public class TransportInfo {

    private TransportState currentTransportState = TransportState.NO_MEDIA_PRESENT;
    private TransportStatus currentTransportStatus = TransportStatus.OK;
    private String currentSpeed = "1";

    public TransportInfo() {
    }

    public TransportInfo(Map<String, ActionArgumentValue> args) {
        this(
                TransportState.valueOrCustomOf((String) args.get("CurrentTransportState").getValue()),
                TransportStatus.valueOrCustomOf((String) args.get("CurrentTransportStatus").getValue()),
                (String) args.get("CurrentSpeed").getValue()
        );
    }

    public TransportInfo(TransportState currentTransportState) {
        this.currentTransportState = currentTransportState;
    }

    public TransportInfo(TransportState currentTransportState, String currentSpeed) {
        this.currentTransportState = currentTransportState;
        this.currentSpeed = currentSpeed;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus) {
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus, String currentSpeed) {
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
        this.currentSpeed = currentSpeed;
    }

    public TransportState getCurrentTransportState() {
        return currentTransportState;
    }

    public TransportStatus getCurrentTransportStatus() {
        return currentTransportStatus;
    }

    public String getCurrentSpeed() {
        return currentSpeed;
    }
}
