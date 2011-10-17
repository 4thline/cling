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

/*
    ui4 (ABS_COUNT, REL_COUNT, TRACK_NR, TAPE-INDEX, FRAME)
    time (ABS_TIME, REL_TIME)
    float (CHANNEL_FREQ, in Hz)
    */
public enum SeekMode {

    TRACK_NR("TRACK_NR"),
    ABS_TIME("ABS_TIME"),
    REL_TIME("REL_TIME"),
    ABS_COUNT("ABS_COUNT"),
    REL_COUNT("REL_COUNT"),
    CHANNEL_FREQ("CHANNEL_FREQ"),
    TAPE_INDEX("TAPE-INDEX"),
    FRAME("FRAME");

    private String protocolString;

    SeekMode(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override
    public String toString() {
        return protocolString;
    }

    public static SeekMode valueOrExceptionOf(String s) throws IllegalArgumentException {
        for (SeekMode seekMode : values()) {
            if (seekMode.protocolString.equals(s)) {
                return seekMode;
            }
        }
        throw new IllegalArgumentException("Invalid seek mode string: " + s);
    }
}

