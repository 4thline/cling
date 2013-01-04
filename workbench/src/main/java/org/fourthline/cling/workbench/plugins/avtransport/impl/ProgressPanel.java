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

import org.fourthline.cling.support.model.PositionInfo;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;

/**
 * @author Christian Bauer
 */
public class ProgressPanel extends JPanel {

    final private JSlider positionSlider = new JSlider(0, 100, 0);
    final private JLabel positionLabel = new JLabel();
    private PositionInfo positionInfo;

    public ProgressPanel() {
        super();

        setBorder(BorderFactory.createTitledBorder("Position"));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        positionLabel.setText("00:00:00/00:00:00");
        positionLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        positionSlider.setEnabled(false);

        positionSlider.setPreferredSize(new Dimension(200, 24));
        add(positionSlider);
        add(positionLabel);

    }

    public JSlider getPositionSlider() {
        return positionSlider;
    }

    public void setProgress(PositionInfo positionInfo) {
        if (positionInfo == null) {
            positionLabel.setText("00:00:00/00:00:00");
            setPositionSliderWithoutNotification(0);
        } else {
            if (positionInfo.getTrackDurationSeconds() > 0) {
                positionLabel.setText(positionInfo.getRelTime() + "/"  + positionInfo.getTrackDuration());
                setPositionSliderWithoutNotification(positionInfo.getElapsedPercent());
                positionSlider.setEnabled(true);
            } else {
                positionLabel.setText(positionInfo.getRelTime());
                positionSlider.setEnabled(false);
            }
        }
        this.positionInfo = positionInfo;
    }

    // Internal re-positioning, should not fire a Seek UPnP action, so we remove
    // the listener before and add it back afterwards
    protected void setPositionSliderWithoutNotification(int value) {
        if (value == positionSlider.getValue()) return;
        ChangeListener[] listeners = positionSlider.getChangeListeners();
        for (ChangeListener listener : listeners) {
            positionSlider.removeChangeListener(listener);
        }
        positionSlider.setValue(value);
        for (ChangeListener listener : listeners) {
            positionSlider.addChangeListener(listener);
        }
    }

    public PositionInfo getPositionInfo() {
        return positionInfo;
    }
}

