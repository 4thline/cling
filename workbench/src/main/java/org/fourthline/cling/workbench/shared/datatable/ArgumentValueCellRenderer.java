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
import org.fourthline.cling.workbench.control.impl.ActionArgumentValuesTableModel;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;


public class ArgumentValueCellRenderer extends DefaultTableCellRenderer {

    private boolean editable;

    public ArgumentValueCellRenderer(boolean editable) {
        this.editable = editable;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int columns) {

        ActionArgumentValuesTableModel model = (ActionArgumentValuesTableModel)table.getModel();


        ActionArgumentValue argumentValue = model.getValues()[row];
        ActionArgument argument = model.getValueAt(row);

        switch (argument.getDatatype().getBuiltin()) {
            case BOOLEAN:

                return new BooleanArgumentValueCellComponent(argumentValue, editable);

            default:
                String argumentStringValue = null;
                if (argumentValue != null && argumentValue.toString().length() == 0) {
                    argumentStringValue = "<<NULL>>";
                } else if (argumentValue != null && argumentValue.toString().length() > 0) {
                    argumentStringValue = argumentValue.toString();
                } else if (argumentValue == null) {
                    argumentStringValue = "Click to edit...";
                }

                return super.getTableCellRendererComponent(
                        table, argumentStringValue, isSelected, hasFocus, row, columns
                );
        }
    }

}
