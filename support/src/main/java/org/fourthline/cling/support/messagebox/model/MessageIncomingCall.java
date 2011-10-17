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