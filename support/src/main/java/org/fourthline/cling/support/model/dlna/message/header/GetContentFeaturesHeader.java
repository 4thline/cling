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

/**
 * @author Mario Franco
 */
public class GetContentFeaturesHeader extends DLNAHeader<Integer> {

    public GetContentFeaturesHeader() {
        setValue(1);
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                int t = Integer.parseInt(s);
                if (t==1)
                    return;
            } catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid GetContentFeatures header value: " + s);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
