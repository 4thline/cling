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

package org.fourthline.cling.model.message.header;

import org.seamless.util.io.HexBin;

/**
 * Custom header for Cling, used to transfer the MAC ethernet address for Wake-on-LAN.
 *
 * @author Christian Bauer
 */
public class InterfaceMacHeader extends UpnpHeader<byte[]> {

    public InterfaceMacHeader() {
    }

    public InterfaceMacHeader(byte[] value) {
        setValue(value);
    }

    public InterfaceMacHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        byte[] bytes = HexBin.stringToBytes(s, ":");
        setValue(bytes);
        if (bytes.length != 6) {
            throw new InvalidHeaderException("Invalid MAC address: " + s);
        }
    }

    public String getString() {
        return HexBin.bytesToString(getValue(), ":");
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getString() + "'";
    }
}