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

package org.fourthline.cling.model.types;

import org.seamless.util.io.HexBin;

/**
 * @author Christian Bauer
 */
public class BinHexDatatype extends AbstractDatatype<byte[]> {

    public BinHexDatatype() {
    }

    public Class<byte[]> getValueType() {
        return byte[].class;
    }

    public byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
            return HexBin.stringToBytes(s);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getString(byte[] value) throws InvalidValueException {
        if (value == null) return "";
        try {
            return HexBin.bytesToString(value);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
    }

}