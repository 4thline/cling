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
package org.fourthline.cling.support.model.dlna.types;

/**
 *
 * @author Mario Franco
 */
public class CodedDataBuffer {
    
    public enum TransferMechanism {
        IMMEDIATELY,
        TIMESTAMP,
        OTHER;
    }
    
    private Long size;
    private TransferMechanism tranfer;

    public CodedDataBuffer(Long size, TransferMechanism transfer) {
        this.size = size;
        this.tranfer = transfer;
    }
    
    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @return the tranfer
     */
    public TransferMechanism getTranfer() {
        return tranfer;
    }
}
