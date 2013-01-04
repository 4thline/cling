/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
