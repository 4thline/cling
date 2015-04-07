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
                new AbstractMap.SimpleEntry<>(
                        "val",
                        new UnsignedIntegerTwoBytesDatatype().getString(
                                new UnsignedIntegerTwoBytes(getValue().getVolumeDB())
                        )
                ),
                new AbstractMap.SimpleEntry<>(
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
