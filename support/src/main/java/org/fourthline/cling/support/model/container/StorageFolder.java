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

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class StorageFolder extends Container {

    public static final Class CLASS = new Class("object.container.storageFolder");

    public StorageFolder() {
        setClazz(CLASS);
    }

    public StorageFolder(Container other) {
        super(other);
    }

    public StorageFolder(String id, Container parent, String title, String creator, Integer childCount,
                         Long storageUsed) {
        this(id, parent.getId(), title, creator, childCount, storageUsed);
    }

    public StorageFolder(String id, String parentID, String title, String creator, Integer childCount,
                         Long storageUsed) {
        super(id, parentID, title, creator, CLASS, childCount);
        if (storageUsed!= null)
            setStorageUsed(storageUsed);
    }

    public Long getStorageUsed() {
        return getFirstPropertyValue(UPNP.STORAGE_USED.class);
    }

    public StorageFolder setStorageUsed(Long l) {
        replaceFirstProperty(new UPNP.STORAGE_USED(l));
        return this;
    }


}
