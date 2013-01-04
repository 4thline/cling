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

package org.fourthline.cling.workbench.plugins.binarylight.device;

import org.fourthline.cling.workbench.Constants;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class BinaryLightViewImpl extends JDialog implements BinaryLightView {

    final protected JLabel iconLabel = new JLabel();
    final protected ImageIcon onIcon = new ImageIcon(getClass().getResource("img/lightbulb.png"));
    final protected ImageIcon offIcon = new ImageIcon(getClass().getResource("img/lightbulb_off.png"));

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
                        dispose();
                        presenter.onViewDisposed();
                    }
                }
        );

        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(iconLabel);

        setStatus(false);

        setMinimumSize(new Dimension(100, 100));
        setPreferredSize(new Dimension(300, 300));
        setResizable(true);
        pack();
        setVisible(true);
    }

    @Override
    public void setStatus(boolean on) {
        if (on) {
            iconLabel.setIcon(onIcon);
            getContentPane().setBackground(Constants.GREEN_DARK);
            setTitle("Status: ON");
        } else {
            iconLabel.setIcon(offIcon);
            getContentPane().setBackground(Color.BLACK);
            setTitle("Status: OFF");
        }
    }

}
