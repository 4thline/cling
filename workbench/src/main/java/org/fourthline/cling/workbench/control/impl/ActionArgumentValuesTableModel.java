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

package org.fourthline.cling.workbench.control.impl;


import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.ActionArgument;

import javax.swing.table.AbstractTableModel;

public class ActionArgumentValuesTableModel extends AbstractTableModel {

    private ActionArgumentValue[] argumentValues;

    public ActionArgumentValuesTableModel(ActionArgumentValue[] argumentValues) {
        this.argumentValues = argumentValues;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return argumentValues.length;
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return argumentValues[row].getArgument().getName();
        }
        return argumentValues[row];
    }

    public ActionArgument getValueAt(int row){
        return argumentValues[row].getArgument();
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        }
        return ActionArgumentValue.class;
    }

    @Override
    public void setValueAt(Object o, int row, int column) {
        if (column == 1 && o != null) {
            try {
                argumentValues[row] = (ActionArgumentValue)o;
                fireTableDataChanged();
            } catch (Exception ex) {
                // Should never happen because 'o' is already valid
                throw new RuntimeException(ex);
            }
        }
    }

    public ActionArgumentValue[] getValues() {
        return argumentValues;
    }

    public void setValues(ActionArgumentValue[] argumentValues) {
        this.argumentValues = argumentValues;
        fireTableDataChanged();
    }
}
