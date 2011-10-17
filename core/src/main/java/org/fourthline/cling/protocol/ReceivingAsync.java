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
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.header.UpnpHeader;

/**
 * Supertype for all asynchronously executing protocols, handling reception of UPnP messages.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its own {@link #waitBeforeExecution()} method. By default, the protocol does not wait
 * before then proceeding with {@link #execute()}.
 * </p>
 *
 * @param <M> The type of UPnP message handled by this protocol.
 *
 * @author Christian Bauer
 */
public abstract class ReceivingAsync<M extends UpnpMessage> implements Runnable {

    private final UpnpService upnpService;

    private M inputMessage;

    protected ReceivingAsync(UpnpService upnpService, M inputMessage) {
        this.upnpService = upnpService;
        this.inputMessage = inputMessage;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public M getInputMessage() {
        return inputMessage;
    }

    public void run() {
        boolean proceed;
        try {
            proceed = waitBeforeExecution();
        } catch (InterruptedException ex) {
            proceed = false;
        }

        if (proceed) {
            execute();
        }
    }

    /**
     * Provides an opportunity to pause before executing the protocol.
     *
     * @return <code>true</code> (default) if execution should continue after waiting.
     *
     * @throws InterruptedException If waiting has been interrupted, which also stops execution.
     */
    protected boolean waitBeforeExecution() throws InterruptedException {
        // Don't wait by default
        return true;
    }

    protected abstract void execute();

    protected <H extends UpnpHeader> H getFirstHeader(UpnpHeader.Type headerType, Class<H> subtype) {
        return getInputMessage().getHeaders().getFirstHeader(headerType, subtype);
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}
