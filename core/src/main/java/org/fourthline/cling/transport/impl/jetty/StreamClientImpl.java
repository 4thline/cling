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

package org.fourthline.cling.transport.impl.jetty;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamClient;
import org.seamless.util.Exceptions;
import org.seamless.util.MimeType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation based on Jetty 8 client API.
 * <p>
 * This implementation works on Android, dependencies are the <code>jetty-client</code>
 * Maven module.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamClientImpl implements StreamClient {

    final private static Logger log = Logger.getLogger(StreamClient.class.getName());

    final protected StreamClientConfigurationImpl configuration;
    final protected HttpClient client;

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;

        log.info("Starting Jetty HttpClient...");
        client = new HttpClient();
        client.setThreadPool(new ExecutorThreadPool(configuration.getExecutorService()));
        client.setTimeout(configuration.getResponseTimeoutSeconds() * 1000);
        client.setConnectTimeout(configuration.getConnectionTimeoutSeconds() * 1000);
        client.setMaxRetries(0);

        try {
            client.start();
        } catch (Exception ex) {
            throw new InitializationException(
                "Could not start Jetty HTTP client: " + ex, ex
            );
        }
    }

    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) {
        try {
            HttpContentExchange exchange = createHttpContentExchange(getConfiguration(), requestMessage);

            client.send(exchange);

            // Execute synchronously, this means the HttpClient will use the thread
            // from the configured ExecutorService to do the work (which by default is the
            // current thread) and also stay within this thread waiting for the response.
            int exchangeState = exchange.waitForDone();

            if (exchangeState == HttpExchange.STATUS_COMPLETED) {
                try {
                    return exchange.createResponse();
                } catch (Throwable t) {
                    log.warning("Error reading response: " + requestMessage);
                    log.warning("Cause: " + Exceptions.unwrap(t));
                    return null;
                }
            } else if (exchangeState == HttpExchange.STATUS_EXCEPTED) {
                return null;
            } else if (exchangeState == HttpExchange.STATUS_EXPIRED) {
                log.warning("Timeout while waiting for HTTP exchange to complete: " + requestMessage);
                return null;
            } else {
                log.warning("Unhandled HTTP exchange status: " + exchangeState);
                return null;
            }

        } catch (IOException ex) {
            log.warning("Client connection for '" + requestMessage + "' was aborted: " + ex);
            return null;
        } catch (InterruptedException ex) {
            log.warning("Interrupted while waiting for HTTP response for '" + requestMessage + "': " + ex);
            return null;
        }
    }

    @Override
    public void stop() {
        try {
            client.stop();
        } catch (Exception ex) {
            log.info("Error stopping HTTP client: " + ex);
        }
    }

    protected HttpContentExchange createHttpContentExchange(StreamClientConfigurationImpl configuration,
                                                            StreamRequestMessage requestMessage) {
        return new HttpContentExchange(configuration, requestMessage);
    }

    static public class HttpContentExchange extends ContentExchange {

        final protected StreamClientConfigurationImpl configuration;
        final protected StreamRequestMessage requestMessage;

        public HttpContentExchange(StreamClientConfigurationImpl configuration,
                                   StreamRequestMessage requestMessage) {
            super(true);
            this.configuration = configuration;
            this.requestMessage = requestMessage;
            applyRequestURLMethod();
            applyRequestHeaders();
            applyRequestBody();
        }

        @Override
        protected void onConnectionFailed(Throwable t) {
            log.warning("Can't connect to HTTP server: " + requestMessage);
            log.warning("Cause: " + Exceptions.unwrap(t));
        }

        @Override
        protected void onException(Throwable t) {
            log.warning("Exception while processing HTTP exchange: " + getRequestMessage());
            log.warning("Cause: " + Exceptions.unwrap(t));
        }

        public StreamClientConfigurationImpl getConfiguration() {
            return configuration;
        }

        public StreamRequestMessage getRequestMessage() {
            return requestMessage;
        }

        protected void applyRequestURLMethod() {
            final UpnpRequest requestOperation = getRequestMessage().getOperation();
            if (log.isLoggable(Level.FINE))
                log.fine(
                    "Preparing HTTP request message with method '"
                        + requestOperation.getHttpMethodName()
                        + "': " + getRequestMessage()
                );

            setURL(requestOperation.getURI().toString());
            setMethod(requestOperation.getHttpMethodName());
        }

        protected void applyRequestHeaders() {
            // Headers
            UpnpHeaders headers = getRequestMessage().getHeaders();
            if (log.isLoggable(Level.FINE))
                log.fine("Writing headers on HttpContentExchange: " + headers.size());
            // TODO Always add the Host header
            // TODO: ? setRequestHeader(UpnpHeader.Type.HOST.getHttpName(), );
            // Add the default user agent if not already set on the message
            if (!headers.containsKey(UpnpHeader.Type.USER_AGENT)) {
                setRequestHeader(
                    UpnpHeader.Type.USER_AGENT.getHttpName(),
                    getConfiguration().getUserAgentValue(
                        getRequestMessage().getUdaMajorVersion(),
                        getRequestMessage().getUdaMinorVersion())
                );
            }
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                for (String v : entry.getValue()) {
                    String headerName = entry.getKey();
                    if (log.isLoggable(Level.FINE))
                        log.fine("Setting header '" + headerName + "': " + v);
                    addRequestHeader(headerName, v);
                }
            }
        }

        protected void applyRequestBody() {
            // Body
            if (getRequestMessage().hasBody()) {
                if (getRequestMessage().getBodyType() == UpnpMessage.BodyType.STRING) {
                    if (log.isLoggable(Level.FINE))
                        log.fine("Writing textual request body: " + getRequestMessage());

                    MimeType contentType =
                        getRequestMessage().getContentTypeHeader() != null
                            ? getRequestMessage().getContentTypeHeader().getValue()
                            : ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8;

                    String charset =
                        getRequestMessage().getContentTypeCharset() != null
                            ? getRequestMessage().getContentTypeCharset()
                            : "UTF-8";

                    setRequestContentType(contentType.toString());
                    ByteArrayBuffer buffer;
                    try {
                        buffer = new ByteArrayBuffer(getRequestMessage().getBodyString(), charset);
                    } catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException("Unsupported character encoding: " + charset, ex);
                    }
                    setRequestHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(buffer.length()));
                    setRequestContent(buffer);

                } else {
                    if (log.isLoggable(Level.FINE))
                        log.fine("Writing binary request body: " + getRequestMessage());

                    if (getRequestMessage().getContentTypeHeader() == null)
                        throw new RuntimeException(
                            "Missing content type header in request message: " + requestMessage
                        );
                    MimeType contentType = getRequestMessage().getContentTypeHeader().getValue();

                    setRequestContentType(contentType.toString());
                    ByteArrayBuffer buffer;
                    buffer = new ByteArrayBuffer(getRequestMessage().getBodyBytes());
                    setRequestHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(buffer.length()));
                    setRequestContent(buffer);
                }
            }
        }

        protected StreamResponseMessage createResponse() {
            // Status
            UpnpResponse responseOperation =
                new UpnpResponse(
                    getResponseStatus(),
                    UpnpResponse.Status.getByStatusCode(getResponseStatus()).getStatusMsg()
                );

            if (log.isLoggable(Level.FINE))
                log.fine("Received response: " + responseOperation);

            StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);

            // Headers
            UpnpHeaders headers = new UpnpHeaders();
            HttpFields responseFields = getResponseFields();
            for (String name : responseFields.getFieldNamesCollection()) {
                for (String value : responseFields.getValuesCollection(name)) {
                    headers.add(name, value);
                }
            }
            responseMessage.setHeaders(headers);

            // Body
            byte[] bytes = getResponseContentBytes();
            if (bytes != null && bytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {

                if (log.isLoggable(Level.FINE))
                    log.fine("Response contains textual entity body, converting then setting string on message");
                try {
                    responseMessage.setBodyCharacters(bytes);
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException("Unsupported character encoding: " + ex, ex);
                }

            } else if (bytes != null && bytes.length > 0) {

                if (log.isLoggable(Level.FINE))
                    log.fine("Response contains binary entity body, setting bytes on message");
                responseMessage.setBody(UpnpMessage.BodyType.BYTES, bytes);

            } else {
                if (log.isLoggable(Level.FINE))
                    log.fine("Response did not contain entity body");
            }

            if (log.isLoggable(Level.FINE))
                log.fine("Response message complete: " + responseMessage);
            return responseMessage;
        }
    }
}


