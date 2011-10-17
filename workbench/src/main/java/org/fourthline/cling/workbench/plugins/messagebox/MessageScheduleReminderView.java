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