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

import java.util.EnumSet;

/**
 * @author Mario Franco
 */
public class DLNAFlagsAttribute extends DLNAAttribute<EnumSet<DLNAFlags>> {

    public DLNAFlagsAttribute() {
        setValue(EnumSet.noneOf(DLNAFlags.class));
    }

    public DLNAFlagsAttribute(DLNAFlags... flags) {
        if (flags != null && flags.length > 0) {
            DLNAFlags first = flags[0];
            if (flags.length > 1) {
                System.arraycopy(flags, 1, flags, 0, flags.length - 1);
                setValue(EnumSet.of(first, flags));
            } else {
                setValue(EnumSet.of(first));
            }
        }
    }

    public void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException {
        EnumSet<DLNAFlags> value = EnumSet.noneOf(DLNAFlags.class);
        try {
            int parseInt = Integer.parseInt(s.substring(0, s.length() - 24), 16);
            for (DLNAFlags op : DLNAFlags.values()) {
                int code = op.getCode() & parseInt;
                if (op.getCode() == code) {
                    value.add(op);
                }
            }
        } catch (Exception e) {
        }

        if (value.isEmpty())
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA flags integer from: " + s);

        setValue(value);
    }

    public String getString() {
        int code = 0;
        for (DLNAFlags op : getValue()) {
            code |= op.getCode();
        }
        return String.format("%08x%024x", code, 0);
    }
}
