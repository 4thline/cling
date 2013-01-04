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
import org.fourthline.cling.support.model.dlna.types.BufferInfoType;

/**
 * @author Mario Franco
 */
public class BufferInfoHeader extends DLNAHeader<BufferInfoType> {

    public BufferInfoHeader() {
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                setValue(BufferInfoType.valueOf(s));
                return;
            } catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid BufferInfo header value: " + s);
    }

    @Override
    public String getString() {
        return getValue().getString();
    }
}
