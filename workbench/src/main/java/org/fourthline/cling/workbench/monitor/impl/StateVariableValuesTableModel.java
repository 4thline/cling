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


import org.fourthline.cling.model.state.StateVariableValue;

import javax.swing.table.AbstractTableModel;
import java.util.List;


public class StateVariableValuesTableModel extends AbstractTableModel {

    private List<StateVariableValue> values;

    public StateVariableValuesTableModel(List<StateVariableValue> values) {
        this.values = values;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return values == null ? 0 : values.size();
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return values.get(row).getStateVariable().getName();
        }
        return getValueAt(row);
    }

    public StateVariableValue getValueAt(int row){
        return values.get(row);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        }
        return StateVariableValue.class;
    }


    public List<StateVariableValue> getValues() {
        return values;
    }

    public void setValues(List<StateVariableValue> values) {
        this.values = values;
        fireTableDataChanged();
    }
}