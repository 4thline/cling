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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public enum StorageMedium {

    UNKNOWN,
    DV,
    MINI_DV("MINI-DV"),
    VHS,
    W_VHS("W-VHS"),
    S_VHS("S-VHS"),
    D_VHS("D-VHS"),
    VHSC,
    VIDEO8,
    HI8,
    CD_ROM("CD-ROM"),
    CD_DA("CD-DA"),
    CD_R("CD-R"),
    CD_RW("CD-RW"),
    VIDEO_CD("VIDEO-CD"),
    SACD,
    MD_AUDIO("M-AUDIO"),
    MD_PICTURE("MD-PICTURE"),
    DVD_ROM("DVD-ROM"),
    DVD_VIDEO("DVD-VIDEO"),
    DVD_R("DVD-R"),
    DVD_PLUS_RW("DVD+RW"),
    DVD_MINUS_RW("DVD-RW"),
    DVD_RAM("DVD-RAM"),
    DVD_AUDIO("DVD-AUDIO"),
    DAT,
    LD,
    HDD,
    MICRO_MV("MICRO_MV"),
    NETWORK,
    NONE,
    NOT_IMPLEMENTED,
    VENDOR_SPECIFIC;

    private static Map<String, StorageMedium> byProtocolString = new HashMap<String, StorageMedium>() {{
        for (StorageMedium e : StorageMedium.values()) {
            put(e.protocolString, e);
        }
    }};

    private String protocolString;

    StorageMedium() {
        this(null);
    }

    StorageMedium(String protocolString) {
        this.protocolString = protocolString == null ? this.name() : protocolString;
    }

    @Override
    public String toString() {
        return protocolString;
    }

    public static StorageMedium valueOrExceptionOf(String s) {
        StorageMedium sm = byProtocolString.get(s);
        if (sm != null) return sm;
        throw new IllegalArgumentException("Invalid storage medium string: " + s);
    }

    public static StorageMedium valueOrVendorSpecificOf(String s) {
        StorageMedium sm = byProtocolString.get(s);
        return sm != null ? sm : StorageMedium.VENDOR_SPECIFIC;
    }

    public static StorageMedium[] valueOfCommaSeparatedList(String s) {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        if (strings == null) return new StorageMedium[0];
        StorageMedium[] result = new StorageMedium[strings.length];
        for (int i = 0; i < strings.length; i++) {
            result[i] = valueOrVendorSpecificOf(strings[i]);
        }
        return result;
    }

}