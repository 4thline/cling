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
 * Mapping state variables
 * <p>
 * The following example only has a single field named <code>power</code>,
 * however, the UPnP service requires two state variables. In this case
 * you declare the UPnP state variables with annotations on the class:
 * </p>
 * <a class="citation" href="javacode://this" style="include:INC1"/>
 * <p>
 * The <code>power</code> field is not mapped to the state variables and
 * you are free to design your service internals as you like. Did you
 * notice that you never declared the datatype of your state variables?
 * Also, how can Cling read the "current state" of your service for GENA
 * subscribers or when a "query state variable" action is received?
 * Both questions have the same answer.
 * </p>
 * <p>
 * Let's consider GENA eventing first. This example has an evented
 * state variable called <code>Status</code>, and if a control point
 * subscribes to the service to be notified of changes, how
 * will Cling obtain the current status? If you'd have used
 * <code>@UpnpStateVariable</code> on your fields, Cling would then
 * directly access field values through Java Reflection. On the other
 * hand if you declare state variables not on fields but on your service
 * class, Cling will during binding detect any JavaBean-style getter
 * method that matches the derived property name of the state variable.
 * </p>
 * <p>
 * In other words, Cling will discover that your class has a
 * <code>getStatus()</code> method. It doesn't matter if that method
 * is also an action-mapped method, the important thing is that it
 * matches JavaBean property naming conventions. The <code>Status</code>
 * UPnP state variable maps to the <code>status</code> property, which
 * is expected to have a <code>getStatus()</code> accessor method.
 * Cling will use this method to read the current state of your
 * service for GENA subscribers and when the state variable is
 * manually queried.
 * </p>
 * <p>
 * If you do not provide a UPnP datatype name in your
 * <code>@UpnpStateVariable</code> annotation, Cling will use the type
 * of the annotated field or discovered JavaBean getter method to
 * figure out the type. The supported default mappings between Java types
 * and UPnP datatypes are shown in the following table:
 * </p>
 * <table class="infotable halfwidth" border="1">
 * <thead><tr>
 * <th>Java Type</th>
 * <th class="halfwidth">UPnP Datatype</th>
 * </tr></thead>
 * <tbody>
 * <tr><td><code>java.lang.Boolean</code></td><td><code>boolean</code></td></tr>
 * <tr><td><code>boolean</code></td><td><code>boolean</code></td></tr>
 * <tr><td><code>java.lang.Short</code></td><td><code>i2</code></td></tr>
 * <tr><td><code>short</code></td><td><code>i2</code></td></tr>
 * <tr><td><code>java.lang.Integer</code></td><td><code>i4</code></td></tr>
 * <tr><td><code>int</code></td><td><code>i4</code></td></tr>
 * <tr><td><code>org.fourthline.cling.model.types.UnsignedIntegerOneByte</code></td><td><code>ui1</code></td></tr>
 * <tr><td><code>org.fourthline.cling.model.types.UnsignedIntegerTwoBytes</code></td><td><code>ui2</code></td></tr>
 * <tr><td><code>org.fourthline.cling.model.types.UnsignedIntegerFourBytes</code></td><td><code>ui4</code></td></tr>
 * <tr><td><code>java.lang.Float</code></td><td><code>r4</code></td></tr>
 * <tr><td><code>float</code></td><td><code>r4</code></td></tr>
 * <tr><td><code>java.lang.Double</code></td><td><code>float</code></td></tr>
 * <tr><td><code>double</code></td><td><code>float</code></td></tr>
 * <tr><td><code>java.lang.Character</code></td><td><code>char</code></td></tr>
 * <tr><td><code>char</code></td><td><code>char</code></td></tr>
 * <tr><td><code>java.lang.String</code></td><td><code>string</code></td></tr>
 * <tr><td><code>java.util.Calendar</code></td><td><code>datetime</code></td></tr>
 * <tr><td><code>byte[]</code></td><td><code>bin.base64</code></td></tr>
 * <tr><td><code>java.net.URI</code></td><td><code>uri</code></td></tr>
 * </tbody>
 * </table>
 *
 */
@UpnpService(                                                                   // DOC:INC1
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
public class SwitchPowerAnnotatedClass {

    private boolean power;

    @UpnpAction
    public void setTarget(@UpnpInputArgument(name = "NewTargetValue")
                          boolean newTargetValue) {
        power = newTargetValue;
        System.out.println("Switch is: " + power);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "RetTargetValue"))
    public boolean getTarget() {
        return power;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ResultStatus"))
    public boolean getStatus() {
        return power;
    }
}                                                                               // DOC:INC1
