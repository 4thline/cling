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

package org.fourthline.cling.workbench.spi;

import org.fourthline.cling.support.shared.View;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReconnectView extends DitheredBackgroundPanel implements View<ReconnectView.Presenter> {

    public interface Presenter {
        void onConnectClicked();

        void onWakeupClicked();

    }

    final protected JButton connectButton = new JButton("Connect...");
    final protected JButton wakeupButton = new JButton("Wake Up...");

    protected Presenter presenter;

    @PostConstruct
    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        add(Box.createHorizontalGlue());
        add(connectButton);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        setPresenter(presenter, false);
    }

    public void setPresenter(Presenter presenter, boolean enableWakeup) {
        this.presenter = presenter;

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ReconnectView.this.presenter.onConnectClicked();
            }
        });

        if (enableWakeup) {
            wakeupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ReconnectView.this.presenter.onWakeupClicked();
                }
            });
            add(wakeupButton);
        }

        add(Box.createHorizontalGlue());
    }
}
