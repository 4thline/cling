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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import java.util.logging.Logger;

/**
 * @author Bruce Green
 */
public class HttpServiceTracker extends ServiceTracker {

    final private static Logger log = Logger.getLogger(HttpServiceTracker.class.getName());

    private DevicePresentationServlet servlet;

    public HttpServiceTracker(BundleContext context, DevicePresentationServlet servlet) {
        super(context, HttpService.class.getName(), null);
        this.servlet = servlet;
    }

    public Object addingService(ServiceReference reference) {
        HttpService service = (HttpService) super.addingService(reference);

        if (service != null) {
            log.info(String.format("HTTP Service (%s) added.", service));
            try {
                service.registerServlet(servlet.getAlias(), servlet, null, null);
                log.info(String.format("Servlet %s registered.", servlet.getAlias()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return service;
    }

    public void removedService(ServiceReference reference, Object service) {
        HttpService httpService = (HttpService) service;
        log.info(String.format("HTTP Service (%s) removed.", service));
        httpService.unregister(servlet.getAlias());
        log.info(String.format("Servlet %s unregistered.", servlet.getAlias()));
        super.removedService(reference, service);
    }
}


