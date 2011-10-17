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