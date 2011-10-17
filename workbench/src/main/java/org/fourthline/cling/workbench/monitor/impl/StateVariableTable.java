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

import javax.swing.JTable;
import java.util.List;

/**
 * http://www.chka.de/swing/table/faq.html
 */
public class StateVariableTable extends JTable {

    // Model
    private final StateVariableValuesTableModel valuesModel;

    public StateVariableTable(List<StateVariableValue> values) {

        this.valuesModel = new StateVariableValuesTableModel(values);

        setRowHeight(25);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setFocusable(false);

        setModel(valuesModel);

        getColumnModel().getColumn(0).setHeaderValue("State Variable");
        getColumnModel().getColumn(0).setMinWidth(100);
        getColumnModel().getColumn(0).setMaxWidth(250);
        getColumnModel().getColumn(0).setPreferredWidth(200);

        getColumnModel().getColumn(1).setHeaderValue("Value");

        setDefaultRenderer(StateVariableValue.class, new StateVariableValueCellRenderer());
    }

    public StateVariableValuesTableModel getValuesModel() {
        return valuesModel;
    }

}