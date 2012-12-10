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

package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.profile.ClientInfo;

import java.util.logging.Logger;

/**
 * Supertype for all synchronously executing protocols, handling reception of UPnP messages and return a response.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its own {@link #waitBeforeExecution()} method. By default, the protocol does not wait
 * before then proceeding with {@link #executeSync()}.
 * </p>
 * <p>
 * The returned response will be available to the client of this protocol. The
 * client will then call either {@link #responseSent(org.fourthline.cling.model.message.StreamResponseMessage)}
 * or {@link #responseException(Throwable)}, depending on whether the response was successfully
 * delivered. The protocol can override these methods to decide if the whole procedure it is
 * implementing was successful or not, including not only creation but also delivery of the response.
 * </p>
 *
 * @param <IN> The type of incoming UPnP message handled by this protocol.
 * @param <OUT> The type of response UPnP message created by this protocol.
 *
 * @author Christian Bauer
 */
public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN> {

    final private static Logger log = Logger.getLogger(ReceivingSync.class.getName());

    final protected ClientInfo clientInfo;
    protected OUT outputMessage;

    protected ReceivingSync(UpnpService upnpService, IN inputMessage) {
        super(upnpService, inputMessage);
        this.clientInfo = new ClientInfo(inputMessage);
    }

    public OUT getOutputMessage() {
        return outputMessage;
    }

    final protected void execute() {
        outputMessage = executeSync();

        if (outputMessage != null && getClientInfo().getExtraResponseHeaders().size() > 0) {
            log.fine("Merging extra headers into response message: " + getClientInfo().getExtraResponseHeaders().size());
            outputMessage.getHeaders().putAll(getClientInfo().getExtraResponseHeaders());
        }
    }

    protected abstract OUT executeSync();

    /**
     * Called by the client of this protocol after the returned response has been successfully delivered.
     * <p>
     * NOOP by default.
     * </p>
     */
    public void responseSent(StreamResponseMessage responseMessage) {
    }

    /**
     * Called by the client of this protocol if the returned response was not delivered.
     * <p>
     * NOOP by default.
     * </p>
     *
     * @param t The reason why the response wasn't delivered.
     */
    public void responseException(Throwable t) {
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}
