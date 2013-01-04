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

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.UnsupportedDataException;
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
     * @throws org.fourthline.cling.model.UnsupportedDataException
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