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
package org.fourthline.cling.support.model.dlna;

/**
 * @author Mario Franco
 */
public class DLNAProfileAttribute extends DLNAAttribute<DLNAProfiles> {
    
    public DLNAProfileAttribute() {
        setValue(DLNAProfiles.NONE);
    }
    
    public DLNAProfileAttribute(DLNAProfiles profile) {
        setValue(profile);
    }
    
    public void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException {
        DLNAProfiles value = DLNAProfiles.valueOf(s, cf);
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA profile from: " + s);
        }
        setValue(value);
    }
    
    public String getString() {
        return getValue().getCode();
    }
}
