/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        setValue(new EnumMap(DLNAAttribute.Type.class));
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
