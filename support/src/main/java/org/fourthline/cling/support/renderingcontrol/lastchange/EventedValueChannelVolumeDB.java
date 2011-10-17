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

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytesDatatype;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.shared.AbstractMap;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class EventedValueChannelVolumeDB extends EventedValue<ChannelVolumeDB> {

    public EventedValueChannelVolumeDB(ChannelVolumeDB value) {
        super(value);
    }

    public EventedValueChannelVolumeDB(Map.Entry<String, String>[] attributes) {
        super(attributes);
    }

    @Override
    protected ChannelVolumeDB valueOf(Map.Entry<String, String>[] attributes) throws InvalidValueException {
        Channel channel = null;
        Integer volumeDB = null;
        for (Map.Entry<String, String> attribute : attributes) {
            if (attribute.getKey().equals("channel"))
                channel = Channel.valueOf(attribute.getValue());
            if (attribute.getKey().equals("val"))
                volumeDB = (new UnsignedIntegerTwoBytesDatatype()
                        .valueOf(attribute.getValue()))
                        .getValue().intValue(); // Java is fun!
        }
        return channel != null && volumeDB != null ? new ChannelVolumeDB(channel, volumeDB) : null;
    }

    @Override
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{
                new AbstractMap.SimpleEntry<String, String>(
                        "val",
                        new UnsignedIntegerTwoBytesDatatype().getString(
                                new UnsignedIntegerTwoBytes(getValue().getVolumeDB())
                        )
                ),
                new AbstractMap.SimpleEntry<String, String>(
                        "channel",
                        getValue().getChannel().name()
                )
        };
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    protected Datatype getDatatype() {
        return null; // Not needed
    }
}
