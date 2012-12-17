/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

            // In potentially long-running action methods, regularly check if you
            // should continue processing, e.g. if the client connection was closed...
            if (!clientInfo.isRequestCancelled())
                System.out.println("Connection/thread still active, continuing...");
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
