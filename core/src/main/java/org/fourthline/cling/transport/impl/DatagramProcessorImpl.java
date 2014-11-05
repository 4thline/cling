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

package org.fourthline.cling.transport.impl;

import java.util.logging.Logger;
import java.util.logging.Level;

import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpOperation;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.http.Headers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Locale;

/**
 * Default implementation.
 * 
 * @author Christian Bauer
 */
public class DatagramProcessorImpl implements DatagramProcessor {

    private static Logger log = Logger.getLogger(DatagramProcessor.class.getName());

    public IncomingDatagramMessage read(InetAddress receivedOnAddress, DatagramPacket datagram) throws UnsupportedDataException {

        try {

            if (log.isLoggable(Level.FINER)) {
                log.finer("===================================== DATAGRAM BEGIN ============================================");
                log.finer(new String(datagram.getData(), "UTF-8"));
                log.finer("-===================================== DATAGRAM END =============================================");
            }

            ByteArrayInputStream is = new ByteArrayInputStream(datagram.getData());

            String[] startLine = Headers.readLine(is).split(" ");
            if (startLine[0].startsWith("HTTP/1.")) {
                return readResponseMessage(receivedOnAddress, datagram, is, Integer.valueOf(startLine[1]), startLine[2], startLine[0]);
            } else {
                return readRequestMessage(receivedOnAddress, datagram, is, startLine[0], startLine[2]);
            }

        } catch (Exception ex) {
            throw new UnsupportedDataException("Could not parse headers: " + ex, ex, datagram.getData());
        }
    }

    public DatagramPacket write(OutgoingDatagramMessage message) throws UnsupportedDataException {

        StringBuilder statusLine = new StringBuilder();

        UpnpOperation operation = message.getOperation();

        if (operation instanceof UpnpRequest) {

            UpnpRequest requestOperation = (UpnpRequest) operation;
            statusLine.append(requestOperation.getHttpMethodName()).append(" * ");
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append("\r\n");

        } else if (operation instanceof UpnpResponse) {
            UpnpResponse responseOperation = (UpnpResponse) operation;
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append(" ");
            statusLine.append(responseOperation.getStatusCode()).append(" ").append(responseOperation.getStatusMessage());
            statusLine.append("\r\n");
        } else {
            throw new UnsupportedDataException(
                    "Message operation is not request or response, don't know how to process: " + message
            );
        }

        // UDA 1.0, 1.1.2: No body but message must have a blank line after header
        StringBuilder messageData = new StringBuilder();
        messageData.append(statusLine);

        messageData.append(message.getHeaders().toString()).append("\r\n");

        if (log.isLoggable(Level.FINER)) {
            log.finer("Writing message data for: " + message);
            log.finer("---------------------------------------------------------------------------------");
            log.finer(messageData.toString().substring(0, messageData.length() - 2)); // Don't print the blank lines
            log.finer("---------------------------------------------------------------------------------");
        }

        try {
            // According to HTTP 1.0 RFC, headers and their values are US-ASCII
            // TODO: Probably should look into escaping rules, too
            byte[] data = messageData.toString().getBytes("US-ASCII");

            log.fine("Writing new datagram packet with " + data.length + " bytes for: " + message);
            return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());

        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedDataException(
                "Can't convert message content to US-ASCII: " + ex.getMessage(), ex, messageData
            );
        }
    }

    protected IncomingDatagramMessage readRequestMessage(InetAddress receivedOnAddress,
                                                         DatagramPacket datagram,
                                                         ByteArrayInputStream is,
                                                         String requestMethod,
                                                         String httpProtocol) throws Exception {

        // Headers
        UpnpHeaders headers = new UpnpHeaders(is);

        // Assemble message
        IncomingDatagramMessage requestMessage;
        UpnpRequest upnpRequest = new UpnpRequest(UpnpRequest.Method.getByHttpName(requestMethod));
        upnpRequest.setHttpMinorVersion(httpProtocol.toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
        requestMessage = new IncomingDatagramMessage(upnpRequest, datagram.getAddress(), datagram.getPort(), receivedOnAddress);

        requestMessage.setHeaders(headers);

        return requestMessage;
    }

    protected IncomingDatagramMessage readResponseMessage(InetAddress receivedOnAddress,
                                                          DatagramPacket datagram,
                                                          ByteArrayInputStream is,
                                                          int statusCode,
                                                          String statusMessage,
                                                          String httpProtocol) throws Exception {

        // Headers
        UpnpHeaders headers = new UpnpHeaders(is);

        // Assemble the message
        IncomingDatagramMessage responseMessage;
        UpnpResponse upnpResponse = new UpnpResponse(statusCode, statusMessage);
        upnpResponse.setHttpMinorVersion(httpProtocol.toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
        responseMessage = new IncomingDatagramMessage(upnpResponse, datagram.getAddress(), datagram.getPort(), receivedOnAddress);

        responseMessage.setHeaders(headers);

        return responseMessage;
    }


}
