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

import javax.servlet.http.HttpServlet;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Bruce Green
 */
public class DevicePresentationServlet extends HttpServlet {

    private static final long serialVersionUID = 2828791839299050304L;
    private final String PROPERTY_PORT = "org.osgi.service.http.port";
    private String alias;

    public DevicePresentationServlet(String alias) {
        this.alias = alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getPresentationURL() {
        String url = null;

        try {
            InetAddress host = InetAddress.getLocalHost();
            String port = System.getProperty(PROPERTY_PORT);
            url = String.format("http://%s%s%s", host.getHostAddress(), port != null ? (":" + port) : "", alias);

        } catch (UnknownHostException e) {
        }

        return url;
    }


}
