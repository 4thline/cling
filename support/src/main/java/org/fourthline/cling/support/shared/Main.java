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

import org.fourthline.cling.support.shared.log.LogView;
import org.seamless.util.logging.LoggingUtil;
import org.seamless.swing.Application;
import org.seamless.swing.logging.LogMessage;
import org.seamless.swing.logging.LoggingHandler;
import org.seamless.util.OS;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.LogManager;

/**
 * @author Christian Bauer
 */
public abstract class Main implements ShutdownHandler, Thread.UncaughtExceptionHandler {

    @Inject
    LogView.Presenter logPresenter;

    final protected JFrame errorWindow = new JFrame();

    // In addition to the JUL-configured handler, show log messages in the UI
    final protected LoggingHandler loggingHandler =
        new LoggingHandler() {
            @Override
            protected void log(LogMessage msg) {
                logPresenter.pushMessage(msg);
            }
        };

    protected boolean isRegularShutdown;

    public void init() {

        try {
            // Platform specific setup
            if (OS.checkForMac())
                NewPlatformApple.setup(this, getAppName());

            // Some UI stuff (of course, why would the OS L&F be the default -- too easy?!)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception ex) {
            // Ignore...
        }

        // Exception handler
        errorWindow.setPreferredSize(new Dimension(900, 400));
        errorWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                errorWindow.dispose();
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(this);

        // Shutdown behavior
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!isRegularShutdown) { // Don't run the hook if everything is already stopped
                    shutdown();
                }
            }
        });

        // Wire logging UI into JUL, don't reset JUL root logger but
        // add our handler if there is a JUL config file
        if (System.getProperty("java.util.logging.config.file") == null) {
            LoggingUtil.resetRootHandler(loggingHandler);
        } else {
            LogManager.getLogManager().getLogger("").addHandler(loggingHandler);
        }
    }

    @Override
    public void shutdown() {
        isRegularShutdown = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                errorWindow.dispose();
            }
        });
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {

        System.err.println("In thread '" + thread + "' uncaught exception: " + throwable);
        throwable.printStackTrace(System.err);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                errorWindow.getContentPane().removeAll();

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                StringBuilder text = new StringBuilder();

                text.append("An exceptional error occurred!\nYou can try to continue or exit the application.\n\n");
                text.append("Please tell us about this here:\nhttp://www.4thline.org/projects/mailinglists-cling.html\n\n");
                text.append("-------------------------------------------------------------------------------------------------------------\n\n");
                Writer stackTrace = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stackTrace));
                text.append(stackTrace.toString());

                textArea.setText(text.toString());
                JScrollPane pane = new JScrollPane(textArea);
                errorWindow.getContentPane().add(pane, BorderLayout.CENTER);

                JButton exitButton = new JButton("Exit Application");
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(1);
                    }
                });

                errorWindow.getContentPane().add(exitButton, BorderLayout.SOUTH);

                errorWindow.pack();
                Application.center(errorWindow);
                textArea.setCaretPosition(0);

                errorWindow.setVisible(true);
            }
        });
    }

    protected void removeLoggingHandler() {
        LogManager.getLogManager().getLogger("").removeHandler(loggingHandler);
    }

    abstract protected String getAppName();

}
