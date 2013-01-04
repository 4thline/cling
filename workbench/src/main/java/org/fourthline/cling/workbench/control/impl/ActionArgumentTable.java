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

package org.fourthline.cling.workbench.control.impl;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.workbench.shared.datatable.ArgumentValueCellEditor;
import org.fourthline.cling.workbench.shared.datatable.ArgumentValueCellRenderer;
import org.fourthline.cling.workbench.shared.datatable.BooleanArgumentValueCellEditor;
import org.fourthline.cling.workbench.shared.datatable.CustomArgumentValueCellEditor;
import org.fourthline.cling.workbench.shared.datatable.TextArgumentValueCellEditor;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * http://www.chka.de/swing/table/faq.html
 */
public abstract class ActionArgumentTable extends JTable {

    // Model
    private final ActionArgumentValuesTableModel argumentValuesModel;

    class CellEditorNavigationAction implements ActionListener {
        boolean forward;

        CellEditorNavigationAction(boolean forward) {
            this.forward = forward;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int column;
            int row;
            if ((column = getEditingColumn()) != -1 && (row = getEditingRow()) != -1) {
                if (forward) {
                    if (getEditingRow() < getRowCount()) {
                        editCellAt(row + 1, column);
                    }
                } else {
                    if (getEditingRow() > 0) {
                        editCellAt(row -1, column);
                    }
                }

                if (getEditorComponent() != null)
                    getEditorComponent().requestFocus();
            }
        }
    }

    public ActionArgumentTable(Action action, boolean isInput) {

        ActionArgumentValue[] argumentValues = isInput
                ? createDefaultValuesInput(action) : createDefaultValuesOutput(action);

        this.argumentValuesModel = new ActionArgumentValuesTableModel(argumentValues);

        setRowHeight(25);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setFocusable(false);

        registerKeyboardAction(
                new CellEditorNavigationAction(true),
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
        registerKeyboardAction(
                new CellEditorNavigationAction(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK, false),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        setModel(argumentValuesModel);

        getColumnModel().getColumn(0).setHeaderValue((isInput ? "IN" : "OUT") + " Argument");
        getColumnModel().getColumn(0).setMinWidth(100);
        getColumnModel().getColumn(0).setMaxWidth(250);
        getColumnModel().getColumn(0).setPreferredWidth(200);

        getColumnModel().getColumn(1).setHeaderValue("Value");

        setDefaultRenderer(
                ActionArgumentValue.class,
                new ArgumentValueCellRenderer(isInput)
        );
    }

    public ActionArgumentValuesTableModel getArgumentValuesModel() {
        return argumentValuesModel;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 1) {
            ActionArgument argument = argumentValuesModel.getValueAt(row);
            // TODO: Avoid double instantiation?
            return (argument.getDirection().equals(ActionArgument.Direction.IN)) ||
                    ((ArgumentValueCellEditor) getCellEditor(row, column)).handlesEditability();
        }
        return false;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 1) {
            ActionArgument argument = argumentValuesModel.getValueAt(row);
            ActionArgumentValue argumentValue = argumentValuesModel.getValues()[row];
            return createArgumentValueCellEditor(argument, argumentValue);
        }
        return super.getCellEditor(row, column);
    }

    protected ActionArgumentValue[] createDefaultValuesInput(Action action) {
        ActionArgument[] arguments = action.getInputArguments();
        ActionArgumentValue[] defaultValues = new ActionArgumentValue[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            // Visually, a boolean is shown by the renderer as "off", so even without invoking the editor, we need
            // to set its value to "off" in the model.
            if (Datatype.Builtin.BOOLEAN.equals(arguments[i].getDatatype().getBuiltin())) {
                defaultValues[i] = new ActionArgumentValue(arguments[i], false);
            } else {
                defaultValues[i] = new ActionArgumentValue(arguments[i], "");
            }
        }
        return defaultValues;
    }

    protected ActionArgumentValue[] createDefaultValuesOutput(Action action) {
        ActionArgument[] arguments = action.getOutputArguments();
        ActionArgumentValue[] defaultValues = new ActionArgumentValue[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            defaultValues[i] = new ActionArgumentValue(arguments[i], "");
        }
        return defaultValues;
    }

    protected TableCellEditor createArgumentValueCellEditor(ActionArgument argument, ActionArgumentValue currentValue) {
        if (argument.getDatatype().getBuiltin() != null) {
            switch (argument.getDatatype().getBuiltin()) {
                case UI1:
                case UI2:
                case UI4:
                case I1:
                case I2:
                case I4:
                case INT:
                case STRING:
                    if (argument.getAction().getService().getStateVariable(
                            argument.getRelatedStateVariableName()
                    ).getTypeDetails().getAllowedValues() != null) {
                        // TODO: Use combo box
                        return new TextArgumentValueCellEditor(argument, currentValue) {
                            @Override
                            protected void onExpandText(String text) {
                                ActionArgumentTable.this.onExpandText(text);
                            }
                        };

                    } else {
                        return new TextArgumentValueCellEditor(argument, currentValue) {
                            @Override
                            protected void onExpandText(String text) {
                                ActionArgumentTable.this.onExpandText(text);
                            }
                        };
                    }
                case BOOLEAN:
                    return new BooleanArgumentValueCellEditor(argument, currentValue);
            }
        }
        return new CustomArgumentValueCellEditor(argument, currentValue);
    }

    abstract protected void onExpandText(String text);

}
