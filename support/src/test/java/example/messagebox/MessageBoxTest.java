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
package example.messagebox;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.support.messagebox.AddMessage;
import org.fourthline.cling.support.messagebox.model.DateTime;
import org.fourthline.cling.support.messagebox.model.MessageIncomingCall;
import org.fourthline.cling.support.messagebox.model.MessageSMS;
import org.fourthline.cling.support.messagebox.model.MessageScheduleReminder;
import org.fourthline.cling.support.messagebox.model.NumberName;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p>
 * There are several message types available. The first is an SMS with a sender and receiver
 * names and phone numbers, as well as a timestamp and message text:
 * </p>
 * <a class="citation" href="javacode://this#createSMS()" style="include: M1"/>
 * <p>
 * This message will appear as a "New SMS Received!" notification on your TV, with the
 * option to reveal all message details. The other message types recognized by the TV
 * are incoming call notification as well as calendar schedule reminder:
 * </p>
 * <a class="citation" href="javacode://this#createIncomingCall()" style="include: M2"/>
 * <a class="citation" href="javacode://this#createScheduleReminder()" style="include: M3"/>
 * <p>
 * This is how you send a message asynchronously:
 * </p>
 * <a class="citation" href="javacode://this#sendMessageToTV()" style="include: S1; exclude: EXC1;"/>
 * <p>
 * Note that although your TV's service descriptor most likely contains a
 * <code>RemoveMessage</code> action and Cling Support also ships with a
 * <code>RemoveMessageCallback</code>, this action doesn't seem to be implemented
 * by any Samsung TVs. Messages can only be deleted directly on the TV, with the
 * remote control.
 * </p>
 */
public class MessageBoxTest {

    static private final String MESSAGE_SMS =
            "<Category>SMS</Category>" +
                    "<DisplayType>Maximum</DisplayType>" +
                    "<ReceiveTime><Date>2010-06-21</Date><Time>16:34:12</Time></ReceiveTime>" +
                    "<Receiver><Number>1234</Number><Name>The Receiver</Name></Receiver>" +
                    "<Sender><Number>5678</Number><Name>The Sender</Name></Sender>" +
                    "<Body>Hello World!</Body>";

    static private final String MESSAGE_INCOMING_CALL =
            "<Category>Incoming Call</Category>" +
                    "<DisplayType>Maximum</DisplayType>" +
                    "<CallTime><Date>2010-06-21</Date><Time>16:34:12</Time></CallTime>" +
                    "<Callee><Number>1234</Number><Name>The Callee</Name></Callee>" +
                    "<Caller><Number>5678</Number><Name>The Caller</Name></Caller>";

    static private final String MESSAGE_SCHEDULE_REMINDER =
            "<Category>Schedule Reminder</Category>" +
                    "<DisplayType>Maximum</DisplayType>" +
                    "<StartTime><Date>2010-06-21</Date><Time>16:34:12</Time></StartTime>" +
                    "<Owner><Number>1234</Number><Name>The Owner</Name></Owner>" +
                    "<Subject>The Subject</Subject>" +
                    "<EndTime><Date>2010-06-21</Date><Time>17:34:12</Time></EndTime>" +
                    "<Location>The Location</Location>" +
                    "<Body>Hello World!</Body>";

    @Test
    public void createSMS() {
        MessageSMS msg = new MessageSMS(                    // DOC: M1
                new DateTime("2010-06-21", "16:34:12"),
                new NumberName("1234", "The Receiver"),
                new NumberName("5678", "The Sender"),
                "Hello World!"
        );                                                  // DOC: M1

        String output = msg.toString();
        assertEquals(output, MESSAGE_SMS);

    }

    @Test
    public void createIncomingCall() {
        MessageIncomingCall msg = new MessageIncomingCall(  // DOC: M2
                new DateTime("2010-06-21", "16:34:12"),
                new NumberName("1234", "The Callee"),
                new NumberName("5678", "The Caller")
        );                                                  // DOC: M2

        String output = msg.toString();
        assertEquals(output, MESSAGE_INCOMING_CALL);

    }

    @Test
    public void createScheduleReminder() {
        MessageScheduleReminder msg = new MessageScheduleReminder(  // DOC: M3
                new DateTime("2010-06-21", "16:34:12"),
                new NumberName("1234", "The Owner"),
                "The Subject",
                new DateTime("2010-06-21", "17:34:12"),
                "The Location",
                "Hello World!"
        );                                                          // DOC: M3

        String output = msg.toString();
        assertEquals(output, MESSAGE_SCHEDULE_REMINDER);

    }

    @Test
    public void sendMessageToTV() throws Exception {

        final boolean[] tests = new boolean[1];

        UpnpService upnpService = new MockUpnpService();

        LocalDevice device = MessageBoxSampleData.createDevice(MyTV.class);
        upnpService.getRegistry().addDevice(device);

        MessageSMS msg = new MessageSMS(
                new DateTime("2010-06-21", "16:34:12"),
                new NumberName("1234", "The Receiver"),
                new NumberName("5678", "The Sender"),
                "Hello World!"
        );

        LocalService service = device.findService(new ServiceId("samsung.com", "MessageBoxService"));       // DOC: S1

        upnpService.getControlPoint().execute(
            new AddMessage(service, msg) {

                @Override
                public void success(ActionInvocation invocation) {
                    // All OK
                    tests[0] = true;                                                                    // DOC: EXC1
                }

                @Override
                public void failure(ActionInvocation invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    // Something is wrong
                }
            }
        );                                                                                                  // DOC: S1

        for (boolean test : tests) {
            assert test;
        }
        for (boolean test : ((LocalService<MyTV>) service).getManager().getImplementation().tests) {
            assert test;
        }

    }

    public static class MyTV extends MessageBoxSampleData.MessageBoxService {

        boolean[] tests = new boolean[1];

        @Override
        protected void checkMessage(String id, String type, String messageText) {
            assert id != null;
            assertEquals(type, "text/xml;charset=\"utf-8\"");
            assertEquals(messageText, MESSAGE_SMS);
            tests[0] = true;
        }
    }

}
