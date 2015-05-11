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

package org.fourthline.cling.workbench.monitor.impl;

import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.monitor.MonitorView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class MonitorViewImpl extends JDialog implements MonitorView {

    final protected JToolBar monitoringToolBar = new JToolBar();

    final protected JButton startButton =
            new JButton("Start Monitoring", Application.createImageIcon(Workbench.class, "img/16/run.png"));

    final protected JButton stopButton =
            new JButton("Stop Monitoring", Application.createImageIcon(Workbench.class, "img/16/stop.png"));

    protected JScrollPane stateVariablesScrollPane;
    protected StateVariableTable stateVariablesTable;

    protected Presenter presenter;

    final protected JToolBar toolBar = new JToolBar();

    final protected JButton copyButton =
            new JButton("Copy", Application.createImageIcon(Workbench.class, "img/16/copyclipboard.png"));

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        presenter.onStopMonitoring();
                        dispose();
                    }
                }
        );

        monitoringToolBar.setMargin(new Insets(5, 0, 5, 0));
        monitoringToolBar.setFloatable(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onStartMonitoring();
            }
        });
        startButton.setPreferredSize(new Dimension(5000, 25));

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onStopMonitoring();
            }
        });
        stopButton.setEnabled(false);
        stopButton.setPreferredSize(new Dimension(5000, 25));

        monitoringToolBar.add(startButton);
        monitoringToolBar.add(stopButton);

        stateVariablesTable = new StateVariableTable(null);
        stateVariablesTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {

                        if (e.getValueIsAdjusting()) return;

                        if (e.getSource() == stateVariablesTable.getSelectionModel()) {
                            int[] rows = stateVariablesTable.getSelectedRows();

                            if (rows == null || rows.length == 0) {
                                copyButton.setEnabled(false);
                            } else if (rows.length == 1) {
                                copyButton.setEnabled(true);
                            } else {
                                copyButton.setEnabled(true);
                            }
                        }
                    }
                }
        );

        stateVariablesScrollPane = new JScrollPane(stateVariablesTable);

        initializeLoggingToolbar();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(monitoringToolBar, BorderLayout.NORTH);
        mainPanel.add(stateVariablesScrollPane, BorderLayout.CENTER);
        mainPanel.add(toolBar, BorderLayout.SOUTH);
        add(mainPanel);

        setResizable(true);
        setMinimumSize(new Dimension(300, 150));
        setPreferredSize(new Dimension(450, 150));
        pack();
        setVisible(true);
    }

    protected void initializeLoggingToolbar() {
        toolBar.setMargin(new Insets(5, 0, 5, 0));
        toolBar.setFloatable(false);
        toolBar.add(copyButton);

        copyButton.setFocusable(false);
        copyButton.setEnabled(false);
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringBuilder sb = new StringBuilder();
                List<StateVariableValue> messages = getSelectedValue();
                for (StateVariableValue message : messages) {
                    sb.append(message.toString()).append("\n");
                }
                Application.copyToClipboard(sb.toString());
            }
        });

        toolBar.setFloatable(false);
        toolBar.add(copyButton);
        toolBar.add(Box.createHorizontalGlue());
    }

    protected List<StateVariableValue> getSelectedValue() {
        List<StateVariableValue> messages = new ArrayList<>();
        for (int row : stateVariablesTable.getSelectedRows()) {
            messages.add((StateVariableValue) stateVariablesTable.getValueAt(row, 1));
        }
        return messages;
    }

    @Override
    public void setValues(List<StateVariableValue> values) {
        stateVariablesTable.getValuesModel().setValues(values);
        validate();
    }

    @Override
    public void setStartStopEnabled(boolean startEnabled, boolean stopEnabled) {
        startButton.setEnabled(startEnabled);
        stopButton.setEnabled(stopEnabled);
        validate();
    }
}
