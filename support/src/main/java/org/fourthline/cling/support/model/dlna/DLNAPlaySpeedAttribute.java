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

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportPlaySpeed;

/**
 * @author Mario Franco
 */
public class DLNAPlaySpeedAttribute extends DLNAAttribute<TransportPlaySpeed[]> {
    
    public DLNAPlaySpeedAttribute() {
        setValue(new TransportPlaySpeed[] {});
    }

    public DLNAPlaySpeedAttribute(TransportPlaySpeed[] speeds) {
        setValue(speeds);
    }
    
    public DLNAPlaySpeedAttribute(String[] speeds) {
        TransportPlaySpeed[] sp = new TransportPlaySpeed[speeds.length]; 
        try {
            for (int i = 0; i < speeds.length; i++) {
                sp[i] = new TransportPlaySpeed(speeds[i]);
            }
        } catch (InvalidValueException invalidValueException) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds.");
        }
        setValue(sp);
    }

    public void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException {
        TransportPlaySpeed[] value = null;
        if (s != null && s.length() != 0) {
            String[] speeds = s.split(",");
            try {
                value = new TransportPlaySpeed[speeds.length]; 
                for (int i = 0; i < speeds.length; i++) {
                    value[i] = new TransportPlaySpeed(speeds[i]);
                }
            } catch (InvalidValueException invalidValueException) {
                value = null;
            }
        }
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds from: " + s);
        }
        setValue(value);
    }

    public String getString() {
        String s = "";
        for (TransportPlaySpeed speed : getValue()) {
            if (speed.getValue().equals("1"))
                continue;
            s += (s.length() == 0 ? "" : ",") + speed;
        }
        return s;
    }
    
}
