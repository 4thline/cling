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
package org.fourthline.cling.support.model.dlna;

/** DLNA.ORG_FLAGS, padded with 24 trailing 0s
 *
 * <pre>
 *     80000000  31  senderPaced
 *     40000000  30  lsopTimeBasedSeekSupported
 *     20000000  29  lsopByteBasedSeekSupported
 *     10000000  28  playcontainerSupported
 *      8000000  27  s0IncreasingSupported
 *      4000000  26  sNIncreasingSupported
 *      2000000  25  rtspPauseSupported
 *      1000000  24  streamingTransferModeSupported
 *       800000  23  interactiveTransferModeSupported
 *       400000  22  backgroundTransferModeSupported
 *       200000  21  connectionStallingSupported
 *       100000  20  dlnaVersion15Supported
 *
 *     Example: (1 << 24) | (1 << 22) | (1 << 21) | (1 << 20)
 *       DLNA.ORG_FLAGS=01700000[000000000000000000000000] // [] show padding
 * </pre>
 *
 * @author Mario Franco
 */
public enum DLNAFlags {

    SENDER_PACED(1 << 31),
    TIME_BASED_SEEK(1 << 30),
    BYTE_BASED_SEEK(1 << 29),
    FLAG_PLAY_CONTAINER(1 << 28),
    S0_INCREASE(1 << 27),
    SN_INCREASE(1 << 26),
    RTSP_PAUSE(1 << 25),
    STREAMING_TRANSFER_MODE(1 << 24),
    INTERACTIVE_TRANSFERT_MODE(1 << 23),
    BACKGROUND_TRANSFERT_MODE(1 << 22),
    CONNECTION_STALL(1 << 21),
    DLNA_V15(1 << 20);

    private int code;

    DLNAFlags(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DLNAFlags valueOf(int code) {
        for (DLNAFlags errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
