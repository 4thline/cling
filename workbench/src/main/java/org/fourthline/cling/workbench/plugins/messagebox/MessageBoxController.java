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

package org.fourthline.cling.workbench.plugins.messagebox;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.messagebox.AddMessage;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.AbstractController;
import org.seamless.swing.Controller;
import org.seamless.swing.DefaultAction;
import org.seamless.swing.logging.LogMessage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class MessageBoxController extends AbstractController<JFrame> {

    final private static Logger log = Logger.getLogger(MessageBoxController.class.getName());

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
                                Workbench.log(new LogMessage(
                                        "MessageBox", "Successfully sent message to device"
                                ));
                            }

                            @Override
                            public void failure(ActionInvocation invocation,
                                                UpnpResponse operation,
                                                String defaultMsg) {
                                log.severe(defaultMsg);
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
