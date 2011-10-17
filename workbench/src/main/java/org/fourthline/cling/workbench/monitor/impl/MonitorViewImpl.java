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

package org.fourthline.cling.workbench.monitor.impl;

import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.monitor.MonitorView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        stateVariablesScrollPane = new JScrollPane(stateVariablesTable);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(monitoringToolBar, BorderLayout.NORTH);
        mainPanel.add(stateVariablesScrollPane, BorderLayout.CENTER);
        add(mainPanel);

        setResizable(true);
        setMinimumSize(new Dimension(300, 150));
        setPreferredSize(new Dimension(450, 150));
        pack();
        setVisible(true);
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
