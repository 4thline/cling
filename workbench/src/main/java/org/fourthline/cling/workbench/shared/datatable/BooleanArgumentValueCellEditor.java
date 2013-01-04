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

import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class BooleanArgumentValueCellEditor extends ArgumentValueCellEditor {

    private BooleanArgumentValueCellComponent component;

    public BooleanArgumentValueCellEditor(ActionArgument argument, ActionArgumentValue argumentValue) {
        super(argument, argumentValue);

        component = new BooleanArgumentValueCellComponent(argumentValue, true);

        component.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    setArgumentValue(new ActionArgumentValue(getArgument(), false));
                } else if (e.getStateChange() == ItemEvent.SELECTED) {
                    setArgumentValue(new ActionArgumentValue(getArgument(), true));
                }
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable jTable, Object o, boolean b, int i, int i1) {
        return component;
    }

    public boolean handlesEditability() {
        return false;
    }
    
}
