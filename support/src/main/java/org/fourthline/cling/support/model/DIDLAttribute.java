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

package org.fourthline.cling.support.model;

/**
 *
 * @author Christian Bauer
 * @author Mario Franco
 */
public class DIDLAttribute {
    
    private String namespaceURI;
    private String prefix;
    private String value;

    public DIDLAttribute(String namespaceURI, String prefix, String value) {
        this.namespaceURI = namespaceURI;
        this.prefix = prefix;
        this.value = value;
    }
    /**
     * @return the namespaceURI
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
}
