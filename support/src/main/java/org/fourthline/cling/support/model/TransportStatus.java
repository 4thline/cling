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
 */
public enum TransportStatus {

    OK,
    ERROR_OCCURED,
    CUSTOM;

    String value;

    TransportStatus() {
        this.value = name();
    }

    public String getValue() {
        return value;
    }

    public TransportStatus setValue(String value) {
        this.value = value;
        return this;
    }

    public static TransportStatus valueOrCustomOf(String s) {
        try {
            return TransportStatus.valueOf(s);
        } catch (IllegalArgumentException ex) {
            return TransportStatus.CUSTOM.setValue(s);
        }
    }
}
