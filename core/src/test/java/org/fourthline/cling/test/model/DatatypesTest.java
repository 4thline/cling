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

package org.fourthline.cling.test.model;

import org.fourthline.cling.model.types.Base64Datatype;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.DateTimeDatatype;
import org.fourthline.cling.model.types.DoubleDatatype;
import org.fourthline.cling.model.types.FloatDatatype;
import org.fourthline.cling.model.types.IntegerDatatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytesDatatype;
import org.fourthline.cling.model.types.UnsignedIntegerOneByteDatatype;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytesDatatype;
import org.fourthline.cling.model.types.csv.CSVBoolean;
import org.fourthline.cling.model.types.csv.CSVString;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;


public class DatatypesTest {

    @Test
    public void upperLowerCase() {
        // Broken devices do this
        assertEquals(Datatype.Builtin.getByDescriptorName("String"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("strinG"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("STRING"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("string"), Datatype.Builtin.STRING);
    }

    @Test
    public void validUnsignedIntegers() {

        UnsignedIntegerOneByteDatatype typeOne = new UnsignedIntegerOneByteDatatype();
        assertEquals(typeOne.valueOf("123").getValue(), Long.valueOf(123l));

        UnsignedIntegerTwoBytesDatatype typeTwo = new UnsignedIntegerTwoBytesDatatype();
        assertEquals(typeTwo.valueOf("257").getValue(), Long.valueOf(257l));

        UnsignedIntegerFourBytesDatatype typeFour = new UnsignedIntegerFourBytesDatatype();
        assertEquals(typeFour.valueOf("65536").getValue(), Long.valueOf(65536l));
        assertEquals(typeFour.valueOf("4294967295").getValue(), Long.valueOf(4294967295l));

        // Well, no need to write another test for that
        assertEquals(typeFour.valueOf("4294967295").increment(true).getValue(), Long.valueOf(1));

    }

    @Test(expectedExceptions = InvalidValueException.class)
    public void invalidUnsignedIntegersOne() {
        UnsignedIntegerOneByteDatatype typeOne = new UnsignedIntegerOneByteDatatype();
        typeOne.valueOf("256");
    }

    @Test(expectedExceptions = InvalidValueException.class)
    public void invalidUnsignedIntegersTwo() {
        UnsignedIntegerTwoBytesDatatype typeTwo = new UnsignedIntegerTwoBytesDatatype();
        typeTwo.valueOf("65536");
    }

    @Test
    public void signedIntegers() {

        IntegerDatatype type = new IntegerDatatype(1);
        assert type.isValid(123);
        assert type.isValid(-124);
        assert type.valueOf("123") == 123;
        assert type.valueOf("-124") == -124;
        assert !type.isValid(256);

        type = new IntegerDatatype(2);
        assert type.isValid(257);
        assert type.isValid(-257);
        assert type.valueOf("257") == 257;
        assert type.valueOf("-257") == -257;
        assert !type.isValid(32768);

    }

    @Test
    public void dateAndTime() {
        DateTimeDatatype type = (DateTimeDatatype) Datatype.Builtin.DATE.getDatatype();

        Calendar expexted = Calendar.getInstance();
        expexted.set(Calendar.YEAR, 2010);
        expexted.set(Calendar.MONTH, 10);
        expexted.set(Calendar.DAY_OF_MONTH, 3);
        expexted.set(Calendar.HOUR_OF_DAY, 8);
        expexted.set(Calendar.MINUTE, 9);
        expexted.set(Calendar.SECOND, 10);

        Calendar parsedDate = type.valueOf("2010-11-03");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));
        assertEquals(type.getString(expexted), "2010-11-03");

        type = (DateTimeDatatype) Datatype.Builtin.DATETIME.getDatatype();

        parsedDate = type.valueOf("2010-11-03");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));

        parsedDate = type.valueOf("2010-11-03T08:09:10");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expexted.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expexted.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expexted.get(Calendar.SECOND));

        assertEquals(type.getString(expexted), "2010-11-03T08:09:10");
    }

    @Test
    public void dateAndTimeWithZone() {
        DateTimeDatatype type =
                new DateTimeDatatype(
                        new String[]{"yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ"},
                        "yyyy-MM-dd'T'HH:mm:ssZ"
                ) {
                    @Override
                    protected TimeZone getTimeZone() {
                        // Set the "local" timezone to CET for the test
                        return TimeZone.getTimeZone("CET");
                    }
                };

        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getTimeZone("CET"));
        expected.set(Calendar.YEAR, 2010);
        expected.set(Calendar.MONTH, 10);
        expected.set(Calendar.DAY_OF_MONTH, 3);
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        Calendar parsedDate = type.valueOf("2010-11-03T08:09:10");
        assertEquals(parsedDate.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expected.get(Calendar.SECOND));

        parsedDate = type.valueOf("2010-11-03T08:09:10+0100");
        assertEquals(parsedDate.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expected.get(Calendar.SECOND));
        assertEquals(parsedDate.getTimeZone(), expected.getTimeZone());

        assertEquals(type.getString(expected), "2010-11-03T08:09:10+0100");
    }

    @Test
    public void time() {
        DateTimeDatatype type = (DateTimeDatatype) Datatype.Builtin.TIME.getDatatype();

        Calendar expected = Calendar.getInstance();
        expected.setTime(new Date(0));
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        Calendar parsedTime = type.valueOf("08:09:10");
        assertEquals(parsedTime.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedTime.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedTime.get(Calendar.SECOND), expected.get(Calendar.SECOND));
        assertEquals(type.getString(expected), "08:09:10");
    }

    @Test
    public void timeWithZone() {

        DateTimeDatatype type = new DateTimeDatatype(new String[]{"HH:mm:ssZ", "HH:mm:ss"}, "HH:mm:ssZ") {
            @Override
            protected TimeZone getTimeZone() {
                // Set the "local" timezone to CET for the test
                return TimeZone.getTimeZone("CET");
            }
        };

        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getTimeZone("CET"));
        expected.setTime(new Date(0));
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        assertEquals(type.valueOf("08:09:10").getTimeInMillis(), expected.getTimeInMillis());
        assertEquals(type.valueOf("08:09:10+0100").getTimeInMillis(), expected.getTimeInMillis());
        assertEquals(type.getString(expected), "08:09:10+0100");

    }

    @Test
    public void base64() {
        Base64Datatype type = (Base64Datatype) Datatype.Builtin.BIN_BASE64.getDatatype();
        assert Arrays.equals(type.valueOf("a1b2"), new byte[]{107, 86, -10});
        assert type.getString(new byte[]{107, 86, -10}).equals("a1b2");
    }

    @Test
    public void simpleCSV() {
        List<String> csv = new CSVString("foo,bar,baz");
        assert csv.size() == 3;
        assert csv.get(0).equals("foo");
        assert csv.get(1).equals("bar");
        assert csv.get(2).equals("baz");
        assert csv.toString().equals("foo,bar,baz");

        csv = new CSVString("f\\\\oo,b\\,ar,b\\\\az");
        assert csv.size() == 3;
        assertEquals(csv.get(0), "f\\oo");
        assertEquals(csv.get(1), "b,ar");
        assertEquals(csv.get(2), "b\\az");

        List<Boolean> csvBoolean = new CSVBoolean("1,1,0");
        assert csvBoolean.size() == 3;
        assertEquals(csvBoolean.get(0), new Boolean(true));
        assertEquals(csvBoolean.get(1), new Boolean(true));
        assertEquals(csvBoolean.get(2), new Boolean(false));
        assertEquals(csvBoolean.toString(), "1,1,0");
    }

    @Test
    public void parseDLNADoc() {
        DLNADoc doc = DLNADoc.valueOf("DMS-1.50");
        assertEquals(doc.getDevClass(), "DMS");
        assertEquals(doc.getVersion(), DLNADoc.Version.V1_5.toString());
        assertEquals(doc.toString(), "DMS-1.50");

        doc = DLNADoc.valueOf("M-DMS-1.50");
        assertEquals(doc.getDevClass(), "M-DMS");
        assertEquals(doc.getVersion(), DLNADoc.Version.V1_5.toString());
        assertEquals(doc.toString(), "M-DMS-1.50");
    }

    @Test
    public void caseSensitivity() {
        Datatype.Builtin dt = Datatype.Builtin.getByDescriptorName("datetime");
        assert dt != null;
        dt = Datatype.Builtin.getByDescriptorName("dateTime");
        assert dt != null;
        dt = Datatype.Builtin.getByDescriptorName("DATETIME");
        assert dt != null;
    }

    @Test
    public void valueOfDouble() {
        DoubleDatatype dt = (DoubleDatatype)Datatype.Builtin.R8.getDatatype();
        Double d = dt.valueOf("1.23");
        assertEquals(d, 1.23d);
    }

    @Test
    public void valueOfFloat() {
        FloatDatatype dt = (FloatDatatype)Datatype.Builtin.R4.getDatatype();
        Float f = dt.valueOf("1.23456");
        assertEquals(f, 1.23456f);
    }

}
