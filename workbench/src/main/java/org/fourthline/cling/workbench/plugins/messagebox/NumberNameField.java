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