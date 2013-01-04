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
