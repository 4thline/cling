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
import org.fourthline.cling.support.lastchange.LastChange;

/**
 * State of one logical instance of the AV Transport service.
 *
 * @author Christian Bauer
 */
public class AVTransport {

    final protected UnsignedIntegerFourBytes instanceID;
    final protected LastChange lastChange;
    protected MediaInfo mediaInfo;
    protected TransportInfo transportInfo;
    protected PositionInfo positionInfo;
    protected DeviceCapabilities deviceCapabilities;
    protected TransportSettings transportSettings;

    public AVTransport(UnsignedIntegerFourBytes instanceID, LastChange lastChange, StorageMedium possiblePlayMedium) {
        this(instanceID, lastChange, new StorageMedium[]{possiblePlayMedium});
    }

    public AVTransport(UnsignedIntegerFourBytes instanceID, LastChange lastChange, StorageMedium[] possiblePlayMedia) {
        this.instanceID = instanceID;
        this.lastChange = lastChange;
        setDeviceCapabilities(new DeviceCapabilities(possiblePlayMedia));
        setMediaInfo(new MediaInfo());
        setTransportInfo(new TransportInfo());
        setPositionInfo(new PositionInfo());
        setTransportSettings(new TransportSettings());
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return instanceID;
    }

    public LastChange getLastChange() {
        return lastChange;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public TransportInfo getTransportInfo() {
        return transportInfo;
    }

    public void setTransportInfo(TransportInfo transportInfo) {
        this.transportInfo = transportInfo;
    }

    public PositionInfo getPositionInfo() {
        return positionInfo;
    }

    public void setPositionInfo(PositionInfo positionInfo) {
        this.positionInfo = positionInfo;
    }

    public DeviceCapabilities getDeviceCapabilities() {
        return deviceCapabilities;
    }

    public void setDeviceCapabilities(DeviceCapabilities deviceCapabilities) {
        this.deviceCapabilities = deviceCapabilities;
    }

    public TransportSettings getTransportSettings() {
        return transportSettings;
    }

    public void setTransportSettings(TransportSettings transportSettings) {
        this.transportSettings = transportSettings;
    }

}
