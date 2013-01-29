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

package org.fourthline.cling.workbench.plugins.igd.impl;

import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.workbench.plugins.igd.PortMappingListView;
import org.fourthline.cling.workbench.plugins.igd.PortMappingPresenter;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * @author Christian Bauer
 */
public class PortMappingListViewImpl implements PortMappingListView {

    protected static class PortMappingTable extends JTable {

        public PortMappingTable() {
            super(new PortMappingTableModel());

            setColumnSelectionAllowed(false);
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            getColumnModel().getColumn(0).setHeaderValue("Enabled");
            getColumnModel().getColumn(0).setMinWidth(50);
            getColumnModel().getColumn(0).setMaxWidth(50);
            getColumnModel().getColumn(0).setPreferredWidth(50);

            getColumnModel().getColumn(1).setHeaderValue("Lease");
            getColumnModel().getColumn(1).setMinWidth(50);
            getColumnModel().getColumn(1).setMaxWidth(50);
            getColumnModel().getColumn(1).setPreferredWidth(50);

            getColumnModel().getColumn(2).setHeaderValue("WAN Host");
            getColumnModel().getColumn(2).setMinWidth(60);
            getColumnModel().getColumn(2).setMaxWidth(150);
            getColumnModel().getColumn(2).setPreferredWidth(60);

            getColumnModel().getColumn(3).setHeaderValue("WAN Port");
            getColumnModel().getColumn(3).setMinWidth(60);
            getColumnModel().getColumn(3).setMaxWidth(60);
            getColumnModel().getColumn(3).setPreferredWidth(60);

            getColumnModel().getColumn(4).setHeaderValue("Protocol");
            getColumnModel().getColumn(4).setMinWidth(60);
            getColumnModel().getColumn(4).setMaxWidth(60);
            getColumnModel().getColumn(4).setPreferredWidth(60);

            getColumnModel().getColumn(5).setHeaderValue("LAN Host");
            getColumnModel().getColumn(5).setMinWidth(80);
            getColumnModel().getColumn(5).setMaxWidth(150);
            getColumnModel().getColumn(5).setPreferredWidth(80);

            getColumnModel().getColumn(6).setHeaderValue("LAN Port");
            getColumnModel().getColumn(6).setMinWidth(60);
            getColumnModel().getColumn(6).setMaxWidth(60);
            getColumnModel().getColumn(6).setPreferredWidth(60);

            getColumnModel().getColumn(7).setHeaderValue("Description");
            getColumnModel().getColumn(7).setMinWidth(50);
            getColumnModel().getColumn(7).setPreferredWidth(100);

        }

        public void updatePortMappings(PortMapping[] portMappings) {
            PortMappingTableModel model = (PortMappingTableModel) getModel();
            model.setPortMappings(portMappings);
        }

        public PortMapping getPortMapping(int index) {
            PortMapping[] portMappings = ((PortMappingTableModel) getModel()).getPortMappings();
            return portMappings.length >= index
                    ? portMappings[index]
                    : null;

        }

    }

    protected static class PortMappingTableModel extends AbstractTableModel {

        PortMapping[] portMappings = new PortMapping[0];

        PortMappingTableModel() {
        }


        public PortMapping[] getPortMappings() {
            return portMappings;
        }

        public void setPortMappings(PortMapping[] portMappings) {
            this.portMappings = portMappings;
            fireTableDataChanged();
        }

        public int getColumnCount() {
            return 8;
        }

        public int getRowCount() {
            return portMappings.length;
        }

        public Object getValueAt(int row, int column) {
            switch (column) {
                case 0:
                    return getPortMappings()[row].isEnabled();
                case 1:
                    return getPortMappings()[row].getLeaseDurationSeconds().getValue();
                case 2:
                    return getPortMappings()[row].getRemoteHost();
                case 3:
                    return getPortMappings()[row].getExternalPort().getValue();
                case 4:
                    return getPortMappings()[row].getProtocol().toString();
                case 5:
                    return getPortMappings()[row].getInternalClient();
                case 6:
                    return getPortMappings()[row].getInternalPort().getValue();
                case 7:
                    return getPortMappings()[row].getDescription();
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return String.class;
                case 5:
                    return String.class;
                case 6:
                    return String.class;
                case 7:
                    return String.class;
            }
            return null;
        }
    }

    final protected PortMappingTable table = new PortMappingTable();
    final protected JScrollPane scrollPane = new JScrollPane(table);

    protected PortMappingPresenter presenter;

    @PostConstruct
    public void init() {
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) return;
                        int index = ((ListSelectionModel) e.getSource()).getMinSelectionIndex();
                        if (index > -1) {
                            presenter.onPortMappingSelected(
                                    table.getPortMapping(index)
                            );
                        }
                    }
                }
        );
    }

    @Override
    public Component asUIComponent() {
        return scrollPane;
    }

    @Override
    public void setPresenter(PortMappingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPortMappings(PortMapping[] portMappings) {
        table.updatePortMappings(portMappings);
    }
}
