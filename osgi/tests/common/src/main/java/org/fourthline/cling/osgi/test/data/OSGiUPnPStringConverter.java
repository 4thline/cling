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

package org.fourthline.cling.osgi.test.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPStateVariable;

public class OSGiUPnPStringConverter {
	/**
	 * Unsigned 1 <code>Byte</code> int.
	 * <p>
	 * Mapped to an <code>Integer</code> object.
	 */
	static final String	TYPE_UI1			= "ui1";
	/**
	 * Unsigned 2 Byte int.
	 * <p>
	 * Mapped to <code>Integer</code> object.
	 */
	static final String	TYPE_UI2			= "ui2";
	/**
	 * Unsigned 4 Byte int.
	 * <p>
	 * Mapped to <code>Long</code> object.
	 */
	static final String	TYPE_UI4			= "ui4";
	/**
	 * 1 Byte int.
	 * <p>
	 * Mapped to <code>Integer</code> object.
	 */
	static final String	TYPE_I1				= "i1";
	/**
	 * 2 Byte int.
	 * <p>
	 * Mapped to <code>Integer</code> object.
	 */
	static final String	TYPE_I2				= "i2";
	/**
	 * 4 Byte int.
	 * <p>
	 * Must be between -2147483648 and 2147483647
	 * <p>
	 * Mapped to <code>Integer</code> object.
	 */
	static final String	TYPE_I4				= "i4";
	/**
	 * Integer number.
	 * <p>
	 * Mapped to <code>Integer</code> object.
	 */
	static final String	TYPE_INT			= "int";
	/**
	 * 4 Byte float.
	 * <p>
	 * Same format as float. Must be between 3.40282347E+38 to 1.17549435E-38.
	 * <p>
	 * Mapped to <code>Float</code> object.
	 */
	static final String	TYPE_R4				= "r4";
	/**
	 * 8 Byte float.
	 * <p>
	 * Same format as float. Must be between -1.79769313486232E308 and
	 * -4.94065645841247E-324 for negative values, and between
	 * 4.94065645841247E-324 and 1.79769313486232E308 for positive values, i.e.,
	 * IEEE 64-bit (8-Byte) double.
	 * <p>
	 * Mapped to <code>Double</code> object.
	 */
	static final String	TYPE_R8				= "r8";
	/**
	 * Same as r8.
	 * <p>
	 * Mapped to <code>Double</code> object.
	 */
	static final String	TYPE_NUMBER			= "number";
	/**
	 * Same as r8 but no more than 14 digits to the left of the decimal point
	 * and no more than 4 to the right.
	 * <p>
	 * Mapped to <code>Double</code> object.
	 */
	static final String	TYPE_FIXED_14_4		= "fixed.14.4";
	/**
	 * Floating-point number.
	 * <p>
	 * Mantissa (left of the decimal) and/or exponent may have a leading sign.
	 * Mantissa and/or exponent may have leading zeros. Decimal character in
	 * mantissa is a period, i.e., whole digits in mantissa separated from
	 * fractional digits by period. Mantissa separated from exponent by E. (No
	 * currency symbol.) (No grouping of digits in the mantissa, e.g., no
	 * commas.)
	 * <p>
	 * Mapped to <code>Float</code> object.
	 */
	static final String	TYPE_FLOAT			= "float";
	/**
	 * Unicode string.
	 * <p>
	 * One character long.
	 * <p>
	 * Mapped to <code>Character</code> object.
	 */
	static final String	TYPE_CHAR			= "char";
	/**
	 * Unicode string.
	 * <p>
	 * No limit on length.
	 * <p>
	 * Mapped to <code>String</code> object.
	 */
	static final String	TYPE_STRING			= "string";
	/**
	 * A calendar date.
	 * <p>
	 * Date in a subset of ISO 8601 format without time data.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/xmlschema-2/#date">http://www.w3.org/TR/xmlschema-2/#date
	 * </a>.
	 * <p>
	 * Mapped to <code>java.util.Date</code> object. Always 00:00 hours.
	 */
	static final String	TYPE_DATE			= "date";
	/**
	 * A specific instant of time.
	 * <p>
	 * Date in ISO 8601 format with optional time but no time zone.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/xmlschema-2/#dateTime">http://www.w3.org/TR/xmlschema-2/#dateTime
	 * </a>.
	 * <p>
	 * Mapped to <code>java.util.Date</code> object using default time zone.
	 */
	static final String	TYPE_DATETIME		= "dateTime";
	/**
	 * A specific instant of time.
	 * <p>
	 * Date in ISO 8601 format with optional time and optional time zone.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/xmlschema-2/#dateTime">http://www.w3.org/TR/xmlschema-2/#dateTime
	 * </a>.
	 * <p>
	 * Mapped to <code>java.util.Date</code> object adjusted to default time zone.
	 */
	static final String	TYPE_DATETIME_TZ	= "dateTime.tz";
	/**
	 * An instant of time that recurs every day.
	 * <p>
	 * Time in a subset of ISO 8601 format with no date and no time zone.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/xmlschema-2/#dateTime">http://www.w3.org/TR/xmlschema-2/#time
	 * </a>.
	 * <p>
	 * Mapped to <code>Long</code>. Converted to milliseconds since midnight.
	 */
	static final String	TYPE_TIME			= "time";
	/**
	 * An instant of time that recurs every day.
	 * <p>
	 * Time in a subset of ISO 8601 format with optional time zone but no date.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/TR/xmlschema-2/#dateTime">http://www.w3.org/TR/xmlschema-2/#time
	 * </a>.
	 * <p>
	 * Mapped to <code>Long</code> object. Converted to milliseconds since
	 * midnight and adjusted to default time zone, wrapping at 0 and
	 * 24*60*60*1000.
	 */
	static final String	TYPE_TIME_TZ		= "time.tz";
	/**
	 * True or false.
	 * <p>
	 * Mapped to <code>Boolean</code> object.
	 */
	static final String	TYPE_BOOLEAN		= "boolean";
	/**
	 * MIME-style Base64 encoded binary BLOB.
	 * <p>
	 * Takes 3 Bytes, splits them into 4 parts, and maps each 6 bit piece to an
	 * octet. (3 octets are encoded as 4.) No limit on size.
	 * <p>
	 * Mapped to <code>byte[]</code> object. The Java byte array will hold the
	 * decoded content of the BLOB.
	 */
	static final String	TYPE_BIN_BASE64		= "bin.base64";
	/**
	 * Hexadecimal digits representing octets.
	 * <p>
	 * Treats each nibble as a hex digit and encodes as a separate Byte. (1
	 * octet is encoded as 2.) No limit on size.
	 * <p>
	 * Mapped to <code>byte[]</code> object. The Java byte array will hold the
	 * decoded content of the BLOB.
	 */
	static final String	TYPE_BIN_HEX		= "bin.hex";
	/**
	 * Universal Resource Identifier.
	 * <p>
	 * Mapped to <code>String</code> object.
	 */
	static final String	TYPE_URI			= "uri";
	/**
	 * Universally Unique ID.
	 * <p>
	 * Hexadecimal digits representing octets. Optional embedded hyphens are
	 * ignored.
	 * <p>
	 * Mapped to <code>String</code> object.
	 */
	static final String	TYPE_UUID			= "uuid";

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
	
    private static boolean isInteger(String type) {
        return (
        		type.equals(TYPE_UI1)	||
        		type.equals(TYPE_UI2) ||
                type.equals(TYPE_I1)	||
                type.equals(TYPE_I2)	||
                type.equals(TYPE_I4)	||
                type.equals(TYPE_INT)
                );
    }

    private static boolean isLong(String type) {
        return (
        		type.equals(TYPE_UI4)		||
        		type.equals(TYPE_TIME)	||
                type.equals(TYPE_TIME_TZ)
                );
    }

    private static boolean isFloat(String type) {
        return (type.equals(TYPE_R4)		||
        		type.equals(TYPE_FLOAT)
                );
    }

    private static boolean isDouble(String type) {
        return (
        		type.equals(TYPE_R8)		||
        		type.equals(TYPE_NUMBER)	||
                type.equals(TYPE_FIXED_14_4)
                );
    }

    private static boolean isCharacter(String type) {
        return (type.equals(TYPE_CHAR));
    }
    
    private static boolean isString(String type) {
        return (
        		type.equals(TYPE_STRING)	||
        		type.equals(TYPE_URI)		||
                type.equals(TYPE_UUID)
                );
    }

    private static boolean isDate(String type) {
        return (
        		type.equals(TYPE_DATE)		||
        		type.equals(TYPE_DATETIME)	||
                type.equals(TYPE_DATETIME_TZ)
                );
    }

    private static boolean isBoolean(String type) {
        return (
        		type.equals(TYPE_BOOLEAN)
        		);
    }
    
    private static boolean isByte(String type) {
        return (
        		type.equals(TYPE_BIN_BASE64)	||
                type.equals(TYPE_BIN_HEX)
                );
    }

	static public Object toOSGiUPnPValue(String type, String string, Object value) {
		Object output = null;
		
		if (isInteger(type)) {
			output = toInteger(string, (Integer) value);
		}
		else if (isLong(type)) {
			output = toLong(type, string, (Long) value);
		}
		else if (isFloat(type)) {
			output = toFloat(string, (Float) value);
		}
		else if (isDouble(type)) {
			output = toDouble(string, (Double) value);
		}
		else if (isCharacter(type)) {
			output = toCharacter(string, (Character) value);
		}
		else if (isString(type)) {
			output = toString(string, (String) value);
		}
		else if (isDate(type)) {
			output = toDate(type, string, (Date) value);
		}
		else if (isBoolean(type)) {
			output = toBoolean(string, (Boolean) value);
		}
		else if (isByte(type)) {
			output = toByte(type, string, (byte[]) value);
		}

		if (output == null) {
			output = value;
		}
		
		return output;
	}

	static public Object toOSGiUPnPValue(String type, String string) {
		return toOSGiUPnPValue(type, string, null);
	}

	/*
	 *  Integer              ui1, ui2, i1, i2, i4, int
	 */
	private static Integer toInteger(String string, Integer value) {
		return string != null ? Integer.valueOf(string) : value != null ? value : new Integer(0);
	}

    private static long toMilliseconds(int hours, int mins, int secs) {
    	return (secs + (mins * 60) + (hours * (60 * 60))) * 1000;
    }

    /*
	 *  Long                 ui4, time, time.tz
	 */
	private static Long toLong(String type, String string, Long object) {
		Long value = null;
		
		if (string != null) {
			try {
				if (type.equals(TYPE_TIME)) {
			    	Date date = timeFormat.parse(string);
			    	
			    	Calendar calendar = new GregorianCalendar();
			    	calendar.setTime(date);
			    	value = Long.valueOf(
			    			toMilliseconds(
			    					calendar.get(Calendar.HOUR_OF_DAY), 
			    					calendar.get(Calendar.MINUTE), 
			    					calendar.get(Calendar.SECOND)
			    					)
			    				);
				}
				else if (type.equals(TYPE_TIME_TZ)) {
			    	Date date = timeTZFormat.parse(string);
			    	
			    	Calendar calendar = new GregorianCalendar();
			    	calendar.setTime(date);
			    	value = Long.valueOf(
			    			toMilliseconds(
			    					calendar.get(Calendar.HOUR_OF_DAY), 
			    					calendar.get(Calendar.MINUTE), 
			    					calendar.get(Calendar.SECOND)
			    					)
			    				);
				}
				else {
					value = Long.valueOf(string);  
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (value == null) {
			value = object != null ? object : new Long(0);
		}
		
		return value;
	}

	/*
	 *  Float                r4, float
	 */
	private static Float toFloat(String string, Float value) {
		return string != null ?  Float.valueOf(string) : value != null ? value : new Float(0);
	}

	/*
	 *  Double               r8, number, fixed.14.4
	 */
	private static Double toDouble(String string, Double value) {
		return string != null ?  Double.valueOf(string) : value != null ? value : new Double(0);
	}

	/*
	 *  Character            char
	 */
	private static Character toCharacter(String string, Character value) {
		return string != null ?  Character.valueOf(string.charAt(0))  : value != null ? value : new Character('A');
	}

	/*
	 *  String               string, uri, uuid
	 */
	private static String toString(String string, String value) {
		return string != null ?  string : value != null ? value : new String();
	}

	/*
	 *  Date                 date, dateTime, dateTime.tz
	 */
	private static Date toDate(String type, String string, Date value) {
		Date date = null;
		
		if (string != null) {
			try {
				if (type.equals(TYPE_DATE)) {
					date = dateFormat.parse(string);
				}
				else if (type.equals(TYPE_DATETIME)) {
					date = dateTimeFormat.parse(string);
				}
				else {
					date = dateTimeTZFormat.parse(string);
				}
	
			} catch (ParseException e) {
				e.printStackTrace();
			}     
		}
		
		if (date == null) {
			date = value != null ? value : new Date(0);
		}
		
		return date;
	}
	
	/*
	 *  Boolean              boolean
	 */
	private static Boolean toBoolean(String string, Boolean value) {
		return string != null ? Boolean.valueOf(string) : value != null ? value : new Boolean(false);
	}
	
	/*
	 *  byte[]               bin.base64, bin.hex
	 */
	private static byte[] toByte(String type, String string, byte[] value) {
		return string != null ?  string.getBytes() : value != null ? value : type.getBytes();
	}


    static public Object toClingUPnPValue(String type, Object value) {
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

//            byte[] bytes = ((byte[]) value);
//            Byte[] Bytes = new Byte[bytes.length];
//            for (int i = 0; i < bytes.length; i++) {
//                Bytes[i] = bytes[i];
//            }
//            value = Bytes;
            byte[] bytes = ((byte[]) value);
            value = bytes;

        } else {
            value = value.toString();
        }

        return value;
    }

}