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
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(true);

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