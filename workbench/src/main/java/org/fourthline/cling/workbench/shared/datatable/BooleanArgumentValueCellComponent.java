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
import org.fourthline.cling.model.state.StateVariableValue;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.Color;


public class BooleanArgumentValueCellComponent extends JCheckBox {

    protected BooleanArgumentValueCellComponent(boolean enabled) {
        setEnabled(enabled);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBackground(Color.WHITE);
    }

    public BooleanArgumentValueCellComponent(ActionArgumentValue argumentValue, boolean enabled) {
        this(enabled);

        if (argumentValue.getValue() != null) {
            setSelected((Boolean) argumentValue.getValue());
        }
    }

    public BooleanArgumentValueCellComponent(StateVariableValue stateVariableValue) {
        this(true);

        if (stateVariableValue != null) {
            setSelected((Boolean) stateVariableValue.getValue());
        }
    }
}
