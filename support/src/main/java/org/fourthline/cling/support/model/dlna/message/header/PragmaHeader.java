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

import java.util.ArrayList;
import java.util.List;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.PragmaType;

/**
 * DLNA Pragma tokens:
 *  - getIfoFileURI.dlna.org
 *  - ifoFileURI.dlna.org
 * 
 * @author Mario Franco
 */
public class PragmaHeader extends DLNAHeader<List<PragmaType>> {
    
    public PragmaHeader() {
        setValue(new ArrayList<PragmaType>());
    }
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            if (s.endsWith(";")) {
                s = s.substring(0, s.length() - 1);
            }
            String[] list = s.split("\\s*;\\s*");
            List<PragmaType> value = new ArrayList<>();
            for (String pragma : list) {
                value.add(PragmaType.valueOf(pragma));
            }
            return;
        }
        throw new InvalidHeaderException("Invalid Pragma header value: " + s);
    }
    
    @Override
    public String getString() {
        List<PragmaType> v = getValue();
        String r = "";
        for (PragmaType pragma : v) {
            r += (r.length() == 0 ? "": "," )+  pragma.getString();
        }
        return r;
    }
}
