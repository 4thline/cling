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
import java.util.logging.Handler;
import java.util.logging.LogManager;

/**
 * @author Christian Bauer
 */
public abstract class Main implements ShutdownHandler, Thread.UncaughtExceptionHandler {

    @Inject
    LogView.Presenter logPresenter;

    final protected JFrame errorWindow = new JFrame();
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

        // Wire logging UI into JUL
        // Don't reset JUL root logger but add if there is a JUL config file
        Handler handler = new LoggingHandler() {
            protected void log(LogMessage msg) {
                logPresenter.pushMessage(msg);
            }
        };
        if (System.getProperty("java.util.logging.config.file") == null) {
            LoggingUtil.resetRootHandler(handler);
        } else {
            LogManager.getLogManager().getLogger("").addHandler(handler);
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

    abstract protected String getAppName();

}
