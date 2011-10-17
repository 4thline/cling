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

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;
import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class MusicVideoClip extends VideoItem {

    public static final Class CLASS = new Class("object.item.videoItem.musicVideoClip");

    public MusicVideoClip() {
        setClazz(CLASS);
    }

    public MusicVideoClip(Item other) {
        super(other);
    }

    public MusicVideoClip(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public MusicVideoClip(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(UPNP.ARTIST.class);
    }

    public PersonWithRole[] getArtists() {
        List<PersonWithRole> list = getPropertyValues(UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }

    public MusicVideoClip setArtists(PersonWithRole[] artists) {
        removeProperties(UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new UPNP.ARTIST(artist));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public MusicVideoClip setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getAlbum() {
        return getFirstPropertyValue(UPNP.ALBUM.class);
    }

    public MusicVideoClip setAlbum(String album) {
        replaceFirstProperty(new UPNP.ALBUM(album));
        return this;
    }

    public String getFirstScheduledStartTime() {
        return getFirstPropertyValue(UPNP.SCHEDULED_START_TIME.class);
    }

    public String[] getScheduledStartTimes() {
        List<String> list = getPropertyValues(UPNP.SCHEDULED_START_TIME.class);
        return list.toArray(new String[list.size()]);
    }

    public MusicVideoClip setScheduledStartTimes(String[] strings) {
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

    public MusicVideoClip setScheduledEndTimes(String[] strings) {
        removeProperties(UPNP.SCHEDULED_END_TIME.class);
        for (String s : strings) {
            addProperty(new UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }

    public Person getFirstContributor() {
        return getFirstPropertyValue(DC.CONTRIBUTOR.class);
    }

    public Person[] getContributors() {
        List<Person> list = getPropertyValues(DC.CONTRIBUTOR.class);
        return list.toArray(new Person[list.size()]);
    }

    public MusicVideoClip setContributors(Person[] contributors) {
        removeProperties(DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DC.DATE.class);
    }

    public MusicVideoClip setDate(String date) {
        replaceFirstProperty(new DC.DATE(date));
        return this;
    }


}
