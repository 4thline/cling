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

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.ModelUtil;

import java.util.Arrays;

/**
 * An arbitrary list of comma-separated elements, representing DLNA capabilities (whatever that is).
 *
 * @author Christian Bauer
 */
public class DLNACaps {

    final String[] caps;

    public DLNACaps(String[] caps) {
        this.caps = caps;
    }

    public String[] getCaps() {
        return caps;
    }

    static public DLNACaps valueOf(String s) throws InvalidValueException {
        if (s == null || s.length() == 0) return new DLNACaps(new String[0]);
        String[] caps = s.split(",");
        String[] trimmed = new String[caps.length];
        for (int i = 0; i < caps.length; i++) {
            trimmed[i] = caps[i].trim();
        }
        return new DLNACaps(trimmed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DLNACaps dlnaCaps = (DLNACaps) o;

        if (!Arrays.equals(caps, dlnaCaps.caps)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(caps);
    }

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(getCaps());
    }
}
