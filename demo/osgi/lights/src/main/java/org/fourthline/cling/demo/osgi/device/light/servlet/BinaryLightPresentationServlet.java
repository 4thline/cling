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

