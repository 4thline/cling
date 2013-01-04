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
