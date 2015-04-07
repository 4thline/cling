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
package example.mediaserver;

import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportPlaySpeed;
import java.util.EnumMap;
import org.fourthline.cling.support.model.dlna.DLNAAttribute;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.dlna.DLNAAttribute.Type;
import org.fourthline.cling.support.model.dlna.DLNAConversionIndicator;
import org.fourthline.cling.support.model.dlna.DLNAFlags;
import org.fourthline.cling.support.model.dlna.DLNAProfiles;
import org.fourthline.cling.support.model.dlna.DLNAProtocolInfo;
import org.testng.annotations.Test;

import java.util.EnumSet;
import org.fourthline.cling.support.model.dlna.DLNAConversionIndicatorAttribute;
import org.fourthline.cling.support.model.dlna.DLNAFlagsAttribute;
import org.fourthline.cling.support.model.dlna.DLNAOperations;
import org.fourthline.cling.support.model.dlna.DLNAOperationsAttribute;
import org.fourthline.cling.support.model.dlna.DLNAPlaySpeedAttribute;

import static org.testng.Assert.assertEquals;

/**
 * @author Mario Franco
 */
public class DLNAProtocolTest {

    @Test
    public void convertProtocol() throws Exception {

        ProtocolInfo p = new ProtocolInfo(
                "http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_TN;DLNA.ORG_PS=-1,1/2,4;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=01700000000000000000000000000000"
        );

        DLNAProtocolInfo dp = new DLNAProtocolInfo(p);

        EnumSet<DLNAFlags> dflags = (EnumSet<DLNAFlags>) dp.getAttribute(Type.DLNA_ORG_FLAGS).getValue();
        TransportPlaySpeed[] speeds = (TransportPlaySpeed[]) dp.getAttribute(Type.DLNA_ORG_PS).getValue();
        
        assertEquals(dp.getProtocol(), Protocol.HTTP_GET);
        assertEquals(dp.getAttribute(Type.DLNA_ORG_PN).getValue(), DLNAProfiles.JPEG_TN);
        assertEquals(dp.getAttribute(Type.DLNA_ORG_CI).getValue(), DLNAConversionIndicator.TRANSCODED);
        
        assertEquals(speeds.length, 3);
        assertEquals(speeds[0].getValue(),"-1");
        assertEquals(speeds[1].getValue(),"1/2");
        assertEquals(speeds[2].getValue(),"4");
        
        assertEquals(dflags.size(), 4);
        assertEquals(dflags.contains(DLNAFlags.DLNA_V15), true);
        assertEquals(dflags.contains(DLNAFlags.CONNECTION_STALL), true);
        assertEquals(dflags.contains(DLNAFlags.RTSP_PAUSE), false);
    }

    @Test
    public void createProtocol() throws Exception {

        DLNAProtocolInfo dp = new DLNAProtocolInfo(DLNAProfiles.JPEG_TN);
        assertEquals(dp.toString(), "http-get:*:image/jpeg:DLNA.ORG_PN=JPEG_TN");

        EnumMap<DLNAAttribute.Type, DLNAAttribute> attributes = new EnumMap<>(DLNAAttribute.Type.class);
        attributes.put(Type.DLNA_ORG_PS, new DLNAPlaySpeedAttribute(new String[] {"1"}));
        dp = new DLNAProtocolInfo(DLNAProfiles.MATROSKA_MKV, attributes);
        assertEquals(dp.toString(), "http-get:*:video/x-mkv:DLNA.ORG_PN=MATROSKA");
        
        attributes.put(Type.DLNA_ORG_PS, new DLNAPlaySpeedAttribute(new String[] {"1","2/3","-1","4"}));
        dp = new DLNAProtocolInfo(DLNAProfiles.MATROSKA_MKV, attributes);
        assertEquals(dp.toString(), "http-get:*:video/x-mkv:DLNA.ORG_PN=MATROSKA;DLNA.ORG_PS=2/3,-1,4");
        
        attributes.put(Type.DLNA_ORG_OP, new DLNAOperationsAttribute(DLNAOperations.TIMESEEK, DLNAOperations.RANGE ));
        attributes.put(Type.DLNA_ORG_FLAGS, new DLNAFlagsAttribute(
                DLNAFlags.DLNA_V15, 
                DLNAFlags.CONNECTION_STALL, 
                DLNAFlags.STREAMING_TRANSFER_MODE,
                DLNAFlags.BACKGROUND_TRANSFERT_MODE)
        );        
        dp = new DLNAProtocolInfo(DLNAProfiles.MATROSKA_MKV, attributes);
        assertEquals(dp.toString(), "http-get:*:video/x-mkv:DLNA.ORG_PN=MATROSKA;DLNA.ORG_OP=11;DLNA.ORG_PS=2/3,-1,4;DLNA.ORG_FLAGS=01700000000000000000000000000000");
        
        attributes.put(Type.DLNA_ORG_CI, new DLNAConversionIndicatorAttribute(DLNAConversionIndicator.TRANSCODED));
        dp = new DLNAProtocolInfo(DLNAProfiles.MATROSKA_MKV, attributes);
        assertEquals(dp.toString(), "http-get:*:video/x-mkv:DLNA.ORG_PN=MATROSKA;DLNA.ORG_OP=11;DLNA.ORG_PS=2/3,-1,4;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=01700000000000000000000000000000");
    }
}
