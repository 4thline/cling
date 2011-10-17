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

package org.fourthline.cling.support.shared;

import org.fourthline.cling.model.ModelUtil;
import org.seamless.swing.Application;
import org.seamless.xml.DOM;
import org.seamless.xml.DOMParser;
import org.w3c.dom.Document;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class TextExpandDialog extends JDialog {

    // TODO: Make this a plugin SPI and let the plugin impl decide how text should be detected and rendered

    private static Logger log = Logger.getLogger(TextExpandDialog.class.getName());

    public TextExpandDialog(Frame frame, String text) {
        super(frame);
        setResizable(true);

        JTextArea textArea = new JTextArea();
        JScrollPane textPane = new JScrollPane(textArea);
        textPane.setPreferredSize(new Dimension(500, 400));
        add(textPane);

        String pretty;
        if (text.startsWith("<") && text.endsWith(">")) {
            try {
                pretty = new DOMParser() {
                    @Override
                    protected DOM createDOM(Document document) {
                        return null;
                    }
                }.print(text, 2, false);
            } catch (Exception ex) {
                log.severe("Error pretty printing XML: " + ex.toString());
                pretty = text;
            }
        } else if (text.startsWith("http-get")) {
            pretty = ModelUtil.commaToNewline(text);
        } else {
            pretty = text;
        }

        textArea.setEditable(false);
        textArea.setText(pretty);

        pack();
        Application.center(this, getOwner());
        setVisible(true);
    }
}
