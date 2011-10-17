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

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class Movie extends VideoItem {

    public static final Class CLASS = new Class("object.item.videoItem.movie");

    public Movie() {
        setClazz(CLASS);
    }

    public Movie(Item other) {
        super(other);
    }

    public Movie(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public Movie(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public Movie setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public Integer getDVDRegionCode() {
        return getFirstPropertyValue(UPNP.DVD_REGION_CODE.class);
    }

    public Movie setDVDRegionCode(Integer DVDRegionCode) {
        replaceFirstProperty(new UPNP.DVD_REGION_CODE(DVDRegionCode));
        return this;
    }

    public String getChannelName() {
        return getFirstPropertyValue(UPNP.CHANNEL_NAME.class);
    }

    public Movie setChannelName(String channelName) {
        replaceFirstProperty(new UPNP.CHANNEL_NAME(channelName));
        return this;
    }

    public String getFirstScheduledStartTime() {
        return getFirstPropertyValue(UPNP.SCHEDULED_START_TIME.class);
    }

    public String[] getScheduledStartTimes() {
        List<String> list = getPropertyValues(UPNP.SCHEDULED_START_TIME.class);
        return list.toArray(new String[list.size()]);
    }

    public Movie setScheduledStartTimes(String[] strings) {
        removeProperties(UPNP.SCHEDULED_START_TIME.class);
        for (String s : strings) {
            addProperty(new UPNP.SCHEDULED_START_TIME(s));
        }
        return this;
    }

    public String getFirstScheduledEndTime() {
        return getFirstPropertyValue(UPNP.SCHEDULED_END_TIME.class);
    }

    public String[] getScheduledEndTimes() {
        List<String> list = getPropertyValues(UPNP.SCHEDULED_END_TIME.class);
        return list.toArray(new String[list.size()]);
    }

    public Movie setScheduledEndTimes(String[] strings) {
        removeProperties(UPNP.SCHEDULED_END_TIME.class);
        for (String s : strings) {
            addProperty(new UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }

}
