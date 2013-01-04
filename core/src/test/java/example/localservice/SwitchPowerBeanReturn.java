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
 * Getting output values from a JavaBean
 * <p>
 * Here the action method does not return the output argument value directly,
 * but a JavaBean instance is returned which offers a getter method to obtain
 * the output argument value:
 * </p>
 * <a class="citation" href="javacode://this" style="include:INC1"/>
 * <p>
 * Cling will detect that you mapped a getter name in the output argument
 * and that the action method is not <code>void</code>. It now expects that
 * it will find the getter method on the returned JavaBean. If there are
 * several output arguments, all of them have to be mapped to getter methods
 * on the returned JavaBean.
 * </p>
 */
@UpnpService(
        serviceId = @UpnpServiceId("SwitchPower"),
        serviceType = @UpnpServiceType(value = "SwitchPower", version = 1)
)
public class SwitchPowerBeanReturn {

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
                    getterName = "getWrapped"
            )
    )
    public StatusHolder getStatus() {
        return new StatusHolder(status);
    }

    public class StatusHolder {
        boolean wrapped;

        public StatusHolder(boolean wrapped) {
            this.wrapped = wrapped;
        }

        public boolean getWrapped() {
            return wrapped;
        }
    }                                               // DOC:INC1

}
