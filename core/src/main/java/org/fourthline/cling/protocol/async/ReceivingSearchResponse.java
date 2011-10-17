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

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.discovery.IncomingSearchResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.protocol.RetrieveRemoteDescriptors;

import java.util.logging.Logger;

/**
 * Handles reception of search response messages.
 * <p>
 * This protocol implementation is basically the same as
 * the {@link org.fourthline.cling.protocol.async.ReceivingNotification} protocol for
 * an <em>ALIVE</em> message.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingSearchResponse extends ReceivingAsync<IncomingSearchResponse> {

    final private static Logger log = Logger.getLogger(ReceivingSearchResponse.class.getName());

    public ReceivingSearchResponse(UpnpService upnpService, IncomingDatagramMessage<UpnpResponse> inputMessage) {
        super(upnpService, new IncomingSearchResponse(inputMessage));
    }

    protected void execute() {

        if (!getInputMessage().isSearchResponseMessage()) {
            log.fine("Ignoring invalid search response message: " + getInputMessage());
            return;
        }

        UDN udn = getInputMessage().getRootDeviceUDN();
        if (udn == null) {
            log.fine("Ignoring search response message without UDN: " + getInputMessage());
            return;
        }

        RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(getInputMessage());
        log.fine("Received device search response: " + rdIdentity);

        if (getUpnpService().getRegistry().update(rdIdentity)) {
            log.fine("Remote device was already known: " + udn);
            return;
        }

        RemoteDevice rd;
        try {
            rd = new RemoteDevice(rdIdentity);
        } catch (ValidationException ex) {
            log.warning("Validation errors of device during discovery: " + rdIdentity);
            for (ValidationError validationError : ex.getErrors()) {
                log.warning(validationError.toString());
            }
            return;
        }

        if (rdIdentity.getDescriptorURL() == null) {
            log.finer("Ignoring message without location URL header: " + getInputMessage());
            return;
        }

        if (rdIdentity.getMaxAgeSeconds() == null) {
            log.finer("Ignoring message without max-age header: " + getInputMessage());
            return;
        }

        // Unfortunately, we always have to retrieve the descriptor because at this point we
        // have no idea if it's a root or embedded device
        getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                new RetrieveRemoteDescriptors(getUpnpService(), rd)
        );

    }

}
