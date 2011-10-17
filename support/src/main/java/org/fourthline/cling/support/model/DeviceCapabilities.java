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
