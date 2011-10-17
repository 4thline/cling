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

import static org.fourthline.cling.support.model.DIDLObject.Property.DC;

/**
 * @author Christian Bauer
 */
public class PersonContainer extends Container {

    public static final Class CLASS = new Class("object.container.person");

    public PersonContainer() {
        setClazz(CLASS);
    }

    public PersonContainer(Container other) {
        super(other);
    }

    public PersonContainer(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public PersonContainer(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, CLASS, childCount);
    }

    public String getLanguage() {
        return getFirstPropertyValue(DC.LANGUAGE.class);
    }

    public PersonContainer setLanguage(String language) {
        replaceFirstProperty(new DC.LANGUAGE(language));
        return this;
    }

}
