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

import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.lastchange.EventedValueShort;
import org.fourthline.cling.support.lastchange.EventedValueString;
import org.fourthline.cling.support.lastchange.EventedValueUnsignedIntegerTwoBytes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Bauer
 */
public class RenderingControlVariable {

    public static Set<Class<? extends EventedValue>> ALL = new HashSet<Class<? extends EventedValue>>() {{
        add(PresetNameList.class);
        add(Brightness.class);
        add(Contrast.class);
        add(Sharpness.class);
        add(RedVideoGain.class);
        add(BlueVideoGain.class);
        add(GreenVideoGain.class);
        add(RedVideoBlackLevel.class);
        add(BlueVideoBlackLevel.class);
        add(GreenVideoBlackLevel.class);
        add(ColorTemperature.class);
        add(HorizontalKeystone.class);
        add(VerticalKeystone.class);
        add(Mute.class);
        add(VolumeDB.class);
        add(Volume.class);
        add(Loudness.class);
    }};

    public static class PresetNameList extends EventedValueString {
        public PresetNameList(String s) {
            super(s);
        }

        public PresetNameList(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Brightness extends EventedValueUnsignedIntegerTwoBytes {
        public Brightness(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public Brightness(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Contrast extends EventedValueUnsignedIntegerTwoBytes {
        public Contrast(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public Contrast(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Sharpness extends EventedValueUnsignedIntegerTwoBytes {
        public Sharpness(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public Sharpness(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class RedVideoGain extends EventedValueUnsignedIntegerTwoBytes {
        public RedVideoGain(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public RedVideoGain(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class BlueVideoGain extends EventedValueUnsignedIntegerTwoBytes {
        public BlueVideoGain(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public BlueVideoGain(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class GreenVideoGain extends EventedValueUnsignedIntegerTwoBytes {
        public GreenVideoGain(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public GreenVideoGain(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class RedVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes {
        public RedVideoBlackLevel(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public RedVideoBlackLevel(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class BlueVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes {
        public BlueVideoBlackLevel(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public BlueVideoBlackLevel(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class GreenVideoBlackLevel extends EventedValueUnsignedIntegerTwoBytes {
        public GreenVideoBlackLevel(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public GreenVideoBlackLevel(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class ColorTemperature extends EventedValueUnsignedIntegerTwoBytes {
        public ColorTemperature(UnsignedIntegerTwoBytes value) {
            super(value);
        }

        public ColorTemperature(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class HorizontalKeystone extends EventedValueShort {
        public HorizontalKeystone(Short value) {
            super(value);
        }

        public HorizontalKeystone(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class VerticalKeystone extends EventedValueShort {
        public VerticalKeystone(Short value) {
            super(value);
        }

        public VerticalKeystone(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Mute extends EventedValueChannelMute {
        public Mute(ChannelMute value) {
            super(value);
        }

        public Mute(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class VolumeDB extends EventedValueChannelVolumeDB {
        public VolumeDB(ChannelVolumeDB value) {
            super(value);
        }

        public VolumeDB(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Volume extends EventedValueChannelVolume {
        public Volume(ChannelVolume value) {
            super(value);
        }

        public Volume(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }

    public static class Loudness extends EventedValueChannelLoudness {
        public Loudness(ChannelLoudness value) {
            super(value);
        }

        public Loudness(Map.Entry<String, String>[] attributes) {
            super(attributes);
        }
    }
}
