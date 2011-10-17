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
