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
package example.controlpoint;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.model.profile.RemoteClientInfo;

/**
 * Reacting to cancellation on the server
 * <p>
 * By default, an action method of your service will run until it completes, it either returns or throws an exception.
 * If you have to perform long-running tasks in a service, your action method can avoid doing unnecessary work by
 * checking if processing should continue. Think about processing in batches: You work for a while, then you check if
 * you should continue, then you work some more, check again, and so on.
 * </p>
 * <p>
 * There are two checks you have to perform:
 * </p>
 * <ul>
 * <li>
 * If a local control point called your service, and meanwhile cancelled the action call, the thread running your action
 * method will have its interruption flag set. When you see this flag you can stop processing, as any result of your
 * action method will be ignored anyway.
 * </li>
 * <li>
 * If a remote control point called your service, it might have dropped the connection while you were processing data
 * to return. Unfortunately, checking if the client's connection is still open requires, on a TCP level, writing data
 * on the socket. This is essentially a heartbeat signal: Every time you check if the client is still there, a byte of
 * (hopefully) insignificant data will be send to the client. If there wasn't any error sending data, the connection
 * is still alive.
 * </li>
 * </ul>
 * <p>
 * These checks look as follows in your service method:
 * </p>
 * <a class="citation"
 *    href="javacode://this"
 *    style="include: ACTION_METHOD; exclude: ACTUAL_WORK;"/>
 *
 * <p>
 * You abort processing by throwing an <code>InterruptedException</code>, Cling will do the rest. Cling will send
 * a heartbeat to the client whenever you check if the remote request was cancelled with the optional
 * <code>RemoteClientInfo</code>, see <a href="#javadoc.example.localservice.RemoteClientInfoTest">this section</a>.
 * </p>
 * <p>
 * <em>Danger:</em> Not all HTTP clients can deal with Cling's heartbeat signal. Not even all bundled
 * <code>StreamClient</code>'s of Cling can handle such a signal. You should only use this feature if you are sure that
 * all clients of your service will ignore the meaningless heartbeat signal. Cling sends a space character (this is
 * configurable) to the HTTP client to check the connection. Hence, the HTTP client sees a response such as
 * '[space][space][space]HTTP/1.0', with a space character for each alive check. If your HTTP client does not trim those
 * space characters before parsing the response, it will fail processing your otherwise valid response.
 * </p>
 * <p>
 * The following Cling-bundled client transports can deal with a heartbeat signal:
 * </p>
 * <table class="infotable fullwidth" border="1">
 * <thead>
 * <tr>
 * <th>Transport</th>
 * <th class="thirdwidth">Accepts Heartbeat?</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.StreamClientImpl (default)</code>
 * </td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.apache.StreamClientImpl</code>
 * </td>
 * <td>YES</td>
 * </tr>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.jetty.StreamClientImpl (default on Android)</code>
 * </td>
 * <td>YES</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * Equally important, not all server transports in Cling can send heartbeat signals, as low-level socket access is
 * required. Some server APIs do not provide this low-level access. If you check the connection state with those
 * transports, the connection is always "alive":
 * </p>
 * <table class="infotable fullwidth" border="1">
 * <thead>
 * <tr>
 * <th>Transport</th>
 * <th class="thirdwidth">Sends Heartbeat?</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.StreamServerImpl (default)</code>
 * </td>
 * <td>NO</td>
 * </tr>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.apache.StreamServerImpl</code>
 * </td>
 * <td>YES</td>
 * </tr>
 * <tr>
 * <td class="nowrap">
 * <code>org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl</code><br/>
 * with <code>org.fourthline.cling.transport.impl.jetty.JettyServletContainer (default on Android)</code>
 * </td>
 * <td>YES</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * In practice, this heartbeat feature is less useful than it sounds in theory: As you usually don't control which HTTP
 * clients will access your server, sending them "garbage" bytes before responding properly will most likely cause
 * interoperability problems.
 * </p>
 */
@UpnpService(
    serviceId = @UpnpServiceId("SwitchPower"),
    serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
)
public class SwitchPowerWithInterruption {

    @UpnpStateVariable(sendEvents = false)
    private boolean target = false;

    @UpnpStateVariable
    private boolean status = false;

    // DOC:ACTION_METHOD
    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = "NewTargetValue") boolean newTargetValue,
                          RemoteClientInfo remoteClientInfo) throws InterruptedException {
        // DOC:ACTUAL_WORK
        target = newTargetValue;
        status = newTargetValue;
        // DOC:ACTUAL_WORK

        boolean interrupted = false;
        while (!interrupted) {
            // Do some long-running work and periodically test if you should continue...

            // ... for local service invocation
            if (Thread.interrupted())
                interrupted = true;

            // ... for remote service invocation
            if (remoteClientInfo != null && remoteClientInfo.isRequestCancelled())
                interrupted = true;
        }
        throw new InterruptedException("Execution interrupted");
    }
    // DOC:ACTION_METHOD

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget() {
        return target;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return status;
    }

}
