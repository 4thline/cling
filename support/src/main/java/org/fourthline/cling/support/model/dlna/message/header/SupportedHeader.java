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
package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

/**
 * @author Mario Franco
 */
public class SupportedHeader extends DLNAHeader<String[]> {
    
    public SupportedHeader() {
        setValue(new String[]{});
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            if (s.endsWith(";"))
                s = s.substring(0, s.length()-1);
            setValue(s.split("\\s*,\\s*"));
            return;
        }
        throw new InvalidHeaderException("Invalid Supported header value: " + s);
    }

    @Override
    public String getString() {
        String[] v = getValue();
        String r = v.length>0 ? v[0] : "";
        for (int i = 1; i < v.length; i++) {
            r += ","+v[i];
        }
        return r;
    }
}
