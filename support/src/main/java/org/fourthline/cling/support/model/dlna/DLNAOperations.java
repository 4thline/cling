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
package org.fourthline.cling.support.model.dlna;

/**
 * DLNA.ORG_OP: operations parameter (string)
 *
 * <pre>
 *     "00" (or "0") neither time seek range nor range supported
 *     "01" range supported
 *     "10" time seek range supported
 *     "11" both time seek range and range supported
 * </pre>
 *
 * @author Mario Franco
 */
public enum DLNAOperations {

    NONE(0x00),
    RANGE(0x01),
    TIMESEEK(0x10);

    private int code;

    DLNAOperations(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DLNAOperations valueOf(int code) {
        for (DLNAOperations errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
