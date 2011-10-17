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
import org.fourthline.cling.support.model.container.Container;

import java.net.URI;

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class VideoBroadcast extends VideoItem {

    public static final Class CLASS = new Class("object.item.videoItem.videoBroadcast");

    public VideoBroadcast() {
        setClazz(CLASS);
    }

    public VideoBroadcast(Item other) {
        super(other);
    }

    public VideoBroadcast(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public VideoBroadcast(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public URI getIcon() {
        return getFirstPropertyValue(UPNP.ICON.class);
    }

    public VideoBroadcast setIcon(URI icon) {
        replaceFirstProperty(new UPNP.ICON(icon));
        return this;
    }

    public String getRegion() {
        return getFirstPropertyValue(UPNP.REGION.class);
    }

    public VideoBroadcast setRegion(String region) {
        replaceFirstProperty(new UPNP.REGION(region));
        return this;
    }

    public Integer getChannelNr() {
        return getFirstPropertyValue(UPNP.CHANNEL_NR.class);
    }

    public VideoBroadcast setChannelNr(Integer channelNr) {
        replaceFirstProperty(new UPNP.CHANNEL_NR(channelNr));
        return this;
    }
}
