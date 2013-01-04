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
