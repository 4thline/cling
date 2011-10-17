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

import org.fourthline.cling.support.model.StorageMedium;

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class StorageVolume extends Container {
    
    public static final Class CLASS = new Class("object.container.storageVolume");

    public StorageVolume() {
        setClazz(CLASS);
    }

    public StorageVolume(Container other) {
        super(other);
    }

    public StorageVolume(String id, Container parent, String title, String creator, Integer childCount,
                         Long storageTotal, Long storageUsed, Long storageFree, StorageMedium storageMedium) {
        this(id, parent.getId(), title, creator, childCount, storageTotal, storageUsed, storageFree, storageMedium);
    }

    public StorageVolume(String id, String parentID, String title, String creator, Integer childCount,
                         Long storageTotal, Long storageUsed, Long storageFree, StorageMedium storageMedium) {
        super(id, parentID, title, creator, CLASS, childCount);
        if (storageTotal != null)
            setStorageTotal(storageTotal);
        if (storageUsed!= null)
            setStorageUsed(storageUsed);
        if (storageFree != null)
            setStorageFree(storageFree);
        if (storageMedium != null)
            setStorageMedium(storageMedium);
    }

    public Long getStorageTotal() {
        return getFirstPropertyValue(UPNP.STORAGE_TOTAL.class);
    }

    public StorageVolume setStorageTotal(Long l) {
        replaceFirstProperty(new UPNP.STORAGE_TOTAL(l));
        return this;
    }

    public Long getStorageUsed() {
        return getFirstPropertyValue(UPNP.STORAGE_USED.class);
    }

    public StorageVolume setStorageUsed(Long l) {
        replaceFirstProperty(new UPNP.STORAGE_USED(l));
        return this;
    }

    public Long getStorageFree() {
        return getFirstPropertyValue(UPNP.STORAGE_FREE.class);
    }

    public StorageVolume setStorageFree(Long l) {
        replaceFirstProperty(new UPNP.STORAGE_FREE(l));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(UPNP.STORAGE_MEDIUM.class);
    }

    public StorageVolume setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
}
