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
package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.dlna.types.AvailableSeekRangeType;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;

/**
 * @author Mario Franco
 */
public class AvailableSeekRangeHeader extends DLNAHeader<AvailableSeekRangeType> {

    public AvailableSeekRangeHeader() {
    }

    public AvailableSeekRangeHeader(AvailableSeekRangeType timeSeekRange) {
        setValue(timeSeekRange);
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            String[] params = s.split(" ");
            if (params.length > 1) {
                try {
                    AvailableSeekRangeType.Mode mode = null;
                    NormalPlayTimeRange timeRange = null;
                    BytesRange byteRange = null;

                    //Parse Mode
                    try {
                        mode = AvailableSeekRangeType.Mode.valueOf("MODE_" + params[0]);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidValueException("Invalid AvailableSeekRange Mode");
                    }

                    boolean useTime = true;
                    //Parse Second Token
                    try {
                        timeRange = NormalPlayTimeRange.valueOf(params[1],true);
                    } catch (InvalidValueException timeInvalidValueException) {
                        try {
                            byteRange = BytesRange.valueOf(params[1]);
                            useTime = false;
                        } catch (InvalidValueException bytesInvalidValueException) {
                            throw new InvalidValueException("Invalid AvailableSeekRange Range");
                        }
                    }
                    if (useTime) {
                        if (params.length > 2) {
                            //Parse Third Token
                            byteRange = BytesRange.valueOf(params[2]);
                            setValue(new AvailableSeekRangeType(mode, timeRange, byteRange));
                        }
                        else {
                            setValue(new AvailableSeekRangeType(mode, timeRange));
                        }
                    } else {
                        setValue(new AvailableSeekRangeType(mode, byteRange));
                    }
                    return;
                } catch (InvalidValueException invalidValueException) {
                    throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + s + "; " + invalidValueException.getMessage());
                }
            }
        }
        throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + s);
    }

    @Override
    public String getString() {
        AvailableSeekRangeType t = getValue();
        String s = Integer.toString(t.getModeFlag().ordinal());
        if (t.getNormalPlayTimeRange() != null) {
            s += " " + t.getNormalPlayTimeRange().getString(false);
        }
        if (t.getBytesRange() != null) {
            s += " " + t.getBytesRange().getString(false);
        }
        return s;
    }
}
