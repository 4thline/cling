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
