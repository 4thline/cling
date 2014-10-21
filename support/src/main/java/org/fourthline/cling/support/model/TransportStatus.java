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
public enum TransportStatus {

    OK,
    ERROR_OCCURRED,
    CUSTOM;

    String value;

    TransportStatus() {
        this.value = name();
    }

    public String getValue() {
        return value;
    }

    public TransportStatus setValue(String value) {
        this.value = value;
        return this;
    }

    public static TransportStatus valueOrCustomOf(String s) {
        try {
            return TransportStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return TransportStatus.CUSTOM.setValue(s);
        }
    }
}
