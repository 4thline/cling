/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.seamless.util.Exceptions;
import org.seamless.util.io.IO;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation based on Servlet 3.0 API.
 *
 * @author Christian Bauer
 */
public class AsyncServletUpnpStream extends UpnpStream implements AsyncListener {

    final private static Logger log = Logger.getLogger(UpnpStream.class.getName());

    final protected AsyncContext asyncContext;
    final protected HttpServletRequest request;
    protected volatile boolean isOpen;

    protected StreamResponseMessage responseMessage;

    public AsyncServletUpnpStream(ProtocolFactory protocolFactory,
                                  AsyncContext asyncContext,
                                  HttpServletRequest request) {
        super(protocolFactory);
        this.asyncContext = asyncContext;
        this.request = request;
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        ServletResponse response;
        if ((response = asyncContext.getResponse()) == null) {
            throw new IllegalStateException(
                "Couldn't get response from asynchronous context, already timed out"
            );
        }
        return (HttpServletResponse) response;
    }

    protected void complete() {
        asyncContext.complete();
    }

    @Override
    public void run() {
        try {
            StreamRequestMessage requestMessage = readRequestMessage();
            if (log.isLoggable(Level.FINER))
                log.finer("Processing new request message: " + requestMessage);

            responseMessage = process(requestMessage);

            if (responseMessage != null) {
                if (log.isLoggable(Level.FINER))
                    log.finer("Preparing HTTP response message: " + responseMessage);
                writeResponseMessage(responseMessage);
            } else {
                // If it's null, it's 404
                if (log.isLoggable(Level.FINER))
                    log.finer("Sending HTTP response status: " + HttpURLConnection.HTTP_NOT_FOUND);
                getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Throwable t) {
            log.finer("Exception occurred during UPnP stream processing: " + t);
            if (log.isLoggable(Level.FINER)) {
                log.log(Level.FINER, "Cause: " + Exceptions.unwrap(t), Exceptions.unwrap(t));
            }
            if (!getResponse().isCommitted()) {
                log.finer("Returning INTERNAL SERVER ERROR to client");
                getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                log.info("Could not return INTERNAL SERVER ERROR to client, response was already committed");
            }
            responseException(t);
        } finally {
            complete();
        }
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        if (log.isLoggable(Level.FINER))
            log.finer("Starting asynchronous processing of HTTP request: " + event.getSuppliedRequest());
        isOpen = true;
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        if (log.isLoggable(Level.FINER))
            log.finer("Completed asynchronous processing of HTTP request: " + event.getSuppliedRequest());
        isOpen = false;
        responseSent(responseMessage);
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        if (log.isLoggable(Level.FINER))
            log.finer("Asynchronous processing of HTTP request timed out: " + event.getSuppliedRequest());
        isOpen = false;
        responseException(new Exception("Asynchronous request timed out"));
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        if (log.isLoggable(Level.FINER))
            log.finer("Asynchronous processing of HTTP request error: " + event.getThrowable());
        isOpen = false;
        responseException(event.getThrowable());
    }

    protected StreamRequestMessage readRequestMessage() throws IOException {
        // Extract what we need from the HTTP httpRequest
        String requestMethod = getRequest().getMethod();
        String requestURI = getRequest().getRequestURI();

        if (log.isLoggable(Level.FINER))
            log.finer("Processing HTTP request: " + requestMethod + " " + requestURI);

        StreamRequestMessage requestMessage;
        try {
            requestMessage =
                new StreamRequestMessage(
                    UpnpRequest.Method.getByHttpName(requestMethod),
                    URI.create(requestURI)
                );
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid request URI: " + requestURI, ex);
        }

        if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
            throw new RuntimeException("Method not supported: " + requestMethod);
        }

        // Connection wrapper
        requestMessage.setConnection(new Connection() {
            @Override
            public boolean isOpen() {
                return isOpen;
            }

            @Override
            public InetAddress getRemoteAddress() {
                try {
                    return InetAddress.getByName(getRequest().getRemoteAddr());
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public InetAddress getLocalAddress() {
                try {
                    return InetAddress.getByName(getRequest().getLocalAddr());
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Headers
        UpnpHeaders headers = new UpnpHeaders();
        Enumeration<String> headerNames = getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = getRequest().getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
        requestMessage.setHeaders(headers);

        // Body
        byte[] bodyBytes;
        InputStream is = null;
        try {
            is = getRequest().getInputStream();
            bodyBytes = IO.readBytes(is);
        } finally {
            if (is != null)
                is.close();
        }
        if (log.isLoggable(Level.FINER))
            log.finer("Reading request body bytes: " + bodyBytes.length);

        if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {

            if (log.isLoggable(Level.FINER))
                log.finer("Request contains textual entity body, converting then setting string on message");
            requestMessage.setBodyCharacters(bodyBytes);

        } else if (bodyBytes.length > 0) {

            if (log.isLoggable(Level.FINER))
                log.finer("Request contains binary entity body, setting bytes on message");
            requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);

        } else {
            if (log.isLoggable(Level.FINER))
                log.finer("Request did not contain entity body");
        }

        return requestMessage;
    }

    protected void writeResponseMessage(StreamResponseMessage responseMessage) throws IOException {
        // Headers
        for (Map.Entry<String, List<String>> entry : responseMessage.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                getResponse().addHeader(entry.getKey(), value);
            }
        }
        // The Date header is recommended in UDA
        getResponse().setDateHeader("Date", System.currentTimeMillis());

        if (log.isLoggable(Level.FINER))
            log.finer("Sending HTTP response status: " + responseMessage.getOperation().getStatusCode());

        getResponse().setStatus(responseMessage.getOperation().getStatusCode());

        // Body
        byte[] responseBodyBytes = responseMessage.hasBody() ? responseMessage.getBodyBytes() : null;
        int contentLength = responseBodyBytes != null ? responseBodyBytes.length : -1;

        if (contentLength > 0) {
            getResponse().setContentLength(contentLength);
            log.finer("Response message has body, writing bytes to stream...");
            IO.writeBytes(getResponse().getOutputStream(), responseBodyBytes);
        }
    }

}
