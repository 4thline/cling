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

import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class PhotoAlbum extends Album {

    public static final Class CLASS = new Class("object.container.album.photoAlbum");

    public PhotoAlbum() {
        setClazz(CLASS);
    }

    public PhotoAlbum(Container other) {
        super(other);
    }

    public PhotoAlbum(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<Photo>());
    }

    public PhotoAlbum(String id, Container parent, String title, String creator, Integer childCount, List<Photo> photos) {
        this(id, parent.getId(), title, creator, childCount, photos);
    }

    public PhotoAlbum(String id, String parentID, String title, String creator, Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<Photo>());
    }

    public PhotoAlbum(String id, String parentID, String title, String creator, Integer childCount, List<Photo> photos) {
        super(id, parentID, title, creator, childCount);
        setClazz(CLASS);
        addPhotos(photos);
    }

    public Photo[] getPhotos() {
        List<Photo> list = new ArrayList();
        for (Item item : getItems()) {
            if (item instanceof Photo) list.add((Photo)item);
        }
        return list.toArray(new Photo[list.size()]);
    }

    public void addPhotos(List<Photo> photos) {
        addPhotos(photos.toArray(new Photo[photos.size()]));
    }

    public void addPhotos(Photo[] photos) {
        if (photos != null) {
            for (Photo photo : photos) {
                photo.setAlbum(getTitle());
                addItem(photo);
            }
        }
    }

}
