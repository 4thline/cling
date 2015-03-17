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

import org.fourthline.cling.support.model.PlayMode;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * @author Sebastian Roth
 */
public class PlayModePanel extends JPanel {

    class PlayModeSpinner extends JComboBox {
        PlayModeSpinner() {
            super(PlayMode.values());
        }
    }

    final private PlayModeSpinner playModeSpinner = new PlayModeSpinner();

    public PlayModePanel() {
        super();

        setBorder(BorderFactory.createTitledBorder("Play Mode"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalGlue());

        add(playModeSpinner);

        add(Box.createHorizontalGlue());
    }

    public PlayModeSpinner getPlayModeSpinner() {
        return playModeSpinner;
    }
}
