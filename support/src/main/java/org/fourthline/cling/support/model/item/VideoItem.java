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

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;
import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class VideoItem extends Item {

    public static final Class CLASS = new Class("object.item.videoItem");

    public VideoItem() {
        setClazz(CLASS);
    }

    public VideoItem(Item other) {
        super(other);
    }

    public VideoItem(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public VideoItem(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, CLASS);
        if (resource != null) {
            getResources().addAll(Arrays.asList(resource));
        }
    }

    public String getFirstGenre() {
        return getFirstPropertyValue(UPNP.GENRE.class);
    }

    public String[] getGenres() {
        List<String> list = getPropertyValues(UPNP.GENRE.class);
        return list.toArray(new String[list.size()]);
    }

    public VideoItem setGenres(String[] genres) {
        removeProperties(UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new UPNP.GENRE(genre));
        }
        return this;
    }

    public String getDescription() {
        return getFirstPropertyValue(DC.DESCRIPTION.class);
    }

    public VideoItem setDescription(String description) {
        replaceFirstProperty(new DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(UPNP.LONG_DESCRIPTION.class);
    }

    public VideoItem setLongDescription(String description) {
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

    public VideoItem setProducers(Person[] persons) {
        removeProperties(UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new UPNP.PRODUCER(p));
        }
        return this;
    }

    public String getRating() {
        return getFirstPropertyValue(UPNP.RATING.class);
    }

    public VideoItem setRating(String rating) {
        replaceFirstProperty(new UPNP.RATING(rating));
        return this;
    }

    public PersonWithRole getFirstActor() {
        return getFirstPropertyValue(UPNP.ACTOR.class);
    }

    public PersonWithRole[] getActors() {
        List<PersonWithRole> list = getPropertyValues(UPNP.ACTOR.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }

    public VideoItem setActors(PersonWithRole[] persons) {
        removeProperties(UPNP.ACTOR.class);
        for (PersonWithRole p : persons) {
            addProperty(new UPNP.ACTOR(p));
        }
        return this;
    }

    public Person getFirstDirector() {
        return getFirstPropertyValue(UPNP.DIRECTOR.class);
    }

    public Person[] getDirectors() {
        List<Person> list = getPropertyValues(UPNP.DIRECTOR.class);
        return list.toArray(new Person[list.size()]);
    }

    public VideoItem setDirectors(Person[] persons) {
        removeProperties(UPNP.DIRECTOR.class);
        for (Person p : persons) {
            addProperty(new UPNP.DIRECTOR(p));
        }
        return this;
    }

    public Person getFirstPublisher() {
        return getFirstPropertyValue(DC.PUBLISHER.class);
    }

    public Person[] getPublishers() {
        List<Person> list = getPropertyValues(DC.PUBLISHER.class);
        return list.toArray(new Person[list.size()]);
    }

    public VideoItem setPublishers(Person[] publishers) {
        removeProperties(DC.PUBLISHER.class);
        for (Person publisher : publishers) {
            addProperty(new DC.PUBLISHER(publisher));
        }
        return this;
    }

    public String getLanguage() {
        return getFirstPropertyValue(DC.LANGUAGE.class);
    }

    public VideoItem setLanguage(String language) {
        replaceFirstProperty(new DC.LANGUAGE(language));
        return this;
    }

    public URI getFirstRelation() {
        return getFirstPropertyValue(DC.RELATION.class);
    }

    public URI[] getRelations() {
        List<URI> list = getPropertyValues(DC.RELATION.class);
        return list.toArray(new URI[list.size()]);
    }

    public VideoItem setRelations(URI[] relations) {
        removeProperties(DC.RELATION.class);
        for (URI relation : relations) {
            addProperty(new DC.RELATION(relation));
        }
        return this;
    }

}
