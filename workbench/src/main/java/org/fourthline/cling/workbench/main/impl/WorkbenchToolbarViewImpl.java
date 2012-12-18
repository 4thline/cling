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

package org.fourthline.cling.workbench.main.impl;

import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.main.WorkbenchToolbarView;
import org.seamless.swing.Application;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Christian Bauer
 */
@Singleton
public class WorkbenchToolbarViewImpl extends JToolBar implements WorkbenchToolbarView {

    final protected JButton demoButton =
            new JButton("Create Demo Device", Application.createImageIcon(Workbench.class, "img/24/lightbulb.png"));

    protected Presenter presenter;

    @PostConstruct
    public void init() {

        setFloatable(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onCreateDemoDevice();
            }
        });
        add(demoButton);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
