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

package org.fourthline.cling.support.messagebox.model;

import org.fourthline.cling.support.messagebox.parser.MessageElement;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Christian Bauer
 */
public class DateTime implements ElementAppender {

    final private String date;
    final private String time;

    public DateTime() {
        this(getCurrentDate(), getCurrentTime());
    }

    public DateTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void appendMessageElements(MessageElement parent) {
        parent.createChild("Date").setContent(getDate());
        parent.createChild("Time").setContent(getTime());
    }

    public static String getCurrentDate() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        return fmt.format(new Date());
    }

}
