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

/**
 *
 */
public enum Protocol {

    ALL(ProtocolInfo.WILDCARD),
    HTTP_GET("http-get"),
    RTSP_RTP_UDP("rtsp-rtp-udp"),
    INTERNAL("internal"),
    IEC61883("iec61883");

    private String protocolString;

    Protocol(String protocolString) {
        this.protocolString = protocolString;
    }

    @Override
    public String toString() {
        return protocolString;
    }

    public static Protocol valueOrNullOf(String s) {
        for (Protocol protocol : values()) {
            if (protocol.toString().equals(s)) {
                return protocol;
            }
        }
        return null;
    }

}
