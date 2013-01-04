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
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 * @author Mario Franco
 */
public class BufferBytesHeader extends DLNAHeader<UnsignedIntegerFourBytes> {

    public BufferBytesHeader() {
        setValue(new UnsignedIntegerFourBytes(0L));
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        try {
            setValue(new UnsignedIntegerFourBytes(s));
            return;
        } catch (NumberFormatException numberFormatException) {
        }
        throw new InvalidHeaderException("Invalid header value: " + s);
    }

    @Override
    public String getString() {
        return getValue().getValue().toString();
    }
}
