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

package org.fourthline.cling.bridge.auth;

import org.fourthline.cling.bridge.BridgeUpnpService;
import org.fourthline.cling.bridge.Constants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class AuthFilter implements Filter {

    final private static Logger log = Logger.getLogger(AuthFilter.class.getName());

    protected AuthManager authManager;

    public AuthFilter() {
    }

    public AuthFilter(AuthManager authManager) {
        this.authManager = authManager;
    }


    public void init(FilterConfig filterConfig) throws ServletException {
        authManager =
                ((BridgeUpnpService)filterConfig.getServletContext().getAttribute(Constants.ATTR_UPNP_SERVICE))
                        .getConfiguration().getAuthManager();
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        AuthCredentials credentials;
        if ((credentials = authManager.read(request)) != null && authManager.isAuthenticated(credentials)) {
            log.fine("Request authenticated, continuing...");
            chain.doFilter(request, response);
        } else {
            log.fine("Request authentication failed, aborting...");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    public void destroy() {
    }
}
