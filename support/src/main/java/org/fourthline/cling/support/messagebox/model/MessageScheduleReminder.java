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

/**
 * @author Christian Bauer
 */
public class MessageScheduleReminder extends Message {

    final private DateTime startTime;
    final private NumberName owner;
    final private String subject;
    final private DateTime endTime;
    final private String location;
    final private String body;

    public MessageScheduleReminder(DateTime startTime, NumberName owner, String subject,
                                   DateTime endTime, String location, String body) {
        this(DisplayType.MAXIMUM, startTime, owner, subject, endTime, location, body);
    }

    public MessageScheduleReminder(DisplayType displayType, DateTime startTime, NumberName owner, String subject,
                                   DateTime endTime, String location, String body) {
        super(Category.SCHEDULE_REMINDER, displayType);
        this.startTime = startTime;
        this.owner = owner;
        this.subject = subject;
        this.endTime = endTime;
        this.location = location;
        this.body = body;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public NumberName getOwner() {
        return owner;
    }

    public String getSubject() {
        return subject;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public String getBody() {
        return body;
    }

    public void appendMessageElements(MessageElement parent) {
        getStartTime().appendMessageElements(parent.createChild("StartTime"));
        getOwner().appendMessageElements(parent.createChild("Owner"));
        parent.createChild("Subject").setContent(getSubject());
        getEndTime().appendMessageElements(parent.createChild("EndTime"));
        parent.createChild("Location").setContent(getLocation());
        parent.createChild("Body").setContent(getBody());
    }

}