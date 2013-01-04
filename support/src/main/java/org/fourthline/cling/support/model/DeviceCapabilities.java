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
import org.fourthline.cling.model.action.ActionArgumentValue;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class DeviceCapabilities {

    private StorageMedium[] playMedia;
    private StorageMedium[] recMedia = new StorageMedium[] {StorageMedium.NOT_IMPLEMENTED};
    private RecordQualityMode[] recQualityModes = new RecordQualityMode[] {RecordQualityMode.NOT_IMPLEMENTED};

    public DeviceCapabilities(Map<String, ActionArgumentValue> args) {
        this(
                StorageMedium.valueOfCommaSeparatedList((String) args.get("PlayMedia").getValue()),
                StorageMedium.valueOfCommaSeparatedList((String) args.get("RecMedia").getValue()),
                RecordQualityMode.valueOfCommaSeparatedList((String) args.get("RecQualityModes").getValue())
        );
    }

    public DeviceCapabilities(StorageMedium[] playMedia) {
        this.playMedia = playMedia;
    }

    public DeviceCapabilities(StorageMedium[] playMedia, StorageMedium[] recMedia, RecordQualityMode[] recQualityModes) {
        this.playMedia = playMedia;
        this.recMedia = recMedia;
        this.recQualityModes = recQualityModes;
    }

    public StorageMedium[] getPlayMedia() {
        return playMedia;
    }

    public StorageMedium[] getRecMedia() {
        return recMedia;
    }

    public RecordQualityMode[] getRecQualityModes() {
        return recQualityModes;
    }

    public String getPlayMediaString() {
        return ModelUtil.toCommaSeparatedList(playMedia);
    }

    public String getRecMediaString() {
        return ModelUtil.toCommaSeparatedList(recMedia);
    }

    public String getRecQualityModesString() {
        return ModelUtil.toCommaSeparatedList(recQualityModes);
    }
}
