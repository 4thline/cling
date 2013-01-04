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

import org.fourthline.cling.support.messagebox.model.DateTime;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Christian Bauer
 */
public class DateTimeField extends JPanel {

    final private JTextField dateField = new JTextField(10);
    final private JTextField timeField = new JTextField(8);

    public DateTimeField() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        DateTime dt = new DateTime();

        dateField.setText(dt.getDate());
        add(dateField);

        timeField.setText(dt.getTime());
        add(timeField);
    }

    public DateTime getDateTime() {
        return new DateTime(dateField.getText(), timeField.getText());
    }
}
