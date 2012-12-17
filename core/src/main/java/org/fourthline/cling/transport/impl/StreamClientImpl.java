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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamClient;
import org.seamless.http.Headers;
import org.seamless.util.io.IO;
import org.seamless.util.URIUtil;
import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Default implementation based on the JDK's <code>HttpURLConnection</code>.
 * <p>
 * This class works around a serious design issue in the SUN JDK, so it will not work on any JDK that
 * doesn't offer the <code>sun.net.www.protocol.http.HttpURLConnection </code> implementation.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamClientImpl implements StreamClient {

    final static String HACK_STREAM_HANDLER_SYSTEM_PROPERTY = "hackStreamHandlerProperty";

    final private static Logger log = Logger.getLogger(StreamClient.class.getName());

    final protected StreamClientConfigurationImpl configuration;

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;

        log.fine("Using persistent HTTP stream client connections: " + configuration.isUsePersistentConnections());
        System.setProperty("http.keepAlive", Boolean.toString(configuration.isUsePersistentConnections()));

        // Hack the JDK to allow additional HTTP methods
        if (System.getProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY) == null) {
            log.fine("Setting custom static URLStreamHandlerFactory to work around Sun JDK bugs");
            URLStreamHandlerFactory shf =
                    new URLStreamHandlerFactory() {
                        public URLStreamHandler createURLStreamHandler(String protocol) {
                            log.fine("Creating new URLStreamHandler for protocol: " + protocol);
                            if ("http".equals(protocol)) {
                                return new Handler() {

                                    protected java.net.URLConnection openConnection(URL u) throws IOException {
                                        return openConnection(u, null);
                                    }

                                    protected java.net.URLConnection openConnection(URL u, Proxy p) throws IOException {
                                        return new UpnpURLConnection(u, this);
                                    }
                                };
                            } else {
                                return null;
                            }
                        }
                    };

            try {
                URL.setURLStreamHandlerFactory(shf);
            } catch (Throwable t) {
                throw new InitializationException(
                        "URLStreamHandlerFactory already set for this JVM." +
                                " Can't use bundled default client based on JDK's HTTPURLConnection." +
                                " Switch to org.fourthline.cling.transport.impl.apache.StreamClientImpl, see manual."
                );
            }
            System.setProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY, "alreadyWorkedAroundTheEvilJDK");
        }
    }

    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) {

        final UpnpRequest requestOperation = requestMessage.getOperation();
        log.fine("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + requestMessage);

        URL url = URIUtil.toURL(requestOperation.getURI());

        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(requestOperation.getHttpMethodName());
            urlConnection.setReadTimeout(configuration.getDataReadTimeoutSeconds() * 1000);
            urlConnection.setConnectTimeout(configuration.getConnectionTimeoutSeconds() * 1000);

            applyRequestProperties(urlConnection, requestMessage);
            applyRequestBody(urlConnection, requestMessage);

            log.fine("Sending HTTP request: " + requestMessage);
            inputStream = urlConnection.getInputStream();
            return createResponse(urlConnection, inputStream);

        } catch (ProtocolException ex) {
            log.fine("Unrecoverable HTTP protocol exception: " + ex);
            return null;
        } catch (IOException ex) {

            if (urlConnection == null) {
                log.info("Could not open URL connection: " + ex.getMessage());
                return null;
            }

            log.fine("Exception occured, trying to read the error stream");
            try {
                inputStream = urlConnection.getErrorStream();
                return createResponse(urlConnection, inputStream);
            } catch (Exception errorEx) {
                log.fine("Could not read error stream: " + errorEx);
                return null;
            }
        } catch (Exception ex) {
            log.info("Unrecoverable exception occured, no error response possible: " + ex);
            return null;

        } finally {

            if (urlConnection != null) {
                // Release any idle persistent connection, or "indicate that we don't want to use this server for a while"
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public void stop() {
        // NOOP
    }

    protected void applyRequestProperties(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) {

        urlConnection.setInstanceFollowRedirects(false); // Defaults to true but not needed here

        // HttpURLConnection always adds a "Host" header

        // HttpURLConnection always adds an "Accept" header (not needed but shouldn't hurt)

        // Add the default user agent if not already set on the message
        if (!requestMessage.getHeaders().containsKey(UpnpHeader.Type.USER_AGENT)) {
            urlConnection.setRequestProperty(
                    UpnpHeader.Type.USER_AGENT.getHttpName(),
                    getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion())
            );
        }

        // Other headers
        applyHeaders(urlConnection, requestMessage.getHeaders());
    }

    protected void applyHeaders(HttpURLConnection urlConnection, Headers headers) {
        log.fine("Writing headers on HttpURLConnection: " + headers.size());
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String v : entry.getValue()) {
                String headerName = entry.getKey();
                log.fine("Setting header '" + headerName + "': " + v);
                urlConnection.setRequestProperty(headerName, v);
            }
        }
    }

    protected void applyRequestBody(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) throws IOException {

        if (requestMessage.hasBody()) {
            urlConnection.setDoOutput(true);
        } else {
            urlConnection.setDoOutput(false);
            return;
        }

        if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
            IO.writeUTF8(urlConnection.getOutputStream(), requestMessage.getBodyString());
        } else if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
            IO.writeBytes(urlConnection.getOutputStream(), requestMessage.getBodyBytes());
        }
        urlConnection.getOutputStream().flush();
    }

    protected StreamResponseMessage createResponse(HttpURLConnection urlConnection, InputStream inputStream) throws Exception {

        if (urlConnection.getResponseCode() == -1) {
            log.fine("Did not receive valid HTTP response");
            return null;
        }

        // Status
        UpnpResponse responseOperation = new UpnpResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());

        log.fine("Received response: " + responseOperation);

        // Message
        StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);

        // Headers
        responseMessage.setHeaders(new UpnpHeaders(urlConnection.getHeaderFields()));

        // Body
        byte[] bodyBytes = null;
        InputStream is = null;
        try {
            is = inputStream;
            if (inputStream != null) bodyBytes = IO.readBytes(is);
        } finally {
            if (is != null)
                is.close();
        }

        if (bodyBytes != null && bodyBytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {

            log.fine("Response contains textual entity body, converting then setting string on message");
            responseMessage.setBodyCharacters(bodyBytes);

        } else if (bodyBytes != null && bodyBytes.length > 0) {

            log.fine("Response contains binary entity body, setting bytes on message");
            responseMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);

        } else {
            log.fine("Response did not contain entity body");
        }

        log.fine("Response message complete: " + responseMessage);
        return responseMessage;
    }

    /**
     * The SUNW morons restrict the JDK handlers to GET/POST/etc for "security" reasons.
     * They do not understand HTTP. This is the hilarious comment in their source:
     * <p/>
     * "This restriction will prevent people from using this class to experiment w/ new
     * HTTP methods using java.  But it should be placed for security - the request String
     * could be arbitrarily long."
     */
    static class UpnpURLConnection extends sun.net.www.protocol.http.HttpURLConnection {

        private static final String[] methods = {
                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE",
                "SUBSCRIBE", "UNSUBSCRIBE", "NOTIFY"
        };

        protected UpnpURLConnection(URL u, Handler handler) throws IOException {
            super(u, handler);
        }

        public UpnpURLConnection(URL u, String host, int port) throws IOException {
            super(u, host, port);
        }

        public synchronized OutputStream getOutputStream() throws IOException {
            OutputStream os;
            String savedMethod = method;
            // see if the method supports output
            if (method.equals("PUT") || method.equals("POST") || method.equals("NOTIFY")) {
                // fake the method so the superclass method sets its instance variables
                method = "PUT";
            } else {
                // use any method that doesn't support output, an exception will be
                // raised by the superclass
                method = "GET";
            }
            os = super.getOutputStream();
            method = savedMethod;
            return os;
        }

        public void setRequestMethod(String method) throws ProtocolException {
            if (connected) {
                throw new ProtocolException("Cannot reset method once connected");
            }
            for (String m : methods) {
                if (m.equals(method)) {
                    this.method = method;
                    return;
                }
            }
            throw new ProtocolException("Invalid UPnP HTTP method: " + method);
        }
    }

}


