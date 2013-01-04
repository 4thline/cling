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

package org.fourthline.cling.workbench.monitor.impl;

import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.workbench.shared.datatable.BooleanArgumentValueCellComponent;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;


public class StateVariableValueCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int columns) {

        StateVariableValuesTableModel model = (StateVariableValuesTableModel)table.getModel();


        StateVariableValue stateVariableValue = model.getValueAt(row);
        StateVariable stateVariable = stateVariableValue.getStateVariable();

        switch (stateVariable.getTypeDetails().getDatatype().getBuiltin()) {
            case BOOLEAN:

                return new BooleanArgumentValueCellComponent(stateVariableValue);

            default:
                String stringValue;
                if (stateVariableValue != null && stateVariableValue.toString().length() > 0) {
                    stringValue = stateVariableValue.toString();
                } else {
                    stringValue = "<<NULL>>";
                }

                return super.getTableCellRendererComponent(
                        table, stringValue, isSelected, hasFocus, row, columns
                );
        }
    }

}
