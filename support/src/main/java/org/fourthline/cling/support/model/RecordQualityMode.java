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

import org.fourthline.cling.model.ModelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum RecordQualityMode {

    EP("0:EP"),
    LP("1:LP"),
    SP("2:SP"),
    BASIC("0:BASIC"),
    MEDIUM("1:MEDIUM"),
    HIGH("2:HIGH"),
    NOT_IMPLEMENTED("NOT_IMPLEMENTED");

    private String protocolString;

    RecordQualityMode(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override
    public String toString() {
        return protocolString;
    }

    public static RecordQualityMode valueOrExceptionOf(String s) throws IllegalArgumentException {
        for (RecordQualityMode recordQualityMode : values()) {
            if (recordQualityMode.protocolString.equals(s)) {
                return recordQualityMode;
            }
        }
        throw new IllegalArgumentException("Invalid record quality mode string: " + s);
    }

    public static RecordQualityMode[] valueOfCommaSeparatedList(String s) {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) return new RecordQualityMode[0];
        List<RecordQualityMode> result = new ArrayList<>();
        for (String rqm : strings) {
            for (RecordQualityMode recordQualityMode : values()) {
                if (recordQualityMode.protocolString.equals(rqm)) {
                    result.add(recordQualityMode);
                }
            }
        }
        return result.toArray(new RecordQualityMode[result.size()]);
    }
}
