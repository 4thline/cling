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

import org.fourthline.cling.support.messagebox.model.NumberName;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Christian Bauer
 */
public class NumberNameField extends JPanel {

    final private JTextField numberField = new JTextField(10);
    final private JTextField nameField = new JTextField(15);

    public NumberNameField() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(numberField);
        add(nameField);
    }

    public NumberName getNumberName() {
        return new NumberName(numberField.getText(), nameField.getText());
    }
}