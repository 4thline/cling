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

package org.fourthline.cling.workbench.info;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.support.shared.View;

import javax.swing.ImageIcon;

/**
 * @author Christian Bauer
 */
public interface DeviceView extends View<DeviceView.Presenter> {

    public interface Presenter {
        void onUseService(Service service);

        void onMonitorService(Service service);

        void onActionInvoke(Action action);

        void onQueryStateVariable(StateVariable stateVar);

        void onCopyInfoItem(InfoItem item);

        void onDeviceViewClosed(DeviceView deviceView);
    }

    String getTitle();

    void setDevice(Namespace namespace, ImageIcon rootDeviceIcon, Device device);

    Device getDevice();

}
