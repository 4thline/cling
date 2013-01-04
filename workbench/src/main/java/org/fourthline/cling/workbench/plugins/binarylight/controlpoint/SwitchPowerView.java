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

package org.fourthline.cling.workbench.plugins.binarylight.controlpoint;

import org.fourthline.cling.support.shared.View;
import org.fourthline.cling.workbench.spi.ReconnectView;

/**
 * @author Christian Bauer
 */
public interface SwitchPowerView extends View<SwitchPowerView.Presenter> {

    public interface Presenter {
        void onSwitchToggle(boolean on);

        void onViewDisposed();
    }

    void setReconnectView(ReconnectView reconnectView);

    void setReconnectViewEnabled(boolean enabled);

    void setTitle(String title);

    void toggleSwitch(boolean on);

    void dispose();
}
