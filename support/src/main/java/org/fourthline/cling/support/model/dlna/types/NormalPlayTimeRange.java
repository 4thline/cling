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

import org.fourthline.cling.model.types.InvalidValueException;

/**
 *
 * @author Mario Franco
 */
public class NormalPlayTimeRange {

    public static final String PREFIX = "npt=";
    
    private NormalPlayTime timeStart;
    private NormalPlayTime timeEnd;
    private NormalPlayTime timeDuration;

    public NormalPlayTimeRange(long timeStart, long timeEnd) {
        this.timeStart = new NormalPlayTime(timeStart);
        this.timeEnd = new NormalPlayTime(timeEnd);
    }

    public NormalPlayTimeRange(NormalPlayTime timeStart, NormalPlayTime timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public NormalPlayTimeRange(NormalPlayTime timeStart, NormalPlayTime timeEnd, NormalPlayTime timeDuration) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.timeDuration = timeDuration;
    }

    /**
     * @return the timeStart
     */
    public NormalPlayTime getTimeStart() {
        return timeStart;
    }

    /**
     * @return the timeEnd
     */
    public NormalPlayTime getTimeEnd() {
        return timeEnd;
    }

    /**
     * @return the timeDuration
     */
    public NormalPlayTime getTimeDuration() {
        return timeDuration;
    }

    /**
     * 
     * @return String format of Normal Play Time Range for response message header 
     */
    public String getString() {
        return getString(true);
    }

    /**
     * 
     * @return String format of Normal Play Time Range for response message header 
     */
    public String getString(boolean includeDuration) {
        String s = PREFIX;

        s += timeStart.getString() + "-";
        if (timeEnd != null) {
            s += timeEnd.getString();
        }
        if (includeDuration) {
            s += "/" + (timeDuration != null ? timeDuration.getString() : "*");
        }

        return s;
    }

    public static NormalPlayTimeRange valueOf(String s) throws InvalidValueException {
        return valueOf(s, false);
    }
    
    public static NormalPlayTimeRange valueOf(String s, boolean mandatoryTimeEnd) throws InvalidValueException {
        if (s.startsWith(PREFIX)) {
            NormalPlayTime timeStart, timeEnd = null, timeDuration = null;
            String[] params = s.substring(PREFIX.length()).split("[-/]");
            switch (params.length) {
                case 3:
                    if (params[2].length() != 0 && !params[2].equals("*")) {
                        timeDuration = NormalPlayTime.valueOf(params[2]);
                    }
                case 2:
                    if (params[1].length() != 0) {
                        timeEnd = NormalPlayTime.valueOf(params[1]);
                    }
                case 1:
                    if (params[0].length() != 0 && (!mandatoryTimeEnd || ( mandatoryTimeEnd && params.length>1))) {
                        timeStart = NormalPlayTime.valueOf(params[0]);
                        return new NormalPlayTimeRange(timeStart, timeEnd, timeDuration);
                    }
                default:
                    break;
            }
        }
        throw new InvalidValueException("Can't parse NormalPlayTimeRange: " + s);
    }
}
