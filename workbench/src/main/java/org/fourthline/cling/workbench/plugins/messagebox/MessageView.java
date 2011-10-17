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
import org.seamless.swing.Form;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;

/**
 * @author Christian Bauer
 */
public abstract class MessageView extends JPanel {

    final private Form form;

    final private JCheckBox displayMaximumCheckBox = new JCheckBox();

    public MessageView() {
        super(new GridBagLayout());

        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        form = new Form(5);

        displayMaximumCheckBox.setSelected(true);
        getForm().addLabelAndLastField("Display whole message:", displayMaximumCheckBox, this);
    }

    public Form getForm() {
        return form;
    }

    public JCheckBox getDisplayMaximumCheckBox() {
        return displayMaximumCheckBox;
    }

    public Message getMessage() {
        return createMessage();
    }

    protected abstract Message createMessage();
}
