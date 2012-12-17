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

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;

/**
 * Service for sending TCP (HTTP) stream request messages.
 * 
 * <p>
 * An implementation has to be thread-safe.
 * Its constructor may throw {@link org.fourthline.cling.transport.spi.InitializationException}.
 * </p>
 *
 * @param <C> The type of the service's configuration.
 *
 * @author Christian Bauer
 */
public interface StreamClient<C extends StreamClientConfiguration> {

    /**
     * Sends the given request via TCP (HTTP) and returns the response.
     * <p>
     * This method will always try to complete execution without throwing an exception. It will
     * return <code>null</code> if an error occurs, and optionally log any exception messages.
     * </p>
     * <p>
     * This method <strong>is required</strong> to add a <code>Host</code> HTTP header to the
     * outgoing HTTP request, even if the given
     * {@link org.fourthline.cling.model.message.StreamRequestMessage} does not contain such a header.
     * </p>
     * <p>
     * This method will add the <code>User-Agent</code> HTTP header to the outgoing HTTP request if
     * the given message did not already contain such a header. You can set this default value in your
     * {@link StreamClientConfiguration}.
     * </p>
     *
     * @param message The message to send.
     * @return The response or <code>null</code> if no response has been received or an error occured.
     */
    public StreamResponseMessage sendRequest(StreamRequestMessage message);

    /**
     * Stops the service, closes any connection pools etc.
     */
    public void stop();

    /**
     * @return This service's configuration.
     */
    public C getConfiguration();

}
