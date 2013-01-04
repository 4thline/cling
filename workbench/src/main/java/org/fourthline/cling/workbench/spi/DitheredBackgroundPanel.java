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

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Graphics;

public class DitheredBackgroundPanel extends JPanel {

    protected Icon ditherBackground =
            new ImageIcon(DitheredBackgroundPanel.class.getResource("img/ditherbackground.png"));

    public void paintComponent(Graphics g) {
        Dimension dim = getSize();
        int x, y;
        if (isOpaque()) {
            super.paintComponent(g);
        }
        for (y = 0; y < dim.height; y += ditherBackground.getIconHeight()) {
            for (x = 0; x < dim.width; x += ditherBackground.getIconWidth()) {
                ditherBackground.paintIcon(this, g, x, y);
            }
        }
    }

}
