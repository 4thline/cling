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
