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
