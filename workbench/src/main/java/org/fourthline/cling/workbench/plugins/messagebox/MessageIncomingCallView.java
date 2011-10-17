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

package org.fourthline.cling.workbench.plugins.messagebox;

import org.fourthline.cling.support.messagebox.model.Message;
import org.fourthline.cling.support.messagebox.model.MessageIncomingCall;

/**
 * @author Christian Bauer
 */
public class MessageIncomingCallView extends MessageView {

    final private DateTimeField dateTimeField = new DateTimeField();
    final private NumberNameField calleeNumberNameField = new NumberNameField();
    final private NumberNameField callerNumberNameField = new NumberNameField();

    public MessageIncomingCallView() {
        super();

        getForm().addLabelAndLastField("Date & Time:", dateTimeField, this);
        getForm().addLabelAndLastField("Callee Number & Name:", calleeNumberNameField, this);
        getForm().addLabelAndLastField("Caller Number & Name:", callerNumberNameField, this);
    }

    @Override
    protected Message createMessage() {
        return new MessageIncomingCall(
                getDisplayMaximumCheckBox().isSelected() ? Message.DisplayType.MAXIMUM : Message.DisplayType.MINIMUM,
                dateTimeField.getDateTime(),
                calleeNumberNameField.getNumberName(),
                callerNumberNameField.getNumberName()
        );

    }
}