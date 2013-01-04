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
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.message.control.ActionResponseMessage;

/**
 * Converts UPnP SOAP messages from/to action invocations.
 * <p>
 * The UPnP protocol layer processes local and remote {@link org.fourthline.cling.model.action.ActionInvocation}
 * instances. The UPnP transport layer accepts and returns {@link org.fourthline.cling.model.message.StreamRequestMessage}s
 * and {@link org.fourthline.cling.model.message.StreamResponseMessage}s. This processor is an adapter between the
 * two layers, reading and writing SOAP content.
 * </p>
 *
 * @author Christian Bauer
 */
public interface SOAPActionProcessor {

    /**
     * Converts the given invocation input into SOAP XML content, setting on the given request message.
     *
     * @param requestMessage The request message on which the SOAP content is set.
     * @param actionInvocation The action invocation from which input argument values are read.
     * @throws org.fourthline.cling.model.UnsupportedDataException
     */
    public void writeBody(ActionRequestMessage requestMessage, ActionInvocation actionInvocation) throws UnsupportedDataException;

    /**
     * Converts the given invocation output into SOAP XML content, setting on the given response message.
     *
     * @param responseMessage The response message on which the SOAP content is set.
     * @param actionInvocation The action invocation from which output argument values are read.
     * @throws UnsupportedDataException
     */
    public void writeBody(ActionResponseMessage responseMessage, ActionInvocation actionInvocation) throws UnsupportedDataException;

    /**
     * Converts SOAP XML content of the request message and sets input argument values on the given invocation.
     *
     * @param requestMessage The request message from which SOAP content is read.
     * @param actionInvocation The action invocation on which input argument values are set.
     * @throws UnsupportedDataException
     */
    public void readBody(ActionRequestMessage requestMessage, ActionInvocation actionInvocation) throws UnsupportedDataException;

    /**
     * Converts SOAP XML content of the response message and sets output argument values on the given invocation.
     *
     * @param responseMsg The response message from which SOAP content is read.
     * @param actionInvocation The action invocation on which output argument values are set.
     * @throws UnsupportedDataException
     */
    public void readBody(ActionResponseMessage responseMsg, ActionInvocation actionInvocation) throws UnsupportedDataException;

}
