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

package org.fourthline.cling.registry;

import org.fourthline.cling.model.ExpirationDetails;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
class RegistryItem<K, I> {

    private K key;
    private I item;
    private ExpirationDetails expirationDetails = new ExpirationDetails();

    RegistryItem(K key) {
        this.key = key;
    }

    RegistryItem(K key, I item, int maxAgeSeconds) {
        this.key = key;
        this.item = item;
        this.expirationDetails = new ExpirationDetails(maxAgeSeconds);
    }

    public K getKey() {
        return key;
    }

    public I getItem() {
        return item;
    }

    public ExpirationDetails getExpirationDetails() {
        return expirationDetails;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistryItem that = (RegistryItem) o;

        return key.equals(that.key);
    }

    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "("+getClass().getSimpleName()+") " + getExpirationDetails() + " KEY: " + getKey() + " ITEM: " + getItem();
    }
}
