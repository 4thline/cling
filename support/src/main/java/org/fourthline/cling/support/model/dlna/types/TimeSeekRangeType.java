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
package org.fourthline.cling.support.model.dlna.types;

import org.fourthline.cling.model.types.BytesRange;

/**
 *
 * @author Mario Franco
 */
public class TimeSeekRangeType {

    private NormalPlayTimeRange normalPlayTimeRange;
    private BytesRange bytesRange;
    

    public TimeSeekRangeType(NormalPlayTimeRange nptRange) {
        this.normalPlayTimeRange = nptRange;
    }
    
    public TimeSeekRangeType(NormalPlayTimeRange nptRange, BytesRange byteRange) {
        this.normalPlayTimeRange = nptRange;
        this.bytesRange = byteRange;
    }

    /**
     * @return the normalPlayTimeRange
     */
    public NormalPlayTimeRange getNormalPlayTimeRange() {
        return normalPlayTimeRange;
    }

    /**
     * @return the bytesRange
     */
    public BytesRange getBytesRange() {
        return bytesRange;
    }

    /**
     * @param bytesRange the bytesRange to set
     */
    public void setBytesRange(BytesRange bytesRange) {
        this.bytesRange = bytesRange;
    }


}
