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

package org.fourthline.cling.support.avtransport;

/**
 *
 */
public enum AVTransportErrorCode {

    TRANSITION_NOT_AVAILABLE(701, "The immediate transition from current to desired state not supported"),
    NO_CONTENTS(702, "The media does not contain any contents that can be played"),
    READ_ERROR(703, "The media cannot be read"),
    PLAYBACK_FORMAT_NOT_SUPPORTED(704, "The storage format of the currently loaded media is not supported for playback"),
    TRANSPORT_LOCKED(705, "The transport is 'hold locked', e.g. with a keyboard lock"),
    WRITE_ERROR(706, "The media cannot be written"),
    MEDIA_PROTECTED(707, "The media is write-protected or is of a not writable type"),
    RECORD_FORMAT_NOT_SUPPORTED(708, "The storage format of the currently loaded media is not supported for recording"),
    MEDIA_FULL(709, "There is no free space left on the loaded media"),
    SEEKMODE_NOT_SUPPORTED(710, "The specified seek mode is not supported by the device"),
    ILLEGAL_SEEK_TARGET(711, "The specified seek target is not specified in terms of the seek mode, or is not present on the media"),
    PLAYMODE_NOT_SUPPORTED(712, "The specified play mode is not supported by the device"),
    RECORDQUALITYMODE_NOT_SUPPORTED(713, "The specified record quality mode is not supported by the device"),
    ILLEGAL_MIME_TYPE(714, "The specified resource has a MIME-type which is not supported"),
    CONTENT_BUSY(715, "The resource is already being played by other means"),
    RESOURCE_NOT_FOUND(716, "The specified resource cannot be found in the network"),
    INVALID_INSTANCE_ID(718, "The specified instanceID is invalid for this AVTransport");

    private int code;
    private String description;

    AVTransportErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AVTransportErrorCode getByCode(int code) {
        for (AVTransportErrorCode errorCode : values()) {
            if (errorCode.getCode() == code)
                return errorCode;
        }
        return null;
    }

}
