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

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.plugins.igd.PortMappingEditView;
import org.fourthline.cling.workbench.plugins.igd.PortMappingPresenter;
import org.fourthline.cling.workbench.plugins.igd.WANIPConnectionControlPoint;
import org.seamless.swing.Application;
import org.seamless.swing.Form;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Christian Bauer
 */
public class PortMappingEditViewImpl extends JPanel implements PortMappingEditView {

    final protected JPanel formPanel = new JPanel(new GridBagLayout());

    final protected JCheckBox enabledField = new JCheckBox();
    final protected JTextField leaseDurationField = new JTextField();
    final protected JTextField remoteHostField = new JTextField();
    final protected JTextField externalPortField = new JTextField();
    final protected JComboBox protocolField = new JComboBox(PortMapping.Protocol.values());
    final protected JTextField internalClientField = new JTextField();
    final protected JTextField internalPortField = new JTextField();
    final protected JTextField descriptionField = new JTextField();

    final protected JToolBar portMappingToolBar = new JToolBar();
    final protected JButton addButton =
            new JButton("Add Port Mapping", Application.createImageIcon(Workbench.class, "img/24/add.png"));
    final protected JButton deleteButton =
            new JButton("Delete Port Mapping", Application.createImageIcon(Workbench.class, "img/24/delete.png"));
    final protected JButton reloadButton =
            new JButton("Reload", Application.createImageIcon(Workbench.class, "img/24/reload.png"));

    protected PortMappingPresenter presenter;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onAddPortMapping();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onDeletePortMapping();
            }
        });

        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onReload();
            }
        });


        Form form = new Form(3);
        setLabelAndField(form, "Enabled:", enabledField);
        enabledField.setSelected(true);
        setLabelAndField(form, "Lease Duration (Seconds):", leaseDurationField);
        leaseDurationField.setText("0");
        setLabelAndField(form, "WAN Host:", remoteHostField);
        remoteHostField.setText("-");
        setLabelAndField(form, "External Port:", externalPortField);
        setLabelAndField(form, "Protocol:", protocolField);
        setLabelAndField(form, "LAN Host:", internalClientField);
        setLabelAndField(form, "Internal Port:", internalPortField);
        setLabelAndField(form, "Description:", descriptionField);

        portMappingToolBar.setFloatable(false);
        portMappingToolBar.add(reloadButton);
        portMappingToolBar.addSeparator();
        portMappingToolBar.add(addButton);
        portMappingToolBar.add(deleteButton);

        add(formPanel, BorderLayout.CENTER);
        add(portMappingToolBar, BorderLayout.SOUTH);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(PortMappingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setPortMapping(PortMapping portMapping) {
        enabledField.setSelected(portMapping.isEnabled());
        leaseDurationField.setText(portMapping.getLeaseDurationSeconds().getValue().toString());
        remoteHostField.setText(portMapping.getRemoteHost());
        externalPortField.setText(portMapping.getExternalPort().getValue().toString());
        protocolField.setSelectedItem(portMapping.getProtocol());
        internalClientField.setText(portMapping.getInternalClient());
        internalPortField.setText(portMapping.getInternalPort().getValue().toString());
        descriptionField.setText(portMapping.getDescription());
    }

    @Override
    public PortMapping getPortMapping() {

        try {
            PortMapping pm = new PortMapping();
            pm.setEnabled(enabledField.isSelected());
            pm.setLeaseDurationSeconds(new UnsignedIntegerFourBytes(leaseDurationField.getText()));
            pm.setRemoteHost(remoteHostField.getText());
            pm.setExternalPort(new UnsignedIntegerTwoBytes(externalPortField.getText()));
            pm.setProtocol((PortMapping.Protocol) protocolField.getSelectedItem());
            pm.setInternalClient(internalClientField.getText());
            pm.setInternalPort(new UnsignedIntegerTwoBytes(internalPortField.getText()));
            pm.setDescription(descriptionField.getText());

            return pm;
        } catch (Exception ex) {
            WANIPConnectionControlPoint.LOGGER.info(
                "Error in port mapping form data: " + ex
            );
        }

        return null;
    }

    protected void setLabelAndField(Form form, String l, Component field) {
        JLabel label = new JLabel(l);
        label.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        form.addLabel(label, formPanel);
        form.addLastField(field, formPanel);
    }

}
