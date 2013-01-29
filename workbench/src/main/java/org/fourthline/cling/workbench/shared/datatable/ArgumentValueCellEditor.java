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
import org.fourthline.cling.workbench.Workbench;

import javax.swing.*;
import javax.swing.table.TableCellEditor;


public abstract class ArgumentValueCellEditor extends AbstractCellEditor implements TableCellEditor {

    final private ActionArgument argument;
    private ActionArgumentValue argumentValue;

    protected ArgumentValueCellEditor(ActionArgument argument, ActionArgumentValue argumentValue) {
        this.argument = argument;
        this.argumentValue = argumentValue;
    }

    public ActionArgument getArgument() {
        return argument;
    }

    public ActionArgumentValue getArgumentValue() {
        return argumentValue;
    }

    public void setArgumentValue(ActionArgumentValue argumentValue) {
        this.argumentValue = argumentValue;
    }

    public Object getCellEditorValue() {
        return getArgumentValue();
    }

    public void setStatus(String msg) {
        Workbench.Log.MAIN.info(msg);
    }

    public abstract boolean handlesEditability();

}
