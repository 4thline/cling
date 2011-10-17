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

/**
 * @author Christian Bauer
 */
public class MusicGenre extends GenreContainer {

    public static final Class CLASS = new Class("object.container.genre.musicGenre");

    public MusicGenre() {
        setClazz(CLASS);
    }

    public MusicGenre(Container other) {
        super(other);
    }

    public MusicGenre(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public MusicGenre(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, childCount);
        setClazz(CLASS);
    }

}
