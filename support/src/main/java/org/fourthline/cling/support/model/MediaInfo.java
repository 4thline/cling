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

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.action.ActionArgumentValue;

import java.util.Map;

/**
 *
 */
public class MediaInfo {

    private String currentURI = "";
    private String currentURIMetaData = "";
    private String nextURI = "NOT_IMPLEMENTED";
    private String nextURIMetaData = "NOT_IMPLEMENTED";

    private UnsignedIntegerFourBytes numberOfTracks = new UnsignedIntegerFourBytes(0);
    private String mediaDuration = "00:00:00";
    private StorageMedium playMedium = StorageMedium.NONE;
    private StorageMedium recordMedium = StorageMedium.NOT_IMPLEMENTED;
    private RecordMediumWriteStatus writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;

    public MediaInfo() {
    }

    public MediaInfo(Map<String, ActionArgumentValue> args) {
        this(
                (String) args.get("CurrentURI").getValue(),
                (String) args.get("CurrentURIMetaData").getValue(),
                (String) args.get("NextURI").getValue(),
                (String) args.get("NextURIMetaData").getValue(),

                (UnsignedIntegerFourBytes) args.get("NrTracks").getValue(),
                (String) args.get("MediaDuration").getValue(),
                StorageMedium.valueOrVendorSpecificOf((String) args.get("PlayMedium").getValue()),
                StorageMedium.valueOrVendorSpecificOf((String) args.get("RecordMedium").getValue()),
                RecordMediumWriteStatus.valueOrUnknownOf((String) args.get("WriteStatus").getValue())
        );
    }

    public MediaInfo(String currentURI, String currentURIMetaData) {
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
    }

    public MediaInfo(String currentURI, String currentURIMetaData,
                     UnsignedIntegerFourBytes numberOfTracks, String mediaDuration,
                     StorageMedium playMedium) {
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }

    public MediaInfo(String currentURI, String currentURIMetaData,
                     UnsignedIntegerFourBytes numberOfTracks, String mediaDuration,
                     StorageMedium playMedium,
                     StorageMedium recordMedium, RecordMediumWriteStatus writeStatus) {
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }

    public MediaInfo(String currentURI, String currentURIMetaData,
                     String nextURI, String nextURIMetaData,
                     UnsignedIntegerFourBytes numberOfTracks, String mediaDuration,
                     StorageMedium playMedium) {
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }

    public MediaInfo(String currentURI, String currentURIMetaData,
                     String nextURI, String nextURIMetaData,
                     UnsignedIntegerFourBytes numberOfTracks, String mediaDuration,
                     StorageMedium playMedium,
                     StorageMedium recordMedium, RecordMediumWriteStatus writeStatus) {
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }

    public String getCurrentURI() {
        return currentURI;
    }

    public String getCurrentURIMetaData() {
        return currentURIMetaData;
    }

    public String getNextURI() {
        return nextURI;
    }

    public String getNextURIMetaData() {
        return nextURIMetaData;
    }

    public UnsignedIntegerFourBytes getNumberOfTracks() {
        return numberOfTracks;
    }

    public String getMediaDuration() {
        return mediaDuration;
    }

    public StorageMedium getPlayMedium() {
        return playMedium;
    }

    public StorageMedium getRecordMedium() {
        return recordMedium;
    }

    public RecordMediumWriteStatus getWriteStatus() {
        return writeStatus;
    }

}
