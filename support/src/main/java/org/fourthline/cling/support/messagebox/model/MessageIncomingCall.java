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
public class MessageIncomingCall extends Message {

    final private DateTime callTime;
    final private NumberName callee;
    final private NumberName caller;

    public MessageIncomingCall(NumberName callee, NumberName caller) {
        this(new DateTime(), callee, caller);
    }

    public MessageIncomingCall(DateTime callTime, NumberName callee, NumberName caller) {
        this(DisplayType.MAXIMUM, callTime, callee, caller);
    }

    public MessageIncomingCall(DisplayType displayType, DateTime callTime, NumberName callee, NumberName caller) {
        super(Category.INCOMING_CALL, displayType);
        this.callTime = callTime;
        this.callee = callee;
        this.caller = caller;
    }

    public DateTime getCallTime() {
        return callTime;
    }

    public NumberName getCallee() {
        return callee;
    }

    public NumberName getCaller() {
        return caller;
    }

    public void appendMessageElements(MessageElement parent) {
        getCallTime().appendMessageElements(parent.createChild("CallTime"));
        getCallee().appendMessageElements(parent.createChild("Callee"));
        getCaller().appendMessageElements(parent.createChild("Caller"));
    }

}