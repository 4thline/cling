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