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

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.UpnpService;

/**
 * Supertype for all synchronously executing protocols, sending UPnP messages.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its {@link #executeSync()} method.
 * </p>
 *
 * @param <IN> The type of request UPnP message send by this protocol.
 * @param <OUT> The type of response UPnP message expected by this protocol.
 *
 * @author Christian Bauer
 */
public abstract class SendingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends SendingAsync {

    final private IN inputMessage;
    protected OUT outputMessage;

    protected SendingSync(UpnpService upnpService, IN inputMessage) {
        super(upnpService);
        this.inputMessage = inputMessage;
    }

    public IN getInputMessage() {
        return inputMessage;
    }

    public OUT getOutputMessage() {
        return outputMessage;
    }

    final protected void execute() {
        outputMessage = executeSync();
    }

    protected abstract OUT executeSync();

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}