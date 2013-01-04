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

package org.fourthline.cling.workbench.monitor;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.support.shared.View;

import java.util.List;

/**
 * @author Christian Bauer
 */
public interface MonitorView extends View<MonitorView.Presenter> {

    public interface Presenter {
        void init(Service service);

        void onStartMonitoring();

        void onStopMonitoring();

    }

    void setTitle(String title);

    void setValues(List<StateVariableValue> values);

    void setStartStopEnabled(boolean startEnabled, boolean stopEnabled);

    void dispose();
}
