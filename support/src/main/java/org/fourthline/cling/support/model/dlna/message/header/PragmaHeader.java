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
            List<PragmaType> value = new ArrayList<PragmaType>();
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
