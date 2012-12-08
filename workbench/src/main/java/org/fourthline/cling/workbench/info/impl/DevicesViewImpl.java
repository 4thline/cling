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

package org.fourthline.cling.workbench.info.impl;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.workbench.info.DeviceView;
import org.fourthline.cling.workbench.info.DevicesView;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Bauer
 */
@Singleton
public class DevicesViewImpl extends JPanel implements DevicesView {

    final protected JTabbedPane tabbedPane = new JTabbedPane();
    final Map<DeviceView, Component> tabs = new HashMap(); // Map back from UI component to DeviceView

    protected Presenter presenter;

    @PostConstruct
    public void init() {
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                Component comp = tabbedPane.getSelectedComponent();
                if (comp == null) return;
                for (Map.Entry<DeviceView, Component> entry : tabs.entrySet()) {
                    if (entry.getValue().equals(comp)) {
                        presenter.onDeviceViewChanged(entry.getKey());
                        break;
                    }
                }
            }
        });

        setLayout(new GridLayout(1, 1)); // Makes the tabs magically auto-fit in the parent container
        add(tabbedPane);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addDeviceView(DeviceView deviceView) {
        Component comp = deviceView.asUIComponent();
        if (tabbedPane.indexOfComponent(comp) != -1) {
            tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(comp));
        } else {
            tabs.put(deviceView, comp);
            tabbedPane.addTab(deviceView.getTitle(), comp);
            tabbedPane.setSelectedComponent(comp);
        }
    }

    @Override
    public void removeDeviceView(DeviceView deviceView) {
        Component comp = tabs.get(deviceView);
        if (comp != null) {
            tabs.remove(deviceView);
            tabbedPane.remove(deviceView.asUIComponent());
        }
    }

    @Override
    public boolean switchDeviceView(Device device) {
        for (DeviceView deviceView : tabs.keySet()) {
            if (deviceView.getDevice().equals(device)) {
                Component comp = tabs.get(deviceView);
                tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(comp));
                return true;
            }
        }
        return false;
    }
}
