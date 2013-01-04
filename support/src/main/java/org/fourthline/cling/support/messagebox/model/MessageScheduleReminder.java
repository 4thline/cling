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