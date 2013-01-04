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

package org.fourthline.cling.demo.osgi.device.light.servlet;

import org.fourthline.cling.demo.osgi.device.light.model.BinaryLight;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author Bruce Green
 */
public class BinaryLightPresentationServlet extends DevicePresentationServlet {

    private static final long serialVersionUID = 8460727256022005488L;
    private static final String ALIAS = "/bl/status";
    private BinaryLight light;

    public BinaryLightPresentationServlet(BinaryLight light) {
        super(ALIAS);
        this.light = light;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        response.getWriter().write("<html>");
        response.getWriter().write("<body>");
        response.getWriter().write(String.format("<b>Binary Light at %s</b>", new Date()));

        response.getWriter().write("<p>");
        response.getWriter().write(String.format("<b>luminance:</b> %d<br/>", light.getLuminance()));
        response.getWriter().write(String.format("<b>switch is:</b> %s<br/>", light.getLightSwitch().getState() ? "on" : "off"));
        response.getWriter().write("</p>");
        response.getWriter().write("</body>");
        response.getWriter().write("</html>");
    }
}

