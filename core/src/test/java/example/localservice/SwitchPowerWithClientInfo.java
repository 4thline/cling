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

package example.localservice;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.model.profile.RemoteClientInfo;

@UpnpService(
    serviceId = @UpnpServiceId("SwitchPower"),
    serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
)
@UpnpStateVariables(
    {
        @UpnpStateVariable(
            name = "Target",
            defaultValue = "0",
            sendEvents = false
        ),
        @UpnpStateVariable(
            name = "Status",
            defaultValue = "0"
        )
    }
)
public class SwitchPowerWithClientInfo {

    private boolean power;

    // DOC:CLIENT_INFO
    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = "NewTargetValue")
                          boolean newTargetValue,
                          RemoteClientInfo clientInfo) {
        power = newTargetValue;
        System.out.println("Switch is: " + power);

        if (clientInfo != null) {
            System.out.println(
                "Client's address is: " + clientInfo.getRemoteAddress()
            );
            System.out.println(
                "Received message on: " + clientInfo.getLocalAddress()
            );
            System.out.println(
                "Client's user agent is: " + clientInfo.getRequestUserAgent()
            );
            System.out.println(
                "Client's custom header is: " +
                clientInfo.getRequestHeaders().getFirstHeader("X-MY-HEADER")
            );

            // Return some extra headers in the response
            clientInfo.getExtraResponseHeaders().add("X-MY-HEADER", "foobar");
        }
    }
    // DOC:CLIENT_INFO

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget() {
        return power;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return power;
    }
}                                                                               // DOC:INC1
