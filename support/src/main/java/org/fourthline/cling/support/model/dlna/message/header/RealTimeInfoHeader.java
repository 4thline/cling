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
import org.fourthline.cling.support.model.dlna.types.NormalPlayTime;

/**
 * @author Mario Franco
 */
public class RealTimeInfoHeader extends DLNAHeader<NormalPlayTime> {

    public static final String PREFIX = "DLNA.ORG_TLAG=";
    
    public RealTimeInfoHeader() {
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0 && s.startsWith(PREFIX) ) {
            try {
                s = s.substring(PREFIX.length());
                setValue(s.equals("*") ? null : NormalPlayTime.valueOf(s) );
                return;
            } catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid RealTimeInfo header value: " + s);
    }

    @Override
    public String getString() {
        NormalPlayTime v = getValue();
        if (v == null)
            return PREFIX+"*";
        return PREFIX+v.getString();
    }
}
