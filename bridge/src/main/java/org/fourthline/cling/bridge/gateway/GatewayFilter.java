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

package org.fourthline.cling.bridge.gateway;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.protocol.ReceivingSync;
import org.seamless.util.io.IO;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Adapts the standard Java servlet HTTP request/response to the UPnP messages of the core stack.
 * <p>
 * This is a web-application compatible replacement for the <code>StreamServer</code> network
 * handler. You can configure your UPnP stack with a "NOOP" <code>StreamServer</code> and instead
 * hook this filter into your web application to handle TCP|HTTP|SOAP messages for descriptors,
 * actions, events, etc.
 * </p>
 *
 * @author Christian Bauer
 */
public class GatewayFilter implements Filter {

    final private static Logger log = Logger.getLogger(GatewayFilter.class.getName());

    protected UpnpService upnpService;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.upnpService = (UpnpService) filterConfig.getServletContext()
                .getAttribute(Constants.ATTR_UPNP_SERVICE);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Always set a server header
        response.setHeader("Server", new ServerHeader().getString());

        log.fine("Trying to handle message with core stack: " + request.getMethod() + " " + request.getRequestURI());

        StreamRequestMessage requestMessage = convertRequest(request);

        if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
            log.fine("Method not supported by core stack, continuing processing: " + request.getMethod());
            chain.doFilter(request, response);
            return;
        }

        // Execute it as a core protocol
        ReceivingSync receivingProtocol = null;
        try {
            receivingProtocol = upnpService.getProtocolFactory().createReceivingSync(requestMessage);
        } catch (ProtocolCreationException ex) {
            log.fine("No protocol available in core for handling: " + request.getMethod() + " " + request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        log.fine("Running protocol available in core stack: " + receivingProtocol.getClass().getName());
        receivingProtocol.run();
        StreamResponseMessage responseMessage = receivingProtocol.getOutputMessage();

        // Return the response
        if (responseMessage != null) {
            log.fine("Preparing HTTP response message: " + responseMessage);
            copyResponse(responseMessage, response);

            log.finer("Notifying ReceivingProtocol of sent response message");
            receivingProtocol.responseSent(responseMessage);
            return;
        }

        log.fine("Null response from core protocol, assuming Not Found and continuing");
        chain.doFilter(request, response);
    }

    public void destroy() {
    }

    protected StreamRequestMessage convertRequest(final HttpServletRequest request) {
        log.finer("Instantiating UPnP StreamMessage from HTTP request");

        StreamRequestMessage coreRequest =
                new StreamRequestMessage(
                        UpnpRequest.Method.getByHttpName(request.getMethod()),
                        URI.create(request.getRequestURI()),
                        ""
                ) {

                    // Don't consume the request body until necessary (that's why we do this
                    // here and pass an empty string into the constructor).

                    boolean processed = false;
                    private Object entity;

                    protected void readEntityIfAvailable() {
                        if (processed) return;

                        InputStream is = null;
                        try {
                            is = request.getInputStream();
                            if (is == null || getContentTypeHeader() == null) {
                                log.fine("No request input stream or content type, not reading entity");
                                return;
                            }

                            log.fine("Reading servlet input stream bytes");
                            entity = IO.readBytes(is);
                            if (isContentTypeText()) {
                                setBodyType(BodyType.STRING);
                                String charset = getContentTypeCharset();
                                if (charset == null) charset = "utf-8";
                                log.fine("Setting body type string with charset: " + charset);
                                entity = new String((byte[])entity, charset);
                            } else {
                                setBodyType(BodyType.BYTES);
                            }

                            log.fine("Completed reading servlet input stream");

                        } catch (IOException ex) {
                            throw new RuntimeException("Can't read request body: " + ex, ex);
                        } finally {

                            processed = true;

                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException ex) {
                                    // Ignore
                                }
                            }
                        }
                    }

                    @Override
                    public boolean hasBody() {
                        readEntityIfAvailable();
                        return entity != null;
                    }

                    @Override
                    public Object getBody() {
                        readEntityIfAvailable();
                        return entity;
                    }
                };

        log.finer("Converting HTTP request headers into UPnP StreamMessage headers");

        UpnpHeaders upnpHeaders = new UpnpHeaders();
        Enumeration<String> headerEnum = request.getHeaderNames();
        while (headerEnum.hasMoreElements()) {
            String headerName = headerEnum.nextElement();
            if (log.isLoggable(Level.FINEST)) {
                log.finest("Header: " + headerName + " => " + request.getHeader(headerName));
            }
            upnpHeaders.add(headerName, request.getHeader(headerName));
        }

        coreRequest.setHeaders(upnpHeaders);

        return coreRequest;
    }

    protected void copyResponse(StreamResponseMessage coreResponse, HttpServletResponse response) {
        log.finer("Setting HTTP response status code from UPnP StreamResponse: " + coreResponse.getOperation().getStatusCode());
        response.setStatus(coreResponse.getOperation().getStatusCode());

        log.finer("Converting UPnP response headers into HTTP headers");
        UpnpHeaders upnpHeaders = coreResponse.getHeaders();
        for (Map.Entry<String, List<String>> entry : upnpHeaders.entrySet()) {
            if (UpnpHeader.Type.CONTENT_TYPE.equals(UpnpHeader.Type.getByHttpName(entry.getKey()))) continue;
            if (UpnpHeader.Type.SERVER.equals(UpnpHeader.Type.getByHttpName(entry.getKey()))) continue;

            for (String headerValue : entry.getValue()) {
                if (log.isLoggable(Level.FINEST)) {
                    log.finest("Header: " + entry.getKey() + " => " + headerValue);
                }
                response.addHeader(entry.getKey(), headerValue);
            }
        }

        if (coreResponse.getOperation().isFailed()) return;

        // If there is a content type header, we use it later to set the entity, if not, there is no entity
        ContentTypeHeader contentTypeHeader =
                coreResponse.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
        if (contentTypeHeader == null) {
            log.finer("No entity body in response");
            return;
        }

        OutputStream out = null;
        try {

            out = response.getOutputStream();
            response.setContentType(contentTypeHeader.getString());

            log.finer("Writing response entity body with content-type: " + contentTypeHeader.getString());

            if (coreResponse.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
                response.setContentLength(coreResponse.getBodyString().length());

                String charset = contentTypeHeader.getValue().getParameters().get("charset");
                if (charset == null) charset = "utf-8";
                log.finer("Writing string to output stream using charset: " + charset);
                OutputStreamWriter outputWriter = new OutputStreamWriter(out, charset);
                outputWriter.write(coreResponse.getBodyString());
                outputWriter.flush();

            } else {
                response.setContentLength(coreResponse.getBodyBytes().length);
                IO.writeBytes(out, coreResponse.getBodyBytes());
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error writing response body: " + ex, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        }
    }
}
