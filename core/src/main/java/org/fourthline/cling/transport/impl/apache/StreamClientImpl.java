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

package org.fourthline.cling.transport.impl.apache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.transport.spi.AbstractStreamClient;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamClient;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation based on <a href="http://hc.apache.org/">Apache HTTP Components 4.2</a>.
 * <p>
 * This implementation <em>DOES NOT WORK</em> on Android. Read the Cling manual for
 * alternatives on Android.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamClientImpl extends AbstractStreamClient<StreamClientConfigurationImpl, HttpUriRequest> {

    final private static Logger log = Logger.getLogger(StreamClient.class.getName());

    final protected StreamClientConfigurationImpl configuration;
    final protected PoolingClientConnectionManager clientConnectionManager;
    final protected DefaultHttpClient httpClient;
    final protected HttpParams globalParams = new BasicHttpParams();

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;

        HttpProtocolParams.setContentCharset(globalParams, getConfiguration().getContentCharset());
        HttpProtocolParams.setUseExpectContinue(globalParams, false);

        // These are some safety settings, we should never run into these timeouts as we
        // do our own expiration checking
        HttpConnectionParams.setConnectionTimeout(globalParams, (getConfiguration().getTimeoutSeconds()+5) * 1000);
        HttpConnectionParams.setSoTimeout(globalParams, (getConfiguration().getTimeoutSeconds()+5) * 1000);

        HttpConnectionParams.setStaleCheckingEnabled(globalParams, getConfiguration().getStaleCheckingEnabled());
        if (getConfiguration().getSocketBufferSize() != -1)
            HttpConnectionParams.setSocketBufferSize(globalParams, getConfiguration().getSocketBufferSize());

        // Only register 80, not 443 and SSL
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        clientConnectionManager = new PoolingClientConnectionManager(registry);
        clientConnectionManager.setMaxTotal(getConfiguration().getMaxTotalConnections());
        clientConnectionManager.setDefaultMaxPerRoute(getConfiguration().getMaxTotalPerRoute());

        httpClient = new DefaultHttpClient(clientConnectionManager, globalParams);
        if (getConfiguration().getRequestRetryCount() != -1) {
            httpClient.setHttpRequestRetryHandler(
                new DefaultHttpRequestRetryHandler(getConfiguration().getRequestRetryCount(), false)
            );
        }
    }

    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return configuration;
    }


    @Override
    protected HttpUriRequest createRequest(StreamRequestMessage requestMessage) {
        UpnpRequest requestOperation = requestMessage.getOperation();
        HttpUriRequest request;
        switch (requestOperation.getMethod()) {
            case GET:
                request = new HttpGet(requestOperation.getURI());
                break;
            case SUBSCRIBE:
                request = new HttpGet(requestOperation.getURI()) {
                    @Override
                    public String getMethod() {
                        return UpnpRequest.Method.SUBSCRIBE.getHttpName();
                    }
                };
                break;
            case UNSUBSCRIBE:
                request = new HttpGet(requestOperation.getURI()) {
                    @Override
                    public String getMethod() {
                        return UpnpRequest.Method.UNSUBSCRIBE.getHttpName();
                    }
                };
                break;
            case POST:
                HttpEntityEnclosingRequest post = new HttpPost(requestOperation.getURI());
                post.setEntity(createHttpRequestEntity(requestMessage));
                request = (HttpUriRequest) post; // Fantastic API
                break;
            case NOTIFY:
                HttpEntityEnclosingRequest notify = new HttpPost(requestOperation.getURI()) {
                    @Override
                    public String getMethod() {
                        return UpnpRequest.Method.NOTIFY.getHttpName();
                    }
                };
                notify.setEntity(createHttpRequestEntity(requestMessage));
                request = (HttpUriRequest) notify; // Fantastic API
                break;
            default:
                throw new RuntimeException("Unknown HTTP method: " + requestOperation.getHttpMethodName());
        }

        // Headers
        request.setParams(getRequestParams(requestMessage));
        HeaderUtil.add(request, requestMessage.getHeaders());

        return request;
    }

    @Override
    protected Callable<StreamResponseMessage> createCallable(final StreamRequestMessage requestMessage,
                                                             final HttpUriRequest request) {
        return new Callable<StreamResponseMessage>() {
            public StreamResponseMessage call() throws Exception {

                if (log.isLoggable(Level.FINE))
                    log.fine("Sending HTTP request: " + requestMessage);

                return httpClient.execute(request, createResponseHandler());
            }
        };
    }

    @Override
    protected void abort(HttpUriRequest request) {
        request.abort();
    }

    @Override
    protected boolean logExecutionException(Throwable t) {
        if (t instanceof IllegalStateException) {
            // TODO: Document when/why this happens and why we can ignore it, violating the
            // logging rules of the StreamClient#sendRequest() method
            if (log.isLoggable(Level.FINE))
                log.fine("Illegal state: " + t.getMessage());
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        if (log.isLoggable(Level.FINE))
            log.fine("Shutting down HTTP client connection manager/pool");
        clientConnectionManager.shutdown();
    }

    protected HttpEntity createHttpRequestEntity(UpnpMessage upnpMessage) {
        if (upnpMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
            if (log.isLoggable(Level.FINE))
                log.fine("Preparing HTTP request entity as byte[]");
            return new ByteArrayEntity(upnpMessage.getBodyBytes());
        } else {
            if (log.isLoggable(Level.FINE))
                log.fine("Preparing HTTP request entity as string");
            try {
                String charset = upnpMessage.getContentTypeCharset();
                return new StringEntity(upnpMessage.getBodyString(), charset != null ? charset : "UTF-8");
            } catch (Exception ex) {
                // WTF else am I supposed to do with this exception?
                throw new RuntimeException(ex);
            }
        }
    }

    protected ResponseHandler<StreamResponseMessage> createResponseHandler() {
        return new ResponseHandler<StreamResponseMessage>() {
            public StreamResponseMessage handleResponse(final HttpResponse httpResponse) throws IOException {

                StatusLine statusLine = httpResponse.getStatusLine();
                if (log.isLoggable(Level.FINE))
                    log.fine("Received HTTP response: " + statusLine);

                // Status
                UpnpResponse responseOperation =
                    new UpnpResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase());

                // Message
                StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);

                // Headers
                responseMessage.setHeaders(new UpnpHeaders(HeaderUtil.get(httpResponse)));

                // Body
                HttpEntity entity = httpResponse.getEntity();
                if (entity == null || entity.getContentLength() == 0) {
                    log.fine("HTTP response message has no entity");
                    return responseMessage;
                }

                byte data[] = EntityUtils.toByteArray(entity);
                if(data != null) {
                	if (responseMessage.isContentTypeMissingOrText()) {
                		log.fine("HTTP response message contains text entity");
                		responseMessage.setBodyCharacters(data);
                	} else {
                		log.fine("HTTP response message contains binary entity");
                		responseMessage.setBody(UpnpMessage.BodyType.BYTES, data);
                	}
                } else {
                    log.fine("HTTP response message has no entity");
                }

                return responseMessage;
            }
        };
    }

    protected HttpParams getRequestParams(StreamRequestMessage requestMessage) {
        HttpParams localParams = new BasicHttpParams();

        localParams.setParameter(
            CoreProtocolPNames.PROTOCOL_VERSION,
            requestMessage.getOperation().getHttpMinorVersion() == 0 ? HttpVersion.HTTP_1_0 : HttpVersion.HTTP_1_1
        );

        // DefaultHttpClient adds HOST header automatically in its default processor

        // Add the default user agent if not already set on the message
        if (!requestMessage.getHeaders().containsKey(UpnpHeader.Type.USER_AGENT)) {
            HttpProtocolParams.setUserAgent(
                localParams,
                getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion())
            );
        }

        return new DefaultedHttpParams(localParams, globalParams);
    }
}
