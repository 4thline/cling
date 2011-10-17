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

package org.fourthline.cling.osgi.basedriver.util;

import org.apache.commons.codec.binary.Base64;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.Datatype.Builtin;
import org.fourthline.cling.model.types.UnsignedVariableInteger;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Bruce Green
 */
public class OSGiDataConverter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat dateTimeTZFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat timeTZFormat = new SimpleDateFormat("HH:mm:ssZ");

    /*
	 *  Integer              ui1, ui2, i1, i2, i4, int
	 *  Long                 ui4, time, time.tz
	 *  Float                r4, float
	 *  Double               r8, number, fixed.14.4
	 *  Character            char
	 *  String               string, uri, uuid
	 *  Date                 date, dateTime, dateTime.tz
	 *  Boolean              boolean
	 *  byte[]               bin.base64, bin.hex
	 */

    private static boolean isInteger(Builtin builtin) {
        return (
                builtin.equals(Builtin.UI1) ||
                        builtin.equals(Builtin.UI2) ||
                        builtin.equals(Builtin.I1) ||
                        builtin.equals(Builtin.I2) ||
                        builtin.equals(Builtin.I4) ||
                        builtin.equals(Builtin.INT)
        );
    }

    private static boolean isLong(Builtin builtin) {
        return (
                builtin.equals(Builtin.UI4) ||
                        builtin.equals(Builtin.TIME) ||
                        builtin.equals(Builtin.TIME_TZ)
        );
    }

    private static boolean isFloat(Builtin builtin) {
        return (builtin.equals(Builtin.R4) ||
                builtin.equals(Builtin.FLOAT)
        );
    }

    private static boolean isDouble(Builtin builtin) {
        return (
                builtin.equals(Builtin.R8) ||
                        builtin.equals(Builtin.NUMBER) ||
                        builtin.equals(Builtin.FIXED144)
        );
    }

    private static boolean isCharacter(Builtin builtin) {
        return (builtin.equals(Builtin.CHAR));
    }

    private static boolean isString(Builtin builtin) {
        return (
                builtin.equals(Builtin.STRING) ||
                        builtin.equals(Builtin.URI) ||
                        builtin.equals(Builtin.UUID)
        );
    }

    private static boolean isDate(Builtin builtin) {
        return (
                builtin.equals(Builtin.DATE) ||
                        builtin.equals(Builtin.DATETIME) ||
                        builtin.equals(Builtin.DATETIME_TZ)
        );
    }

    private static boolean isBoolean(Builtin builtin) {
        return (
                builtin.equals(Builtin.BOOLEAN)
        );
    }

    private static boolean isByte(Builtin builtin) {
        return (
                builtin.equals(Builtin.BIN_HEX)
        );
    }


    private static boolean isBase64(Builtin builtin) {
        return (
                builtin.equals(Builtin.BIN_BASE64)
        );
    }

    static public Object toOSGiValue(Datatype type, Object input) {
        Object output = null;

        if (isInteger(type.getBuiltin())) {
            output = toInteger(input);
        } else if (isLong(type.getBuiltin())) {
            output = toLong(input);
        } else if (isFloat(type.getBuiltin())) {
            output = toFloat(input);
        } else if (isDouble(type.getBuiltin())) {
            output = toDouble(input);
        } else if (isCharacter(type.getBuiltin())) {
            output = toCharacter(input);
        } else if (isString(type.getBuiltin())) {
            output = toString(input);
        } else if (isDate(type.getBuiltin())) {
            output = toDate(input);
        } else if (isBoolean(type.getBuiltin())) {
            output = toBoolean(input);
        } else if (isByte(type.getBuiltin())) {
            output = toByte(input);
        } else if (isBase64(type.getBuiltin())) {
            output = toBase64(input);
        }

        return output;
    }

    /*
      *  Integer              ui1, ui2, i1, i2, i4, int
      */
    private static Integer toInteger(Object input) {
        Integer output = null;

        if (input instanceof Integer) {
            output = (Integer) input;
        } else if (input instanceof UnsignedVariableInteger) {
            output = ((UnsignedVariableInteger) input).getValue().intValue();
        }

        return output;
    }

    static long toMilliseconds(int hours, int mins, int secs) {
        return (secs + (mins * 60) + (hours * (60 * 60))) * 1000;
    }

    /*
      *  Long                 ui4, time, time.tz
      */
    private static Long toLong(Object input) {
        Long output = null;

        if (input instanceof Long) {
            output = (Long) input;
        } else if (input instanceof UnsignedVariableInteger) {
            output = (long) ((UnsignedVariableInteger) input).getValue().intValue();
        } else if (input instanceof Calendar) {
            Calendar calendar = (Calendar) input;
            output = toMilliseconds(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
            );

        }

        return output;
    }

    /*
      *  Float                r4, float
      */
    private static Float toFloat(Object input) {
        Float output = null;

        if (input instanceof Float) {
            output = (Float) input;
        } else if (input instanceof Double) {
            output = ((Double) input).floatValue();
        }

        return output;
    }

    /*
      *  Double               r8, number, fixed.14.4
      */
    private static Double toDouble(Object input) {
        Double output = null;

        if (input instanceof Double) {
            output = (Double) input;
        }

        return output;
    }

    /*
      *  Character            char
      */
    private static Character toCharacter(Object input) {
        Character output = null;

        if (input instanceof Character) {
            output = (Character) input;
        }

        return output;
    }

    /*
      *  String               string, uri, uuid
      */
    private static String toString(Object input) {
        String output = null;

        if (input == null) {
            output = "";
        } else if (input instanceof String) {
            output = (String) input;
        } else if (input instanceof URI) {
            output = String.valueOf(input.toString());
        }

        return output;
    }

    /*
      *  Date                 date, dateTime, dateTime.tz
      */
    private static Date toDate(Object input) {
        Date output = null;

        if (input instanceof Date) {
            output = (Date) input;
        } else if (input instanceof Calendar) {
            output = new Date(((Calendar) input).getTimeInMillis());
        }

        return output;
    }

    /*
      *  Boolean              boolean
      */
    private static Boolean toBoolean(Object input) {
        Boolean output = null;

        if (input instanceof Boolean) {
            output = (Boolean) input;
        }

        return output;
    }

    /*
      *  byte[]               bin.base64, bin.hex
      */
    private static byte[] toByte(Object input) {
        byte[] output = null;

        if (input == null) {
            output = new byte[0];
        } else {
            if (input instanceof Byte[]) {
                Byte[] Bytes = (Byte[]) input;
                byte[] bytes = new byte[Bytes.length];
                for (int i = 0; i < Bytes.length; i++) {
                    bytes[i] = Bytes[i];
                }
                output = bytes;
            }
        }

        return output;
    }

    private static byte[] toBase64(Object input) {
        byte[] output = null;

        if (input == null) {
            output = toByte(input);
        } else if (input instanceof Byte[]) {
            output = toByte(input);
        }

        if (output != null) {
            output = Base64.decodeBase64(output);
        }

        return output;
    }


    static public Object toClingValue(String type, Object value) {
        if (value instanceof Date) {
            Date date = (Date) value;

            if (type.equals(UPnPLocalStateVariable.TYPE_DATE)) {
                value = dateFormat.format(date);
            } else if (type.equals(UPnPLocalStateVariable.TYPE_DATETIME)) {
                value = dateTimeFormat.format(date);
            } else if (type.equals(UPnPLocalStateVariable.TYPE_DATETIME_TZ)) {
                value = dateTimeTZFormat.format(date);
            }
        } else if (value instanceof Long) {

            if (type.equals(UPnPLocalStateVariable.TYPE_TIME)) {
                int offset = TimeZone.getDefault().getOffset((Long) value);
                Date date = new Date((Long) value - offset);
                value = timeFormat.format(date);
            } else if (type.equals(UPnPLocalStateVariable.TYPE_TIME_TZ)) {
                int offset = TimeZone.getDefault().getOffset((Long) value);
                Date date = new Date((Long) value - offset);
                value = timeTZFormat.format(date);
            } else {
                value = value.toString();
            }
        } else if (value instanceof byte[]) {

            if (type.equals(UPnPStateVariable.TYPE_BIN_BASE64)) {
                value = Base64.encodeBase64((byte[]) value);
            }

            byte[] bytes = ((byte[]) value);
            Byte[] Bytes = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                Bytes[i] = bytes[i];
            }
            value = Bytes;
        } else {
            value = value.toString();
        }

        return value;
    }
}
