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

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportPlaySpeed;

/**
 * @author Mario Franco
 */
public class PlaySpeedHeader extends DLNAHeader<TransportPlaySpeed> {

    public PlaySpeedHeader() {
    }

    public PlaySpeedHeader(TransportPlaySpeed speed) {
        setValue(speed);
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                TransportPlaySpeed t = new TransportPlaySpeed(s);
                setValue(t);
                return;
            } catch (InvalidValueException invalidValueException) {}
        }
        throw new InvalidHeaderException("Invalid PlaySpeed header value: " + s);
    }

    @Override
    public String getString() {
        return getValue().getValue();
    }
}
