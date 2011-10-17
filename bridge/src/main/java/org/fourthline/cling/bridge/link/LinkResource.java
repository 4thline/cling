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

package org.fourthline.cling.bridge.link;

import org.fourthline.cling.bridge.BridgeServerResource;
import org.fourthline.cling.bridge.BridgeWebApplicationException;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.bridge.auth.AuthCredentials;
import org.fourthline.cling.bridge.auth.HashCredentials;
import org.fourthline.cling.bridge.auth.SecureHashAuthManager;
import org.fourthline.cling.model.resource.Resource;
import org.seamless.http.Query;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

import static org.seamless.xhtml.XHTML.ATTR;
import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
@Path("/link")
public class LinkResource extends BridgeServerResource {

    final private static Logger log = Logger.getLogger(LinkResource.class.getName());

    @GET
    public XHTML browseAll() {
        return representEndpoints();
    }

    @PUT
    @Path("/{EndpointId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(MultivaluedMap<String, String> form) {

        int timeoutSeconds = Constants.LINK_DEFAULT_TIMEOUT_SECONDS;
        String timeout = form.getFirst(LinkManager.FORM_TIMEOUT);
        try {
            if (timeout != null)
                timeoutSeconds = Integer.parseInt(timeout);
        } catch (NumberFormatException ex) {
            log.fine("Ignoring invalid timeout seconds value in update message: " + timeout);
        }

        String callback = form.getFirst(LinkManager.FORM_CALLBACK);
        if (callback == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        URL callbackURL;
        try {
            callbackURL = new URL(callback);
        } catch (MalformedURLException ex) {
            log.fine("Invalid callback URL: " + callback);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String authKey = form.getFirst(LinkManager.FORM_AUTH_HASH);
        if (authKey == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String endpointId = getFirstPathParamValue(Constants.PARAM_ENDPOINT_ID);

        if (getConfiguration().getLocalEndpointURL().equals(callbackURL)) {
            throw new BridgeWebApplicationException(
                    Response.Status.CONFLICT,
                    "Illegal attempt to link two endpoints on the same host, failing: " + endpointId
            );
        }

        Endpoint endpoint = new Endpoint(endpointId, callbackURL, false, new HashCredentials(authKey));
        EndpointResource endpointResource = createEndpointResource(endpoint);

        log.fine("Registering endpoint: " + endpoint);
        boolean created = getUpnpService().getLinkManager().register(endpointResource, timeoutSeconds);

        return Response.status(created ? Response.Status.CREATED : Response.Status.OK).build();
    }

    @DELETE
    @Path("/{EndpointId}")
    public Response remove() {
        EndpointResource resource = getRequestedEndpointResource();
        boolean removed = getUpnpService().getLinkManager().deregister(resource);
        return Response.status(removed ? Response.Status.OK : Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public XHTML executeAction(MultivaluedMap<String, String> form) {
        String action = form.getFirst("action");

        if ("create".equals(action)) {

            int timeoutSeconds = Constants.LINK_DEFAULT_TIMEOUT_SECONDS;
            String s = form.getFirst("timeoutSeconds");
            if (s != null) {
                try {
                    timeoutSeconds = Integer.parseInt(s);
                } catch (Exception ex) {
                    // Ignore
                }
            }

            URL remoteURL;
            String authHash;
            try {
                s = form.getFirst("remoteURL");
                if (s == null) {
                    throw new BridgeWebApplicationException(Response.Status.BAD_REQUEST, "Missing remoteURL request parameter");
                }
                remoteURL = new URL(form.getFirst("remoteURL"));

                Query query = new Query(remoteURL.getQuery());
                if ((authHash = query.get(SecureHashAuthManager.QUERY_PARAM_AUTH)) == null || authHash.length() == 0) {
                    throw new BridgeWebApplicationException(Response.Status.BAD_REQUEST, "Missing auth query parameter");
                }

                // Cut off query parameter (only used for auth)
                remoteURL = new URL(remoteURL.getProtocol(), remoteURL.getHost(), remoteURL.getPort(), remoteURL.getPath());

            } catch (MalformedURLException ex) {
                throw new BridgeWebApplicationException(Response.Status.BAD_REQUEST, "Invalid remote base URL " + ex);
            }

            // Try to prevent double-links between two hosts - of course our best bet is the host name which is
            // absolutely not unique. So there is no fool proof way how we can stop people from linking two hosts
            // twice...
            for (EndpointResource r : getUpnpService().getRegistry().getResources(EndpointResource.class)) {
                URL existingCallbackURL = r.getModel().getCallback();
                if (existingCallbackURL.getAuthority().equals(remoteURL.getAuthority()) &&
                        existingCallbackURL.getProtocol().equals(remoteURL.getProtocol())) {
                    log.info("Link exists with the host of the callback URL, not creating endpoint: " + r.getModel());
                    return representEndpoints("Link with given host already exists: " + r.getModel());
                }
            }

            AuthCredentials credentials = new HashCredentials(authHash);

            Endpoint endpoint = new Endpoint(UUID.randomUUID().toString(), remoteURL, true, credentials);
            EndpointResource endpointResource = createEndpointResource(endpoint);

            boolean success = getUpnpService().getLinkManager().registerAndPut(endpointResource, timeoutSeconds);
            if (success) {
                return representEndpoints("Created link between this and remote bridge: " + remoteURL);
            } else {
                return representEndpoints("Link creation failed, check the logs: " + remoteURL);
            }
        } else if ("remove".equals(action)) {

            String endpointId = form.getFirst("id");
            if (endpointId == null) {
                log.fine("Missing endpoint id request parameter");
                throw new BridgeWebApplicationException(Response.Status.BAD_REQUEST);
            }

            EndpointResource resource = getEndpointResource(endpointId);

            if (resource != null) {
                getUpnpService().getLinkManager().deregisterAndDelete(resource);
                return representEndpoints("Removed link: " + resource.getModel());
            }

            return representEndpoints("Unknown link identifier, can't remove: " + endpointId);

        } else {
            throw new BridgeWebApplicationException(Response.Status.BAD_REQUEST, "Unsupported action: " + action);
        }
    }

    protected EndpointResource getRequestedEndpointResource() {
        String endpointId = getFirstPathParamValue(Constants.PARAM_ENDPOINT_ID);
        EndpointResource resource = getEndpointResource(endpointId);
        if (resource == null) {
            throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
        }
        return resource;
    }

    protected EndpointResource getEndpointResource(String endpointId) {
        return getRegistry().getResource(
                EndpointResource.class,
                getNamespace().getEndpointPath(endpointId)
        );
    }

    protected EndpointResource createEndpointResource(Endpoint endpoint) {
        return new EndpointResource(
                getNamespace().getEndpointPath(endpoint.getId()),
                getConfiguration().getLocalEndpointURL(),
                endpoint
        ) {
            @Override
            public LinkManager getLinkManager() {
                return getUpnpService().getLinkManager();
            }
        };
    }

    protected XHTML representEndpoints(String... messages) {

        XHTML result = getParserXHTML().createDocument();
        Body body = createBodyTemplate(result, getParserXHTML().createXPath(), "Links");
        body.createChild(ELEMENT.h1).setContent("Links");

        XHTMLElement container = body.createChild(ELEMENT.div).setId("links");
        XHTMLElement ul = container.createChild(ELEMENT.ul).setId("endpoints");

        Collection<EndpointResource> resources = getRegistry().getResources(EndpointResource.class);

        for (Resource<Endpoint> resource : resources) {
            XHTMLElement li = ul.createChild(ELEMENT.li);

            li.createChild(ELEMENT.div)
                    .setClasses("endpoint-id")
                    .setContent(resource.getModel().getId());
            li.createChild(ELEMENT.div)
                    .setClasses("endpoint-callback")
                    .setContent(resource.getModel().getCallback().toString());
            li.createChild(ELEMENT.div)
                    .setClasses("endpoint-origin")
                    .setContent("Local Origin: " + resource.getModel().isLocalOrigin());


            XHTMLElement form = li.createChild(ELEMENT.div)
                    .createChild(ELEMENT.form)
                    .setClasses("endpoint-action")
                    .setAttribute(ATTR.action, getUriInfo().getAbsolutePath().getPath())
                    .setAttribute(ATTR.method, "POST");

            form.createChild(ELEMENT.input)
                    .setAttribute(ATTR.name, "id")
                    .setAttribute("value", resource.getModel().getId())
                    .setAttribute(ATTR.type, "hidden");

            form.createChild(ELEMENT.input)
                    .setAttribute(ATTR.name, "action")
                    .setAttribute("value", "remove")
                    .setAttribute(ATTR.type, "submit");

        }

        if (messages != null) {
            for (String message : messages) {
                body.createChild(ELEMENT.div).setClasses("message").setContent(message);
            }
        }

        createForm(body);

        return result;
    }

    protected void createForm(XHTMLElement container) {
        container.createChild(ELEMENT.h2).setContent("Create Link");

        XHTMLElement form = container.createChild(ELEMENT.form)
                .setAttribute(
                        ATTR.action,
                        getUriInfo().getAbsolutePath().getPath()
                                + "?" + SecureHashAuthManager.QUERY_PARAM_AUTH
                                + "=" + getConfiguration().getAuthManager().getLocalCredentials()
                )
                .setAttribute(ATTR.method, "POST");

        XHTMLElement remoteURLInput = form.createChild(ELEMENT.div);
        remoteURLInput.createChild(ELEMENT.span).setContent("Remote Base URL:");
        remoteURLInput.createChild(ELEMENT.input).setAttribute(ATTR.name, "remoteURL");

        XHTMLElement timeoutSecondsInput = form.createChild(ELEMENT.div);
        timeoutSecondsInput.createChild(ELEMENT.span).setContent("Timeout Seconds:");
        timeoutSecondsInput.createChild(ELEMENT.input).setAttribute(ATTR.name, "timeoutSeconds");

        form.createChild(ELEMENT.input)
                .setAttribute(ATTR.name, "action")
                .setAttribute("value", "create")
                .setAttribute(ATTR.type, "submit");
    }


}
