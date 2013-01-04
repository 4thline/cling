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

package org.fourthline.cling.bridge;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.Registry;
import org.seamless.xhtml.Body;
import org.seamless.xhtml.XHTML;
import org.seamless.xhtml.XHTMLElement;
import org.seamless.xhtml.XHTMLParser;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.xpath.XPath;

/**
 * @author Christian Bauer
 */
public class BridgeServerResource {

    final private XHTMLParser parserXHTML = new XHTMLParser();

    protected ServletContext servletContext;

    protected UriInfo uriInfo;

    public ServletContext getServletContext() {
        return servletContext;
    }

    @Context
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @Context
    public void setUriInfo(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    protected BridgeUpnpService getUpnpService() {
        return (BridgeUpnpService)getServletContext().getAttribute(Constants.ATTR_UPNP_SERVICE);
    }

    protected Registry getRegistry() {
        return getUpnpService().getRegistry();
    }

    protected ControlPoint getControlPoint() {
        return getUpnpService().getControlPoint();
    }

    protected BridgeUpnpServiceConfiguration getConfiguration() {
        return getUpnpService().getConfiguration();
    }

    protected BridgeNamespace getNamespace() {
        return getUpnpService().getConfiguration().getNamespace();
    }

    protected String getFirstPathParamValue(String paramName) {
        MultivaluedMap<String, String> map = getUriInfo().getPathParameters();
        String value = map.getFirst(paramName);
        if (value == null) {
            throw new BridgeWebApplicationException(
                    Response.Status.INTERNAL_SERVER_ERROR,
                    "Desired path parameter value not found in request: " + paramName
            );
        }
        return value;
    }

    protected UDN getRequestedUDN() {
        return UDN.valueOf(getFirstPathParamValue(Constants.PARAM_UDN));
    }

    public XHTMLParser getParserXHTML() {
        return parserXHTML;
    }

    public void createHead(XHTMLElement root, String title) {
        root.createChild(XHTML.ELEMENT.head)
                .createChild(XHTML.ELEMENT.title)
                .setContent(title);
    }

    public Body createBodyTemplate(XHTML xhtml, XPath xpath, String title) {
        XHTMLElement root = xhtml.createRoot(xpath, XHTML.ELEMENT.html);
        createHead(root, title);
        root.createChild(XHTML.ELEMENT.body);
        return xhtml.getRoot(xpath).getBody();
    }

    protected String appendLocalCredentials(String uri) {
        return UriBuilder.fromUri(uri)
                .queryParam("auth", getConfiguration().getAuthManager().getLocalCredentials())
                .build().toString();
    }

}
