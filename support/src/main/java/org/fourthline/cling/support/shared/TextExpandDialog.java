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
