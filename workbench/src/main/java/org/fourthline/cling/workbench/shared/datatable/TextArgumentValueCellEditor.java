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
