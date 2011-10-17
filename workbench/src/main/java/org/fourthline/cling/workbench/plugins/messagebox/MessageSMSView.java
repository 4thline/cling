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
