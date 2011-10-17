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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPointAdapter;
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
                    Application.createImageIcon(AVTransportControlPointAdapter.class, "img/16/set_on_screen.png")
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
