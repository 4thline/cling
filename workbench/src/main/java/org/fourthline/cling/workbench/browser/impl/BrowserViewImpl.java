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

package org.fourthline.cling.workbench.browser.impl;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.workbench.Constants;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.browser.BrowserView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Christian Bauer
 */
@Singleton
public class BrowserViewImpl extends JPanel implements BrowserView {

    final protected JToolBar browserToolBar = new JToolBar();

    final protected JButton refreshDevicesButton =
            new JButton("Refresh", Application.createImageIcon(Workbench.class, "img/24/search.png"));

    final protected DeviceList deviceList = new DeviceList(new DeviceListModel(), new DeviceListCellRenderer());
    final protected JScrollPane deviceListPane = new JScrollPane(deviceList);

    protected Presenter presenter;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        refreshDevicesButton.setPreferredSize(new Dimension(2500, 32));
        refreshDevicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onRefreshDevices();
            }
        });

        browserToolBar.setFloatable(false);
        browserToolBar.add(refreshDevicesButton);

        // Disables auto-resizing, will fit Container view
        deviceListPane.setPreferredSize(new Dimension(100, 100));

        add(browserToolBar, BorderLayout.SOUTH);
        add(deviceListPane, BorderLayout.CENTER);
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
    public void setSelected(Device device) {
        int indexOfDevice = deviceList.getModel().indexOf(device);
        if (indexOfDevice != -1) {
            deviceList.setSelectedIndex(indexOfDevice);
            deviceList.ensureIndexIsVisible(indexOfDevice);
        } else {
            deviceList.clearSelection();
        }
    }

    @Override
    public void addDeviceItem(DeviceItem item) {
        deviceList.getModel().addElement(item);
    }

    @Override
    public void removeDeviceItem(DeviceItem item) {
        deviceList.getModel().removeElement(item);
    }

    protected class DeviceListModel extends DefaultListModel {

        @Override
        public int indexOf(Object o) {
            if (o instanceof Device) {
                Device device = (Device) o;
                DeviceItem display = new DeviceItem(device, device.getDisplayString());
                return indexOf(display);
            }
            return super.indexOf(o);
        }

    }

    protected class DeviceList extends JList {

        public DeviceList(DeviceListModel model, ListCellRenderer renderer) {
            super(model);
            setCellRenderer(renderer);

            setLayoutOrientation(JList.VERTICAL);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            setFocusable(false);

            addListSelectionListener(
                    new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            if (e.getValueIsAdjusting() || getSelectedIndex() == -1) return;
                            DeviceItem selected = (DeviceItem) getModel().getElementAt(getSelectedIndex());
                            presenter.onDeviceSelected(selected.getIcon(), selected.getDevice());
                        }
                    }
            );
        }

        @Override
        public DeviceListModel getModel() {
            return (DeviceListModel) super.getModel();
        }
    }

    protected class DeviceListCellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            assert value instanceof DeviceItem;
            DeviceItem display = (DeviceItem) value;

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel iconLabel = new JLabel(display.getIcon());
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(iconLabel);

            for (String label : display.getLabel()) {
                JLabel l = new JLabel(label);

                // First label is larger font
                if (display.getLabel()[0].equals(label)) {
                    Application.increaseFontSize(l);
                } else {
                    Application.decreaseFontSize(l);
                }
                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(l);
            }

            panel.setBackground(Color.WHITE);
            if (isSelected) {
                iconLabel.setBorder(new LineBorder(Constants.GREEN_DARK, 4));
            } else {
                iconLabel.setBorder(new LineBorder(Color.WHITE, 4));
            }

            return panel;
        }
    }
}
