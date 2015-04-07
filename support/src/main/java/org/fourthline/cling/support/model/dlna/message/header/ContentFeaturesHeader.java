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

import java.util.EnumMap;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.DLNAAttribute;

/**
 * @author Mario Franco
 */
public class ContentFeaturesHeader extends DLNAHeader<EnumMap<DLNAAttribute.Type, DLNAAttribute>> {

    public ContentFeaturesHeader() {
        setValue(new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class));
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            String[] atts = s.split(";");
            for (String att : atts) {
                String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    DLNAAttribute.Type type = DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        DLNAAttribute dlnaAttrinute = DLNAAttribute.newInstance(type, attNameValue[1], "");
                        getValue().put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }

    @Override
    public String getString() {
        String s = "";
        for (DLNAAttribute.Type type : DLNAAttribute.Type.values()) {
            String value = getValue().containsKey(type) ? getValue().get(type).getString() : null;
            if (value != null && value.length() != 0) {
                s += (s.length() == 0 ? "" : ";") + type.getAttributeName() + "=" + value;
            }
        }
        return s;
    }
}
