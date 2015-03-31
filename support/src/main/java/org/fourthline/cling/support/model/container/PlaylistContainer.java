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

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.StorageMedium;

import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;
import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class PlaylistContainer extends Container {

    public static final Class CLASS = new Class("object.container.playlistContainer");

    public PlaylistContainer() {
        setClazz(CLASS);
    }

    public PlaylistContainer(Container other) {
        super(other);
    }

    public PlaylistContainer(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public PlaylistContainer(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, CLASS, childCount);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(UPNP.ARTIST.class);
    }

    public PersonWithRole[] getArtists() {
        List<PersonWithRole> list = getPropertyValues(UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }

    public PlaylistContainer setArtists(PersonWithRole[] artists) {
        removeProperties(UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new UPNP.ARTIST(artist));
        }
        return this;
    }

    public String getFirstGenre() {
        return getFirstPropertyValue(UPNP.GENRE.class);
    }

    public String[] getGenres() {
        List<String> list = getPropertyValues(UPNP.GENRE.class);
        return list.toArray(new String[list.size()]);
    }

    public PlaylistContainer setGenres(String[] genres) {
        removeProperties(UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new UPNP.GENRE(genre));
        }
        return this;
    }

    public String getDescription() {
        return getFirstPropertyValue(DC.DESCRIPTION.class);
    }

    public PlaylistContainer setDescription(String description) {
        replaceFirstProperty(new DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(UPNP.LONG_DESCRIPTION.class);
    }

    public PlaylistContainer setLongDescription(String description) {
        replaceFirstProperty(new UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(UPNP.PRODUCER.class);
    }

    public Person[] getProducers() {
        List<Person> list = getPropertyValues(UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }

    public PlaylistContainer setProducers(Person[] persons) {
        removeProperties(UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new UPNP.PRODUCER(p));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public PlaylistContainer setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DC.DATE.class);
    }

    public PlaylistContainer setDate(String date) {
        replaceFirstProperty(new DC.DATE(date));
        return this;
    }

    public String getFirstRights() {
        return getFirstPropertyValue(DC.RIGHTS.class);
    }

    public String[] getRights() {
        List<String> list = getPropertyValues(DC.RIGHTS.class);
        return list.toArray(new String[list.size()]);
    }

    public PlaylistContainer setRights(String[] rights) {
        removeProperties(DC.RIGHTS.class);
        for (String right : rights) {
            addProperty(new DC.RIGHTS(right));
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

    public PlaylistContainer setContributors(Person[] contributors) {
        removeProperties(DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getLanguage() {
        return getFirstPropertyValue(DC.LANGUAGE.class);
    }

    public PlaylistContainer setLanguage(String language) {
        replaceFirstProperty(new DC.LANGUAGE(language));
        return this;
    }

}
