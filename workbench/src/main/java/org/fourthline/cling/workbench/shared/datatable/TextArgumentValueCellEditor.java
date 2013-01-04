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

package org.fourthline.cling.workbench.shared.datatable;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.ActionArgument;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public abstract class TextArgumentValueCellEditor extends ArgumentValueCellEditor {

    private JTextField textField = new JTextField();

    public TextArgumentValueCellEditor(ActionArgument argument, ActionArgumentValue argumentValue) {
        super(argument, argumentValue);

        textField.setEditable(argument.getDirection().equals(ActionArgument.Direction.IN));
        textField.setText(argumentValue != null ? argumentValue.toString() : "");
    }

    @Override
    public boolean stopCellEditing() {
        try {
            String text = textField.getText();

/*
            if (text == null || text.length() == 0) {
                fireEditingStopped();
                return true;
            }
*/

            setArgumentValue(
                    new ActionArgumentValue(getArgument(), text)
            );

            fireEditingStopped();
            return true;

        // TODO
        //} catch (ActionException ex) {

        } catch (Exception ex) {
            setStatus(ex.getMessage());
            textField.setBorder(new LineBorder(Color.RED, 1));
            return false;
        }
    }


    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        if (getArgument().getDirection().equals(ActionArgument.Direction.OUT)) {

            if (textField.getText() != null && textField.getText().length() > 50) {
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

                JButton expandButton = new JButton("Expand");
                expandButton.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                onExpandText(textField.getText());
                            }
                        }
                );
                panel.add(textField);
                panel.add(expandButton);
                return panel;
            }

            return textField;
        }
        return textField;
    }

    public boolean handlesEditability() {
        return true;
    }

    abstract protected void onExpandText(String text);
}
