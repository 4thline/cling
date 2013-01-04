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

package org.fourthline.cling.bridge.gateway;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.fourthline.cling.bridge.BridgeWebApplicationException;
import org.fourthline.cling.bridge.auth.SecureHashAuthManager;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static org.seamless.xhtml.XHTML.ATTR;
import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
@Path("/dev/{UDN}/svc/{ServiceIdNamespace}/{ServiceId}/action")
public class ActionResource extends GatewayServerResource {

    final private static Logger log = Logger.getLogger(ActionResource.class.getName());

    @GET
    public XHTML browseAll() {
        Service service = getRequestedService();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Actions");

        body.createChild(ELEMENT.h1).setContent("Actions");
        representActions(body, service);

        return result;
    }

    @GET
    @Path("/{ActionName}")
    public XHTML browse() {
        Action action = getRequestedAction();

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), action.getName());

        createForm(body, action);

        return result;
    }

    @POST
    @Path("/{ActionName}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response executeAction(MultivaluedMap<String, String> form) {

        ActionInvocation invocation = executeInvocation(form, getRequestedAction());

        MultivaluedMap<String, String> result = new MultivaluedMapImpl();

        if (invocation.getFailure() != null) {
            log.fine("Invocation was unsuccessful, returning server error for: " + invocation.getFailure());
            getConfiguration().getActionProcessor().appendFailure(invocation, result);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }

        log.fine("Invocation was successful, returning OK response: " + invocation);
        getConfiguration().getActionProcessor().appendOutput(invocation, result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @POST
    @Path("/{ActionName}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_XHTML_XML)
    public XHTML executeActionXHTML(MultivaluedMap<String, String> form) {

        ActionInvocation invocation = executeInvocation(form, getRequestedAction());

        Action action = invocation.getAction();
        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), action.getName());

        createForm(body, action);

        XHTMLElement output = body.createChild(ELEMENT.div).setAttribute(ATTR.id, "invocation-output");

        if (invocation.getFailure() != null) {
            log.fine("Invocation was unsuccessful, generating FAILURE message: " + invocation.getFailure());
            getConfiguration().getActionProcessor().appendFailure(invocation, output);
        } else {
            log.fine("Invocation was successful, generating SUCCESS message: " + invocation);
            getConfiguration().getActionProcessor().appendOutput(invocation, output);
        }

        return result;
    }

    protected ActionInvocation executeInvocation(MultivaluedMap<String, String> form, Action action) {
        ActionInvocation invocation;
        try {
            invocation = getConfiguration().getActionProcessor().createInvocation(form, action);
        } catch (InvalidValueException ex) {
            throw new BridgeWebApplicationException(
                    Response.Status.BAD_REQUEST,
                    "Error processing action input form data: " + Exceptions.unwrap(ex)
            );
        }

        ActionCallback actionCallback = new ActionCallback.Default(invocation, getControlPoint());
        log.fine("Executing action after transformation from HTML form: " + invocation);
        actionCallback.run();

        return invocation;
    }

    protected void createForm(XHTMLElement container, Action action) {
        container.createChild(ELEMENT.h1).setContent(action.getName());

        XHTMLElement form = container.createChild(ELEMENT.form)
                .setAttribute(
                        ATTR.action,
                        getUriInfo().getAbsolutePath().getPath()
                                + "?" + SecureHashAuthManager.QUERY_PARAM_AUTH
                                + "=" + getConfiguration().getAuthManager().getLocalCredentials()
                )
                .setAttribute(ATTR.method, "POST")
                .setAttribute(ATTR.id, "invocation-input");

        if (action.hasInputArguments()) {
            for (ActionArgument argument : action.getInputArguments()) {
                XHTMLElement arg = form.createChild(ELEMENT.div);
                arg.createChild(ELEMENT.span).setContent(argument.getName());
                arg.createChild(ELEMENT.input).setAttribute(ATTR.name, argument.getName());
            }
        }

        form.createChild(ELEMENT.input).setAttribute(ATTR.type, "submit");
        container.createChild("hr");
    }


}
