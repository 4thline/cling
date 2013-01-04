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

import org.seamless.util.MimeType;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class Res {

    protected URI importUri;
    protected ProtocolInfo protocolInfo;
    protected Long size;
    protected String duration;
    protected Long bitrate;
    protected Long sampleFrequency;
    protected Long bitsPerSample;
    protected Long nrAudioChannels;
    protected Long colorDepth;
    protected String protection;
    protected String resolution;

    protected String value;

    public Res() {
    }

    public Res(String httpGetMimeType, Long size, String duration, Long bitrate, String value) {
        this(new ProtocolInfo(Protocol.HTTP_GET, ProtocolInfo.WILDCARD, httpGetMimeType, ProtocolInfo.WILDCARD), size, duration, bitrate, value);
    }
    
    public Res(MimeType httpGetMimeType, Long size, String duration, Long bitrate, String value) {
        this(new ProtocolInfo(httpGetMimeType), size, duration, bitrate, value);
    }

    public Res(MimeType httpGetMimeType, Long size, String value) {
        this(new ProtocolInfo(httpGetMimeType), size, value);
    }

    public Res(ProtocolInfo protocolInfo, Long size, String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.value = value;
    }

    public Res(ProtocolInfo protocolInfo, Long size, String duration, Long bitrate, String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.value = value;
    }

    public Res(URI importUri, ProtocolInfo protocolInfo, Long size, String duration, Long bitrate, Long sampleFrequency, Long bitsPerSample, Long nrAudioChannels, Long colorDepth, String protection, String resolution, String value) {
        this.importUri = importUri;
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.sampleFrequency = sampleFrequency;
        this.bitsPerSample = bitsPerSample;
        this.nrAudioChannels = nrAudioChannels;
        this.colorDepth = colorDepth;
        this.protection = protection;
        this.resolution = resolution;
        this.value = value;
    }

    public URI getImportUri() {
        return importUri;
    }

    public void setImportUri(URI importUri) {
        this.importUri = importUri;
    }

    public ProtocolInfo getProtocolInfo() {
        return protocolInfo;
    }

    public void setProtocolInfo(ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public Long getSampleFrequency() {
        return sampleFrequency;
    }

    public void setSampleFrequency(Long sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    public Long getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(Long bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }

    public Long getNrAudioChannels() {
        return nrAudioChannels;
    }

    public void setNrAudioChannels(Long nrAudioChannels) {
        this.nrAudioChannels = nrAudioChannels;
    }

    public Long getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(Long colorDepth) {
        this.colorDepth = colorDepth;
    }

    public String getProtection() {
        return protection;
    }

    public void setProtection(String protection) {
        this.protection = protection;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setResolution(int x, int y) {
        this.resolution = x + "x" + y;
    }

    public int getResolutionX() {
        return getResolution() != null && getResolution().split("x").length == 2
                ? Integer.valueOf(getResolution().split("x")[0])
                : 0;
    }

    public int getResolutionY() {
        return getResolution() != null && getResolution().split("x").length == 2
                ? Integer.valueOf(getResolution().split("x")[1])
                : 0;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
