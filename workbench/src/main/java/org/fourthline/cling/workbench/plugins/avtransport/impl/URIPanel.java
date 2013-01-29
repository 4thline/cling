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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;
import org.seamless.swing.Application;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

/**
 * @author Christian Bauer
 */
public class URIPanel extends JPanel {

    public static String[] ACTION_SET = {"Set URI", "avTransportSetURI"};

    final private JTextField uriTextField = new JTextField();
    final private JButton setButton =
            new JButton(
                    ACTION_SET[0],
                    Application.createImageIcon(AVTransportControlPoint.class, "img/16/set_on_screen.png")
            );

    public URIPanel() {
        super();

        setBorder(BorderFactory.createTitledBorder("Current URI"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(uriTextField);

        setButton.setFocusable(false);
        add(setButton);

    }

    public JTextField getUriTextField() {
        return uriTextField;
    }

    public JButton getSetButton() {
        return setButton;
    }
}
