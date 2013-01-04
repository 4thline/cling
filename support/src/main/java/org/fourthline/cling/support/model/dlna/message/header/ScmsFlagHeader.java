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

import java.util.regex.Pattern;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.ScmsFlagType;

/**
 * @author Mario Franco
 */
public class ScmsFlagHeader extends DLNAHeader<ScmsFlagType> {

    final static Pattern pattern = Pattern.compile("^[01]{2}$", Pattern.CASE_INSENSITIVE);
    
    public ScmsFlagHeader() {
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (pattern.matcher(s).matches()) {
          setValue(new ScmsFlagType(s.charAt(0) == '0', s.charAt(1) == '0'));
          return;
        }
        throw new InvalidHeaderException("Invalid ScmsFlag header value: " + s);
    }

    @Override
    public String getString() {
        ScmsFlagType v = getValue();
        return (v.isCopyright()?"0":"1") + (v.isOriginal()?"0":"1");
    }
}
