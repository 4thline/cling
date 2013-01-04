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

package org.fourthline.cling.support.test.model.dlna;

import org.fourthline.cling.support.model.dlna.message.header.BufferInfoHeader;
import org.fourthline.cling.support.model.dlna.message.header.SupportedHeader;
import org.fourthline.cling.support.model.dlna.message.header.EventTypeHeader;
import org.fourthline.cling.support.model.dlna.message.header.MaxPrateHeader;
import org.fourthline.cling.support.model.dlna.message.header.WCTHeader;
import org.fourthline.cling.support.model.dlna.message.header.ScmsFlagHeader;
import org.fourthline.cling.support.model.dlna.message.header.RealTimeInfoHeader;
import org.fourthline.cling.support.model.dlna.message.header.PeerManagerHeader;
import org.fourthline.cling.support.model.dlna.message.header.TransferModeHeader;
import java.util.EnumSet;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportPlaySpeed;
import org.fourthline.cling.support.model.dlna.DLNAAttribute.Type;
import org.fourthline.cling.support.model.dlna.DLNAConversionIndicator;
import org.fourthline.cling.support.model.dlna.DLNAFlags;
import org.fourthline.cling.support.model.dlna.DLNAProfiles;
import org.fourthline.cling.support.model.dlna.message.header.ContentFeaturesHeader;
import org.fourthline.cling.support.model.dlna.message.header.GetContentFeaturesHeader;
import org.fourthline.cling.support.model.dlna.message.header.GetAvailableSeekRangeHeader;
import org.fourthline.cling.support.model.dlna.types.AvailableSeekRangeType;
import org.fourthline.cling.support.model.dlna.message.header.AvailableSeekRangeHeader;
import org.fourthline.cling.support.model.dlna.message.header.PlaySpeedHeader;
import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.message.header.TimeSeekRangeHeader;
import org.fourthline.cling.support.model.dlna.types.CodedDataBuffer.TransferMechanism;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class DLNAHeaderParsingTest {

    @Test
    public void parseTimeSeekRangeHeader() {
        TimeSeekRangeHeader header = new TimeSeekRangeHeader();
        header.setString("npt=335.11-336.08");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335110);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336080);
        assertEquals(header.getString(), "npt=335.110-336.080/*");
        
        header.setString("npt=00:05:35.3-00:05:37.5");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335300);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),337500);
        assertEquals(header.getString(), "npt=335.300-337.500/*");
        
        header.setString("npt=335.1-336.1/40445.4");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeDuration().getMilliseconds(),40445400);
        assertEquals(header.getString(), "npt=335.100-336.100/40445.400");
        
        header.setString("npt=335.1-336.1/*");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeDuration(), null);
        assertEquals(header.getString(), "npt=335.100-336.100/*");
        
        
        header.setString("npt=335.1-336.1/40445.4 bytes=1539686400-1540210688/304857907200");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeDuration().getMilliseconds(), 40445400);
        assertEquals(header.getValue().getBytesRange().getFirstByte(),new Long(1539686400));
        assertEquals(header.getValue().getBytesRange().getLastByte(), new Long(1540210688));
        assertEquals(header.getValue().getBytesRange().getByteLength(), new Long(304857907200L));
        assertEquals(header.getString(), "npt=335.100-336.100/40445.400 bytes=1539686400-1540210688/304857907200");
        
        header.setString("npt=335.1-336.1/40445.4 bytes=1539686400-1540210688/*");
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeDuration().getMilliseconds(), 40445400);
        assertEquals(header.getValue().getBytesRange().getFirstByte(),new Long(1539686400));
        assertEquals(header.getValue().getBytesRange().getLastByte(), new Long(1540210688));
        assertEquals(header.getValue().getBytesRange().getByteLength(), null);
        assertEquals(header.getString(), "npt=335.100-336.100/40445.400 bytes=1539686400-1540210688/*");
    }


    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidTimeSeekRangeHeader() {
        TimeSeekRangeHeader header = new TimeSeekRangeHeader();
        header.setString("npt=335.1111-336.08");
    }

    @Test
    public void parsePlaySpeedHeader() {
        PlaySpeedHeader header = new PlaySpeedHeader();
        
        header.setString("10");
        assertEquals(header.getValue().getValue(),"10");
        assertEquals(header.getString(), "10");
        
        header.setString("-10");
        assertEquals(header.getValue().getValue(),"-10");
        assertEquals(header.getString(), "-10");
        
        header.setString("-1/10");
        assertEquals(header.getValue().getValue(),"-1/10");
        assertEquals(header.getString(), "-1/10");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidPlaySpeedHeader() {
        PlaySpeedHeader header = new PlaySpeedHeader();
        header.setString("2.1");
    }
    
    @Test
    public void parseAvailableSeekRangeHeader() {
        AvailableSeekRangeHeader header = new AvailableSeekRangeHeader();
        
        header.setString("1 npt=335.1-336.1 bytes=1539686400-1540210688");
        assertEquals(header.getValue().getModeFlag(),AvailableSeekRangeType.Mode.MODE_1);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getValue().getBytesRange().getFirstByte(),new Long(1539686400));
        assertEquals(header.getValue().getBytesRange().getLastByte(), new Long(1540210688));
        assertEquals(header.getString(), "1 npt=335.100-336.100 bytes=1539686400-1540210688");
        
        header.setString("0 npt=335.1-336.1");
        assertEquals(header.getValue().getModeFlag(),AvailableSeekRangeType.Mode.MODE_0);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeStart().getMilliseconds(),335100);
        assertEquals(header.getValue().getNormalPlayTimeRange().getTimeEnd().getMilliseconds(),336100);
        assertEquals(header.getString(), "0 npt=335.100-336.100");
        
        header.setString("1 bytes=1539686400-1540210688");
        assertEquals(header.getValue().getModeFlag(),AvailableSeekRangeType.Mode.MODE_1);
        assertEquals(header.getValue().getBytesRange().getFirstByte(),new Long(1539686400));
        assertEquals(header.getValue().getBytesRange().getLastByte(), new Long(1540210688));
        assertEquals(header.getString(), "1 bytes=1539686400-1540210688");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidAvailableSeekRangeHeader() {
        AvailableSeekRangeHeader header = new AvailableSeekRangeHeader();
        header.setString("1 npt=335.1-");
    }
    
    @Test
    public void parseGetAvailableSeekRangeHeader() {
        GetAvailableSeekRangeHeader header = new GetAvailableSeekRangeHeader();
        header.setString("1");
        assertEquals(header.getValue().intValue(),1);
        assertEquals(header.getString(), "1");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidGetAvailableSeekRangeHeader() {
        GetAvailableSeekRangeHeader header = new GetAvailableSeekRangeHeader();
        header.setString("2");
    }

    @Test
    public void parseContentFeaturesHeader() {
        ContentFeaturesHeader header = new ContentFeaturesHeader();
        header.setString("DLNA.ORG_PN=JPEG_TN;DLNA.ORG_PS=-1,1/2,4;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=01700000000000000000000000000000");

        EnumSet<DLNAFlags> dflags = (EnumSet<DLNAFlags>) header.getValue().get(Type.DLNA_ORG_FLAGS).getValue();
        TransportPlaySpeed[] speeds = (TransportPlaySpeed[]) header.getValue().get(Type.DLNA_ORG_PS).getValue();
        
        assertEquals(header.getValue().get(Type.DLNA_ORG_PN).getValue(), DLNAProfiles.JPEG_TN);
        assertEquals(header.getValue().get(Type.DLNA_ORG_CI).getValue(), DLNAConversionIndicator.TRANSCODED);
        
        assertEquals(speeds.length, 3);
        assertEquals(speeds[0].getValue(),"-1");
        assertEquals(speeds[1].getValue(),"1/2");
        assertEquals(speeds[2].getValue(),"4");
        
        assertEquals(dflags.size(), 4);
        assertEquals(dflags.contains(DLNAFlags.DLNA_V15), true);
        assertEquals(dflags.contains(DLNAFlags.CONNECTION_STALL), true);
        assertEquals(dflags.contains(DLNAFlags.RTSP_PAUSE), false);
        
        assertEquals(header.getString(), "DLNA.ORG_PN=JPEG_TN;DLNA.ORG_PS=-1,1/2,4;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=01700000000000000000000000000000");
    }

    @Test
    public void parseGetContentFeaturesHeader() {
        GetContentFeaturesHeader header = new GetContentFeaturesHeader();
        header.setString("1");
        assertEquals(header.getValue().intValue(),1);
        assertEquals(header.getString(), "1");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidGetContentFeaturesHeader() {
        GetContentFeaturesHeader header = new GetContentFeaturesHeader();
        header.setString("2");
    }
    
    @Test
    public void parseTransferModeHeader() {
        TransferModeHeader header = new TransferModeHeader();
        header.setString("Streaming");
        assertEquals(header.getValue(),TransferModeHeader.Type.Streaming);
        assertEquals(header.getString(), "Streaming");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidTransferModeHeader() {
        TransferModeHeader header = new TransferModeHeader();
        header.setString("TEST");
    }

    @Test
    public void parsePeerManagerHeader() {
        PeerManagerHeader header = new PeerManagerHeader();
        header.setString("uuid:12345678123456781234567812345678/urn:schemas-upnp-org:serviceId:ConnectionManager");
        assertEquals(header.getValue().getUdn().getIdentifierString(),"12345678123456781234567812345678");
        assertEquals(header.getValue().getServiceId().getId(),"ConnectionManager");
        assertEquals(header.getString(), "uuid:12345678123456781234567812345678/urn:schemas-upnp-org:serviceId:ConnectionManager");
    }
    
    @Test
    public void parseRealTimeInfoHeader() {
        RealTimeInfoHeader header = new RealTimeInfoHeader();
        
        header.setString("DLNA.ORG_TLAG=1.75");
        assertEquals(header.getValue().getMilliseconds(),1750);
        assertEquals(header.getString(), "DLNA.ORG_TLAG=1.750");
        
        header.setString("DLNA.ORG_TLAG=*");
        assertEquals(header.getValue(),null);
        assertEquals(header.getString(), "DLNA.ORG_TLAG=*");
        
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidRealTimeInfoHeader() {
        RealTimeInfoHeader header = new RealTimeInfoHeader();
        header.setString("DLNA.ORG_TLAG=1.7521");
    }
    
    @Test
    public void parseScmsFlagHeader() {
        ScmsFlagHeader header = new ScmsFlagHeader();
        
        header.setString("00");
        assertEquals(header.getValue().isCopyright(),true);
        assertEquals(header.getValue().isOriginal(),true);
        assertEquals(header.getString(), "00");
        
        header.setString("01");
        assertEquals(header.getValue().isCopyright(),true);
        assertEquals(header.getValue().isOriginal(),false);
        assertEquals(header.getString(), "01");
        
        header.setString("10");
        assertEquals(header.getValue().isCopyright(),false);
        assertEquals(header.getValue().isOriginal(),true);
        assertEquals(header.getString(), "10");

        header.setString("11");
        assertEquals(header.getValue().isCopyright(),false);
        assertEquals(header.getValue().isOriginal(),false);
        assertEquals(header.getString(), "11");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidScmsFlagHeader() {
        ScmsFlagHeader header = new ScmsFlagHeader();
        header.setString("2");
    }
    
    @Test
    public void parseWCTHeader() {
        WCTHeader header = new WCTHeader();
        
        header.setString("0");
        assertEquals(header.getValue().booleanValue(),false);
        assertEquals(header.getString(), "0");
        
        header.setString("1");
        assertEquals(header.getValue().booleanValue(),true);
        assertEquals(header.getString(), "1");
        
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidWCTHeader() {
        WCTHeader header = new WCTHeader();
        header.setString("2");
    }

    @Test
    public void parseMaxPrateHeader() {
        MaxPrateHeader header = new MaxPrateHeader();
        
        header.setString("120");
        assertEquals(header.getValue().longValue(),120);
        assertEquals(header.getString(), "120");
        
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidMaxPrateHeader() {
        MaxPrateHeader header = new MaxPrateHeader();
        header.setString("A");
    }
    
    @Test
    public void parseEventTypeHeader() {
        EventTypeHeader header = new EventTypeHeader();
        
        header.setString("2000");
        assertEquals(header.getValue(),"2000");
        assertEquals(header.getString(), "2000");
        
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidEventTypeHeader() {
        EventTypeHeader header = new EventTypeHeader();
        header.setString("001");
    }
    
    @Test
    public void parseSupportedHeader() {
        SupportedHeader header = new SupportedHeader();
        
        header.setString("dlna.announce, rtsp.basic");
        assertEquals(header.getValue().length,2);
        assertEquals(header.getValue()[0],"dlna.announce");
        assertEquals(header.getValue()[1],"rtsp.basic");
        assertEquals(header.getString(), "dlna.announce,rtsp.basic;");
        
        header.setString("dlna.announce  , rtsp.basic;");
        assertEquals(header.getValue().length,2);
        assertEquals(header.getValue()[0],"dlna.announce");
        assertEquals(header.getValue()[1],"rtsp.basic");
        assertEquals(header.getString(), "dlna.announce,rtsp.basic;");
    }
    
    @Test
    public void parseBufferInfoHeader() {
        BufferInfoHeader header = new BufferInfoHeader();
        
        header.setString("dejitter=65536;CDB=98302;BTM=0;TD=1000;BFR=1");
        assertEquals(header.getValue().getDejitterSize().longValue(), 65536L);
        assertEquals(header.getValue().getCdb().getSize().longValue(), 98302L);
        assertEquals(header.getValue().getCdb().getTranfer(), TransferMechanism.IMMEDIATELY);
        assertEquals(header.getValue().getTargetDuration().longValue(), 1000L);
        assertEquals(header.getValue().isFullnessReports().booleanValue(), true);
        assertEquals(header.getString(), "dejitter=65536;CDB=98302;BTM=0;TD=1000;BFR=1");
        
        header.setString("dejitter=65536;CDB=98302;BTM=0;TD=1000");
        assertEquals(header.getValue().getDejitterSize().longValue(), 65536L);
        assertEquals(header.getValue().getCdb().getSize().longValue(), 98302L);
        assertEquals(header.getValue().getCdb().getTranfer(), TransferMechanism.IMMEDIATELY);
        assertEquals(header.getValue().getTargetDuration().longValue(), 1000L);
        assertEquals(header.getValue().isFullnessReports(), null);
        assertEquals(header.getString(), "dejitter=65536;CDB=98302;BTM=0;TD=1000");
        
        header.setString("dejitter=65536;CDB=98302;BTM=0");
        assertEquals(header.getValue().getDejitterSize().longValue(), 65536L);
        assertEquals(header.getValue().getCdb().getSize().longValue(), 98302L);
        assertEquals(header.getValue().getCdb().getTranfer(), TransferMechanism.IMMEDIATELY);
        assertEquals(header.getValue().getTargetDuration(), null);
        assertEquals(header.getValue().isFullnessReports(), null);
        assertEquals(header.getString(), "dejitter=65536;CDB=98302;BTM=0");
        
        header.setString("dejitter=65536");
        assertEquals(header.getValue().getDejitterSize().longValue(), 65536L);
        assertEquals(header.getValue().getCdb(), null);
        assertEquals(header.getValue().getTargetDuration(), null);
        assertEquals(header.getValue().isFullnessReports(), null);
        assertEquals(header.getString(), "dejitter=65536");
    }
    
    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidBufferInfoHeader() {
        BufferInfoHeader header = new BufferInfoHeader();
        header.setString("dejitter=65536;CDB=98302");
    }
}
