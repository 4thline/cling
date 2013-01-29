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

package org.fourthline.cling.workbench.plugins.messagebox;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.messagebox.AddMessage;
import org.seamless.swing.AbstractController;
import org.seamless.swing.Controller;
import org.seamless.swing.DefaultAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Christian Bauer
 */
public class MessageBoxController extends AbstractController<JFrame> {

    public static String[] ACTION_SEND_MESSAGE = {"Send Message", "sendMessage"};

    // Dependencies
    final protected ControlPoint controlPoint;
    final protected Service service;

    // View
    final private JTabbedPane tabs = new JTabbedPane();
    final private JButton sendMessageButton = new JButton(ACTION_SEND_MESSAGE[0]);

    public MessageBoxController(Controller parentController, final ControlPoint controlPoint, final Service service) {
        super(new JFrame("MessageBoxService on: " + service.getDevice().getDetails().getFriendlyName()), parentController);
        this.controlPoint = controlPoint;
        this.service = service;

        tabs.addTab("SMS", new MessageSMSView());
        tabs.addTab("Incoming Call", new MessageIncomingCallView());
        tabs.addTab("Schedule Reminder", new MessageScheduleReminderView());

        registerAction(sendMessageButton, ACTION_SEND_MESSAGE[1], new DefaultAction() {
            public void actionPerformed(ActionEvent e) {

                controlPoint.execute(
                        new AddMessage(service, getCurrentView().getMessage()) {
                            @Override
                            public void success(ActionInvocation invocation) {
                                MessageBoxControlPoint.LOGGER.info(
                                    "Successfully sent message to device"
                                );
                            }

                            @Override
                            public void failure(ActionInvocation invocation,
                                                UpnpResponse operation,
                                                String defaultMsg) {
                                MessageBoxControlPoint.LOGGER.severe(defaultMsg);
                            }
                        }
                );

            }
        });


        getView().addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        MessageBoxController.this.dispose();
                    }
                }
        );

        getView().getContentPane().add(tabs, BorderLayout.CENTER);
        getView().getContentPane().add(sendMessageButton, BorderLayout.SOUTH);
        getView().setResizable(false);
        getView().pack();
    }

    public MessageView getCurrentView() {
        return (MessageView)tabs.getSelectedComponent();
    }


}
