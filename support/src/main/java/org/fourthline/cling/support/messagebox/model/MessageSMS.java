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
 * Sender and body will only be displayed if display type is set to "Maximum".
 *
 * @author Christian Bauer
 */
public class MessageSMS extends Message {

    final private DateTime receiveTime;
    final private NumberName receiver;
    final private NumberName sender;
    final private String body;

    public MessageSMS(NumberName receiver, NumberName sender, String body) {
        this(new DateTime(), receiver, sender, body);
    }

    public MessageSMS(DateTime receiveTime, NumberName receiver, NumberName sender, String body) {
        this(Message.DisplayType.MAXIMUM, receiveTime, receiver, sender, body);
    }

    public MessageSMS(DisplayType displayType, DateTime receiveTime, NumberName receiver, NumberName sender, String body) {
        super(Message.Category.SMS, displayType);
        this.receiveTime = receiveTime;
        this.receiver = receiver;
        this.sender = sender;
        this.body = body;
    }

    public DateTime getReceiveTime() {
        return receiveTime;
    }

    public NumberName getReceiver() {
        return receiver;
    }

    public NumberName getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    public void appendMessageElements(MessageElement parent) {
        getReceiveTime().appendMessageElements(parent.createChild("ReceiveTime"));
        getReceiver().appendMessageElements(parent.createChild("Receiver"));
        getSender().appendMessageElements(parent.createChild("Sender"));
        parent.createChild("Body").setContent(getBody());
    }
    
}
