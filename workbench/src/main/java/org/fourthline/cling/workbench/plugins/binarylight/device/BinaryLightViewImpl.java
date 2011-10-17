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
