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

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.StorageMedium;

import java.net.URI;
import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;
import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class Album extends Container {

    public static final Class CLASS = new Class("object.container.album");

    public Album() {
        setClazz(CLASS);
    }

    public Album(Container other) {
        super(other);
    }

    public Album(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public Album(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, CLASS, childCount);
    }

    public String getDescription() {
        return getFirstPropertyValue(DC.DESCRIPTION.class);
    }

    public Album setDescription(String description) {
        replaceFirstProperty(new DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(UPNP.LONG_DESCRIPTION.class);
    }

    public Album setLongDescription(String description) {
        replaceFirstProperty(new UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public Album setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DC.DATE.class);
    }

    public Album setDate(String date) {
        replaceFirstProperty(new DC.DATE(date));
        return this;
    }

    public URI getFirstRelation() {
        return getFirstPropertyValue(DC.RELATION.class);
    }

    public URI[] getRelations() {
        List<URI> list = getPropertyValues(DC.RELATION.class);
        return list.toArray(new URI[list.size()]);
    }

    public Album setRelations(URI[] relations) {
        removeProperties(DC.RELATION.class);
        for (URI relation : relations) {
            addProperty(new DC.RELATION(relation));
        }
        return this;
    }

    public String getFirstRights() {
        return getFirstPropertyValue(DC.RIGHTS.class);
    }

    public String[] getRights() {
        List<String> list = getPropertyValues(DC.RIGHTS.class);
        return list.toArray(new String[list.size()]);
    }

    public Album setRights(String[] rights) {
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

    public Album setContributors(Person[] contributors) {
        removeProperties(DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DC.CONTRIBUTOR(p));
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

    public Album setPublishers(Person[] publishers) {
        removeProperties(DC.PUBLISHER.class);
        for (Person publisher : publishers) {
            addProperty(new DC.PUBLISHER(publisher));
        }
        return this;
    }

}
