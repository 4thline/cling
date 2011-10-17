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

import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;

/**
 * Reads and writes GENA XML content.
 *
 * @author Christian Bauer
 */
public interface GENAEventProcessor {

    /**
     * Transforms a collection of {@link org.fourthline.cling.model.state.StateVariableValue}s into an XML message body.
     *
     * @param requestMessage The message to transform.
     * @throws UnsupportedDataException
     */
    public void writeBody(OutgoingEventRequestMessage requestMessage) throws UnsupportedDataException;

    /**
     * Transforms an XML message body and adds to a collection of {@link org.fourthline.cling.model.state.StateVariableValue}s..
     *
     * @param requestMessage The message to transform.
     * @throws UnsupportedDataException
     */
    public void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException;

}