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
import org.fourthline.cling.support.messagebox.model.MessageScheduleReminder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Christian Bauer
 */
public class MessageScheduleReminderView extends MessageView {

    final private DateTimeField startTimeField = new DateTimeField();
    final private DateTimeField endTimeField = new DateTimeField();
    final private NumberNameField ownerNumberNameField = new NumberNameField();
    final private JTextField subjectField = new JTextField();
    final private JTextField locationField = new JTextField();
    final private JTextArea bodyTextArea = new JTextArea(5, 25);

    public MessageScheduleReminderView() {
        super();

        getForm().addLabelAndLastField("Start Time:", startTimeField, this);
        getForm().addLabelAndLastField("End Time:", endTimeField, this);
        getForm().addLabelAndLastField("Owner Number & Name:", ownerNumberNameField, this);
        getForm().addLabelAndLastField("Subject:", subjectField, this);
        getForm().addLabelAndLastField("Location:", locationField, this);
        JScrollPane bodyTextAreaScrollPane = new JScrollPane(bodyTextArea);
        getForm().addLabelAndLastField("Message:", bodyTextAreaScrollPane, this);
    }

    @Override
    protected Message createMessage() {
        return new MessageScheduleReminder(
                getDisplayMaximumCheckBox().isSelected() ? Message.DisplayType.MAXIMUM : Message.DisplayType.MINIMUM,
                startTimeField.getDateTime(),
                ownerNumberNameField.getNumberName(),
                subjectField.getText(),
                endTimeField.getDateTime(),
                locationField.getText(),
                bodyTextArea.getText()
        );
    }
}