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

package org.fourthline.cling.support.xmicrosoft;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

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
    private UnsignedIntegerFourBytes authorizationGrantedUpdateID = new UnsignedIntegerFourBytes(0);

    @UpnpStateVariable(eventMinimumDelta = 1)
    private UnsignedIntegerFourBytes authorizationDeniedUpdateID = new UnsignedIntegerFourBytes(0);

    @UpnpStateVariable
    private UnsignedIntegerFourBytes validationSucceededUpdateID = new UnsignedIntegerFourBytes(0);

    @UpnpStateVariable
    private UnsignedIntegerFourBytes validationRevokedUpdateID = new UnsignedIntegerFourBytes(0);

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
    public UnsignedIntegerFourBytes getAuthorizationGrantedUpdateID() {
        return authorizationGrantedUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "AuthorizationDeniedUpdateID"))
    public UnsignedIntegerFourBytes getAuthorizationDeniedUpdateID() {
        return authorizationDeniedUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ValidationSucceededUpdateID"))
    public UnsignedIntegerFourBytes getValidationSucceededUpdateID() {
        return validationSucceededUpdateID;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "ValidationRevokedUpdateID"))
    public UnsignedIntegerFourBytes getValidationRevokedUpdateID() {
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
