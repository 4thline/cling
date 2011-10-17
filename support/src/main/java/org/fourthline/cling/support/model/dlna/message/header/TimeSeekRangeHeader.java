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
import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;
import org.fourthline.cling.support.model.dlna.types.TimeSeekRangeType;

/**
 * @author Mario Franco
 */
public class TimeSeekRangeHeader extends DLNAHeader<TimeSeekRangeType> {

    public TimeSeekRangeHeader() {
    }

    public TimeSeekRangeHeader(TimeSeekRangeType timeSeekRange) {
        setValue(timeSeekRange);
    }
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            String[] params = s.split(" ");
            if (params.length>0) {
                try {
                    TimeSeekRangeType t = new TimeSeekRangeType(NormalPlayTimeRange.valueOf(params[0]));
                    if (params.length > 1) {
                        t.setBytesRange(BytesRange.valueOf(params[1]));
                    }
                    setValue(t);
                    return;
                } catch (InvalidValueException invalidValueException) {
                    throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s + "; "+invalidValueException.getMessage());
                }
            }
        }
        throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s);
    }

    @Override
    public String getString() {
        TimeSeekRangeType t = getValue();
        String s = t.getNormalPlayTimeRange().getString();
        if (t.getBytesRange()!=null) s+= " "+t.getBytesRange().getString(true);
        return s;
    }
    
}
