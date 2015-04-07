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
 * @author Christian Bauer
 */
public enum TransportAction {

    Play,
    Stop,
    Pause,
    Seek,
    Next,
    Previous,
    Record;

    public static TransportAction[] valueOfCommaSeparatedList(String s) {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) return new TransportAction[0];
        List<TransportAction> result = new ArrayList<>();
        for (String taString : strings) {
            for (TransportAction ta : values()) {
                if (ta.name().equals(taString)) {
                    result.add(ta);
                }
            }

        }
        return result.toArray(new TransportAction[result.size()]);
    }
}
