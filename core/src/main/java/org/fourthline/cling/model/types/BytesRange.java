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
package org.fourthline.cling.model.types;

/**
 *
 * @author Mario Franco
 */
public class BytesRange {

    public static final String PREFIX = "bytes=";
    
    private Long firstByte;
    private Long lastByte;
    private Long byteLength;

    public BytesRange(Long firstByte, Long lastByte) {
        this.firstByte = firstByte;
        this.lastByte = lastByte;
        this.byteLength = null;
    }

    public BytesRange(Long firstByte, Long lastByte, Long byteLength) {
        this.firstByte = firstByte;
        this.lastByte = lastByte;
        this.byteLength = byteLength;
    }

    /**
     * @return the firstByte
     */
    public Long getFirstByte() {
        return firstByte;
    }

    /**
     * @return the lastByte
     */
    public Long getLastByte() {
        return lastByte;
    }

    /**
     * @return the byteLength
     */
    public Long getByteLength() {
        return byteLength;
    }

    /**
     * 
     * @return String format of Bytes Range for response message header 
     */
    public String getString() {
        return getString(false,null);
    }

    /**
     * 
     * @return String format of Bytes Range for response message header 
     */
    public String getString(boolean includeDuration) {
        return getString(includeDuration,null);
    }
    
    /**
     * 
     * @return String format of Bytes Range for response message header 
     */
    public String getString(boolean includeDuration, String rangePrefix) {
        String s = (rangePrefix!=null)?rangePrefix:PREFIX;

        if (firstByte!=null)
            s += firstByte.toString();
        s += "-";
        if (lastByte!=null)
            s+= lastByte.toString();
        if (includeDuration) {
            s += "/" + (byteLength != null ? byteLength.toString() : "*");
        }

        return s;
    }

    public static BytesRange valueOf(String s) throws InvalidValueException {
        return valueOf(s,null);
    }
    
    public static BytesRange valueOf(String s, String rangePrefix) throws InvalidValueException {
        if (s.startsWith((rangePrefix!=null)?rangePrefix:PREFIX)) {
            Long firstByte=null, lastByte = null, byteLength = null;
            String[] params = s.substring( ((rangePrefix!=null)?rangePrefix:PREFIX).length()).split("[-/]");
            switch (params.length) {
                case 3:
                    if (params[2].length() != 0 && !params[2].equals("*")) {
                        byteLength = Long.parseLong(params[2]);
                    }
                case 2:
                    if (params[1].length() != 0) {
                        lastByte = Long.parseLong(params[1]);
                    }
                case 1:
                    if (params[0].length() != 0) {
                        firstByte = Long.parseLong(params[0]);
                    }
                    if (firstByte!=null || lastByte!= null)
                        return new BytesRange(firstByte, lastByte, byteLength);
                default:
                    break;
            }
        }

        throw new InvalidValueException("Can't parse Bytes Range: " + s);
    }
}
