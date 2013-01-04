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

package org.fourthline.cling.bridge.link.proxy;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.auth.AuthCredentials;
import org.fourthline.cling.bridge.gateway.FormActionProcessor;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class ProxyActionExecutor implements ActionExecutor {

    final private static Logger log = Logger.getLogger(ProxyActionExecutor.class.getName());

    final private BridgeUpnpServiceConfiguration configuration;
    final private URL controlURL;
    final private AuthCredentials credentials;

    protected ProxyActionExecutor(BridgeUpnpServiceConfiguration configuration, URL controlURL, AuthCredentials credentials) {
        this.configuration = configuration;
        this.controlURL = controlURL;
        this.credentials = credentials;
    }

    public BridgeUpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public FormActionProcessor getActionProcessor() {
        return getConfiguration().getActionProcessor();
    }

    public URL getControlURL() {
        return controlURL;
    }

    public AuthCredentials getCredentials() {
        return credentials;
    }

    public void execute(ActionInvocation<LocalService> actionInvocation) {

        boolean failed = false;
        String responseBody = null;
        try {
            String requestURL = getControlURL().toString() + "/" + actionInvocation.getAction().getName();
            log.fine("Sending POST to remote: " + requestURL);
            ClientRequest request = new ClientRequest(requestURL);

            request.header("Accept", MediaType.APPLICATION_FORM_URLENCODED);
            request.body(
                    MediaType.APPLICATION_FORM_URLENCODED,
                    getActionProcessor().createFormString(actionInvocation)
            );

            getConfiguration().getAuthManager().write(getCredentials(), request);
            ClientResponse<String> response = request.post(String.class);

            log.fine("Received response: " + response.getStatus());

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                log.fine("Remote '" + actionInvocation + "' failed: " + response.getStatus());
                failed = true;
            }

            responseBody = response.getEntity();

        } catch (Exception ex) {
            log.fine("Remote '" + actionInvocation + "' failed: " + Exceptions.unwrap(ex));
            failed = true;
        }

        if (failed && responseBody == null) {
            log.fine("Response is failed with no body, setting failure");
            actionInvocation.setFailure(
                    new ActionException(ErrorCode.ACTION_FAILED, "No response received or internal proxy error")
            );
        } else if (failed && responseBody.length() > 0) {
            log.fine("Response is failed with body, reading failure message");
            getActionProcessor().readFailure(
                    getActionProcessor().valueOf(responseBody),
                    actionInvocation
            );
        } else if (responseBody.length() > 0) {
            log.fine("Response successful with body, reading output argument values");
            try {
                getActionProcessor().readOutput(
                        getActionProcessor().valueOf(responseBody),
                        actionInvocation
                );
            } catch (InvalidValueException ex) {
                log.fine("Error transforming output values after remote invocation of: " + actionInvocation);
                log.fine("Cause: " + Exceptions.unwrap(ex));
                actionInvocation.setFailure(
                        new ActionException(ErrorCode.ACTION_FAILED, "Error transforming output values of proxied remoted invocation", ex)
                );
            }
        }
    }

}
