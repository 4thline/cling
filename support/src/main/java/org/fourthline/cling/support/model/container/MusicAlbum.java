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
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class MusicAlbum extends Album {

    public static final Class CLASS = new Class("object.container.album.musicAlbum");

    public MusicAlbum() {
        setClazz(CLASS);
    }

    public MusicAlbum(Container other) {
        super(other);
    }

    public MusicAlbum(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<MusicTrack>());
    }

    public MusicAlbum(String id, Container parent, String title, String creator, Integer childCount, List<MusicTrack> musicTracks) {
        this(id, parent.getId(), title, creator, childCount, musicTracks);
    }

    public MusicAlbum(String id, String parentID, String title, String creator, Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<MusicTrack>());
    }

    public MusicAlbum(String id, String parentID, String title, String creator, Integer childCount, List<MusicTrack> musicTracks) {
        super(id, parentID, title, creator, childCount);
        setClazz(CLASS);
        addMusicTracks(musicTracks);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(UPNP.ARTIST.class);
    }

    public PersonWithRole[] getArtists() {
        List<PersonWithRole> list = getPropertyValues(UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }

    public MusicAlbum setArtists(PersonWithRole[] artists) {
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

    public MusicAlbum setGenres(String[] genres) {
        removeProperties(UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new UPNP.GENRE(genre));
        }
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(UPNP.PRODUCER.class);
    }

    public Person[] getProducers() {
        List<Person> list = getPropertyValues(UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }

    public MusicAlbum setProducers(Person[] persons) {
        removeProperties(UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new UPNP.PRODUCER(p));
        }
        return this;
    }

    public URI getFirstAlbumArtURI() {
        return getFirstPropertyValue(UPNP.ALBUM_ART_URI.class);
    }

    public URI[] getAlbumArtURIs() {
        List<URI> list = getPropertyValues(UPNP.ALBUM_ART_URI.class);
        return list.toArray(new URI[list.size()]);
    }

    public MusicAlbum setAlbumArtURIs(URI[] uris) {
        removeProperties(UPNP.ALBUM_ART_URI.class);
        for (URI uri : uris) {
            addProperty(new UPNP.ALBUM_ART_URI(uri));
        }
        return this;
    }

    public String getToc() {
        return getFirstPropertyValue(UPNP.TOC.class);
    }

    public MusicAlbum setToc(String toc) {
        replaceFirstProperty(new UPNP.TOC(toc));
        return this;
    }

    public MusicTrack[] getMusicTracks() {
        List<MusicTrack> list = new ArrayList<>();
        for (Item item : getItems()) {
            if (item instanceof MusicTrack) list.add((MusicTrack)item);
        }
        return list.toArray(new MusicTrack[list.size()]);
    }

    public void addMusicTracks(List<MusicTrack> musicTracks) {
        addMusicTracks(musicTracks.toArray(new MusicTrack[musicTracks.size()]));
    }

    public void addMusicTracks(MusicTrack[] musicTracks) {
        if (musicTracks != null) {
            for (MusicTrack musicTrack : musicTracks) {
                musicTrack.setAlbum(getTitle());
                addItem(musicTrack);
            }
        }
    }

}
