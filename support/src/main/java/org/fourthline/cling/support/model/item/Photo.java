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

import static org.fourthline.cling.support.model.DIDLObject.Property.UPNP;

/**
 * @author Christian Bauer
 */
public class Photo extends ImageItem {

    public static final Class CLASS = new Class("object.item.imageItem.photo");

    public Photo() {
        setClazz(CLASS);
    }

    public Photo(Item other) {
        super(other);
    }

    public Photo(String id, Container parent, String title, String creator, String album, Res... resource) {
        this(id, parent.getId(), title, creator, album, resource);
    }

    public Photo(String id, String parentID, String title, String creator, String album, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
        if (album != null)
            setAlbum(album);
    }

    public String getAlbum() {
        return getFirstPropertyValue(UPNP.ALBUM.class);
    }

    public Photo setAlbum(String album) {
        replaceFirstProperty(new UPNP.ALBUM(album));
        return this;
    }


}
