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
import org.fourthline.cling.support.messagebox.model.MessageSMS;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Christian Bauer
 */
public class MessageSMSView extends MessageView {

    final private DateTimeField dateTimeField = new DateTimeField();
    final private NumberNameField receiverNumberNameField = new NumberNameField();
    final private NumberNameField senderNumberNameField= new NumberNameField();
    final private JTextArea bodyTextArea = new JTextArea(5, 25);

    public MessageSMSView() {
        super();

        getForm().addLabelAndLastField("Date & Time:", dateTimeField, this);
        getForm().addLabelAndLastField("Receiver Number & Name:", receiverNumberNameField, this);
        getForm().addLabelAndLastField("Sender Number & Name:", senderNumberNameField, this);
        JScrollPane bodyTextAreaScrollPane = new JScrollPane(bodyTextArea);
        getForm().addLabelAndLastField("Message:", bodyTextAreaScrollPane, this);
    }

    @Override
    protected Message createMessage() {
        return new MessageSMS(
                getDisplayMaximumCheckBox().isSelected() ? Message.DisplayType.MAXIMUM : Message.DisplayType.MINIMUM,
                dateTimeField.getDateTime(),
                receiverNumberNameField.getNumberName(),
                senderNumberNameField.getNumberName(),
                bodyTextArea.getText()
        );
    }
}
