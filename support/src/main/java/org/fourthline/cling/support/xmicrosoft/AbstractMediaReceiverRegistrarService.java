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

package org.fourthline.cling.support.xmicrosoft;

import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;

import java.beans.PropertyChangeSupport;

/**
 * Basic implementation of service required by MSFT devices such as XBox 360.
 *
 * @author Mario Franco
 */
@UpnpService(
        serviceId = @UpnpServiceId(
                namespace = "microsoft.com",
                value = "X_MS_MediaReceiverRegistrar"
        ),
        serviceType = @UpnpServiceType(
                namespace = "microsoft.com",
                value = "X_MS_MediaReceiverRegistrar",
                version = 1
        )
)
@UpnpStateVariables(
        {
                @UpnpStateVariable(name = "A_ARG_TYPE_DeviceID",
                                   sendEvents = false,
                                   datatype = "string"),
                @UpnpStateVariable(name = "A_ARG_TYPE_Result",
                                   sendEvents = false,
                                   datatype = "int"),
                @UpnpStateVariable(name = "A_ARG_TYPE_RegistrationReqMsg",
                                   sendEvents = false,
                                   datatype = "bin.base64"),
                @UpnpStateVariable(name = "A_ARG_TYPE_RegistrationRespMsg",
                                   sendEvents = false,
                                   datatype = "bin.base64")
        }
)
public abstract class AbstractMediaReceiverRegistrarService {

    final protected PropertyChangeSupport propertyChangeSupport;

    @UpnpStateVariable(eventMinimumDelta = 1)
    private UnsignedIntegerEightBytes authorizationGrantedUpdateID = new UnsignedIntegerEightBytes(0);

    @UpnpStateVariable(eventMinimumDelta = 1)
    private UnsignedIntegerEightBytes authorizationDeniedUpdateID = new UnsignedIntegerEightBytes(0);

    @UpnpStateVariable
    private UnsignedIntegerEightBytes validationSucceededUpdateID = new UnsignedIntegerEightBytes(0);

    @UpnpStateVariable
    private UnsignedIntegerEightBytes validationRevokedUpdateID = new UnsignedIntegerEightBytes(0);

    protected AbstractMediaReceiverRegistrarService() {
        this(null);
    }

    protected AbstractMediaReceiverRegistrarService(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport != null ? propertyChangeSupport : new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }


    @UpnpAction(out = @UpnpOutputArgument(name = "AuthorizationGrantedUpdateID"))
    public UnsignedIntegerEightBytes getAuthorizationGrantedUpdateID() {
        return authorizationGrantedUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "AuthorizationDeniedUpdateID"))
    public UnsignedIntegerEightBytes getAuthorizationDeniedUpdateID() {
        return authorizationDeniedUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ValidationSucceededUpdateID"))
    public UnsignedIntegerEightBytes getValidationSucceededUpdateID() {
        return validationSucceededUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ValidationRevokedUpdateID"))
    public UnsignedIntegerEightBytes getValidationRevokedUpdateID() {
        return validationRevokedUpdateID;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                                stateVariable = "A_ARG_TYPE_Result")
    })
    public int isAuthorized(@UpnpInputArgument(name = "DeviceID",
                                                   stateVariable = "A_ARG_TYPE_DeviceID")
                                String deviceID) {
        return 1;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                                stateVariable = "A_ARG_TYPE_Result")
    })
    public int isValidated(@UpnpInputArgument(name = "DeviceID",
                                                  stateVariable = "A_ARG_TYPE_DeviceID")
                               String deviceID) {
        return 1;
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "RegistrationRespMsg",
                                stateVariable = "A_ARG_TYPE_RegistrationRespMsg")
    })
    public byte[] registerDevice(@UpnpInputArgument(name = "RegistrationReqMsg",
                                                    stateVariable = "A_ARG_TYPE_RegistrationReqMsg")
                                 byte[] registrationReqMsg) {
        return new byte[]{};
    }
}
