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

import org.fourthline.cling.binding.annotations.*;

/**
 * Explicitly naming related state variables
 * <p>
 * If your mapped action method does not match the name of a mapped state variable,
 * you have to provide the name of (any) argument's related state variable:
 * </p>
 * <a class="citation" href="javacode://this" style="include:INC1"/>
 * <p>
 * Here the method has the name <code>retrieveStatus</code>, which
 * you also have to override if you want it be known as a the
 * <code>GetStatus</code> UPnP action. Because it is no longer a JavaBean
 * accessor for <code>status</code>, it explicitly has to be linked with
 * the related state variable <code>Status</code>. You always have to
 * provide the related state variable name if your action has more than one
 * output argument.
 * </p>
 * <p>
 * The "related statevariable" detection algorithm in Cling has one more trick
 * up its sleeve however. The UPnP specification says that a state variable which
 * is only ever used to describe the type of an input or output argument should
 * be named with the prefix <code>A_ARG_TYPE_</code>. So if you do not name the
 * related state variable of your action argument, Cling will also
 * look for a state variable with the name
 * <code>A_ARG_TYPE_[Name Of Your Argument]</code>. In the example above, Cling
 * is therefore also searching (unsuccessfully) for a state variable named
 * <code>A_ARG_TYPE_ResultStatus</code>. (Given that direct querying
 * of state variables is already deprecated in UDA 1.0, there are <em>NO</em>
 * state variables which are anything but type declarations for action input/output
 * arguments. This is a good example why UPnP is such a horrid specification.)
 * </p>
 */
@UpnpService(
        serviceId = @UpnpServiceId("SwitchPower"),
        serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
)
public class SwitchPowerNamedStateVariable {

    @UpnpStateVariable(defaultValue = "0", sendEvents = false)
    private boolean target = false;

    @UpnpStateVariable(defaultValue = "0")
    private boolean status = false;

    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = "NewTargetValue")
                          boolean newTargetValue) {
        target = newTargetValue;
        status = newTargetValue;
        System.out.println("Switch is: " + status);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget() {
        return target;
    }

    @UpnpAction(                                    // DOC:INC1
            name = "GetStatus",
            out = @UpnpOutputArgument(
                    name = "ResultStatus",
                    stateVariable = "Status"
            )
    )
    public boolean retrieveStatus() {
        return status;
    }                                               // DOC:INC1

}
