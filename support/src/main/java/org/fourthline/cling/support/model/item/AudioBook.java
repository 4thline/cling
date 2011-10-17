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
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.container.Container;

import java.util.List;

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;
import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class AudioBook extends AudioItem {

    public static final Class CLASS = new Class("object.item.audioItem.audioBook");

    public AudioBook() {
        setClazz(CLASS);
    }

    public AudioBook(Item other) {
        super(other);
    }

    public AudioBook(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, null, null, null, resource);
    }

    public AudioBook(String id, Container parent, String title, String creator, String producer, String contributor, String date, Res... resource) {
        this(id, parent.getId(), title, creator, new Person(producer), new Person(contributor), date, resource);
    }

    public AudioBook(String id, String parentID, String title, String creator, Person producer, Person contributor, String date, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
        if (producer != null)
            addProperty(new UPNP.PRODUCER(producer));
        if (contributor != null)
            addProperty(new DC.CONTRIBUTOR(contributor));
        if (date != null)
            setDate(date);
    }
    
    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public AudioBook setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(UPNP.PRODUCER.class);
    }

    public Person[] getProducers() {
        List<Person> list = getPropertyValues(UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }

    public AudioBook setProducers(Person[] persons) {
        removeProperties(UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new UPNP.PRODUCER(p));
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

    public AudioBook setContributors(Person[] contributors) {
        removeProperties(DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DC.DATE.class);
    }

    public AudioBook setDate(String date) {
        replaceFirstProperty(new DC.DATE(date));
        return this;
    }

}
