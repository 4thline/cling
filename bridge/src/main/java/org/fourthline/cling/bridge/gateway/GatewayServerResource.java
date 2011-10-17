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

package org.fourthline.cling.bridge.gateway;

import org.fourthline.cling.bridge.BridgeServerResource;
import org.fourthline.cling.bridge.BridgeWebApplicationException;
import org.fourthline.cling.bridge.Constants;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.seamless.xhtml.XHTMLElement;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static org.seamless.xhtml.XHTML.ATTR;
import static org.seamless.xhtml.XHTML.ELEMENT;

/**
 * @author Christian Bauer
 */
public class GatewayServerResource extends BridgeServerResource {

    final private static Logger log = Logger.getLogger(GatewayServerResource.class.getName());

    protected ServiceId getRequestedServiceId() {
        return new ServiceId(
                getFirstPathParamValue(Constants.PARAM_SERVICE_ID_NS),
                getFirstPathParamValue(Constants.PARAM_SERVICE_ID)
        );
    }

    protected Device getRequestedDevice() {
        Device d = getRegistry().getDevice(getRequestedUDN(), false);
        if (d == null) {
            throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
        }
        return d;
    }

    protected RemoteDevice getRequestedRemoteDevice() {
        Device device = getRequestedDevice();
        if (device == null) {
            throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
        } else if (!(device instanceof RemoteDevice)) {
            throw new BridgeWebApplicationException(Response.Status.FORBIDDEN);
        }
        return (RemoteDevice) device;
    }

    protected Service getRequestedService() {
        Device device = getRequestedDevice();
        ServiceId sid = getRequestedServiceId();
        for (Service service : device.getServices()) {
            if (service.getServiceId().equals(sid)) {
                return service;
            }
        }
        throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
    }

    protected RemoteService getRequestedRemoteService() {
        Service service = getRequestedService();
        if (service == null) {
            throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
        } else if (!(service instanceof RemoteService)) {
            throw new BridgeWebApplicationException(Response.Status.FORBIDDEN);
        }
        return (RemoteService) service;
    }

    protected Action getRequestedAction() {
        Service service = getRequestedService();
        Action action = service.getAction(getFirstPathParamValue(Constants.PARAM_ACTION_NAME));
        if (action == null) {
            throw new BridgeWebApplicationException(Response.Status.NOT_FOUND);
        }
        return action;
    }

    protected void representDeviceList(XHTMLElement parent, List<Device> devices) {
        if (devices.size() == 0) return;
        XHTMLElement container = parent.createChild(ELEMENT.ul).setId("devices");

        Collections.sort(devices, new Comparator<Device>() {
            public int compare(Device a, Device b) {
                if (a instanceof LocalDevice && !(b instanceof LocalDevice)) {
                    return -1;
                }
                return a.getDisplayString().compareTo(b.getDisplayString());
            }
        });
        for (Device device : devices) {
            URI deviceURI = getNamespace().getPath(device);
            container.createChild(ELEMENT.li)
                    .createChild(ELEMENT.a)
                    .setAttribute(ATTR.href, appendLocalCredentials(deviceURI.toString()))
                    .setContent(device.getClass().getSimpleName() + " - " + device.getDisplayString() + " (" + device.getIdentity().getUdn() + ")");
        }
    }

    protected void representDevice(XHTMLElement parent, Device device) {
        XHTMLElement container = parent.createChild(ELEMENT.div).setClasses("device");

        if (!device.isRoot()) {
            container.createChild(ELEMENT.span)
                    .setClasses("embedded-device").setContent("(Embedded)");
            container.createChild(ELEMENT.a)
                    .setClasses("root-location")
                    .setAttribute(ATTR.href, getNamespace().getPath(device.getParentDevice()).toString())
                    .setContent("Parent");
        }

        container.createChild(ELEMENT.h2).
                createChild(ELEMENT.a)
                .setClasses("device-location")
                .setAttribute(ATTR.href, getNamespace().getPath(device).toString())
                .setContent(device.getDisplayString());

        container.createChild(ELEMENT.div)
                .setClasses("device-identity")
                .setContent(device.getIdentity().toString());

        container.createChild(ELEMENT.p)
                .setClasses("device-type")
                .setContent(device.getType().toString());

        if (device.isRoot()) {
            String descriptorURI = getNamespace().getDescriptorPath(device).toString();
            container.createChild(ELEMENT.a)
                    .setId("descriptor-location")
                    .setAttribute(ATTR.href, appendLocalCredentials(descriptorURI))
                    .setContent(descriptorURI);
        }

        if (device.hasIcons()) {
            container.createChild(ELEMENT.h2).setContent("Icons");
            XHTMLElement iconList = container.createChild(ELEMENT.ul).setClasses("icons");

            for (int i = 0; i < device.getIcons().length; i++) {
                Icon icon = device.getIcons()[i];
                String originalURI;
                String iconURI;
                if (device instanceof RemoteDevice) {
                    RemoteDevice rd = (RemoteDevice) device;
                    iconURI = getNamespace().getPath(rd) + "/MappedIcon" + "/" + i;
                    originalURI = rd.normalizeURI(icon.getUri()).toString();
                } else {
                    iconURI = getNamespace().prefixIfRelative(device, icon.getUri()).toString();
                    originalURI = iconURI;
                }
                XHTMLElement li = iconList.createChild(ELEMENT.li);
                li.createChild(ELEMENT.a)
                        .setId("icon-location")
                        .setAttribute(ATTR.href, iconURI)
                        .setContent(appendLocalCredentials(iconURI));
                if (!iconURI.equals(originalURI))
                    li.createChild(ELEMENT.a)
                            .setId("icon-original-location")
                            .setAttribute(ATTR.href, originalURI)
                            .setContent("(Original URL)");
            }
        }

        if (device.hasServices()) {
            container.createChild(ELEMENT.h2).setContent("Services");
            representServices(container, Arrays.asList(device.getServices()));
        }

        if (device.hasEmbeddedDevices()) {
            container.createChild(ELEMENT.h2).setContent("Embedded Devices");
            List<Device> devices = Arrays.asList(device.getEmbeddedDevices());
            representDeviceList(container, devices);
        }
    }

    protected void representServices(XHTMLElement parent, Collection<Service> deviceServices) {
        XHTMLElement container = parent.createChild(ELEMENT.ul).setClasses("services");

        for (Service deviceService : deviceServices) {
            String serviceURI = getNamespace().getPath(deviceService).toString();
            container.createChild(ELEMENT.li)
                    .createChild(ELEMENT.a)
                    .setId("service-location")
                    .setAttribute(ATTR.href, appendLocalCredentials(serviceURI))
                    .setContent(serviceURI);
        }
    }

    protected void representService(XHTMLElement parent, Service service) {
        XHTMLElement container = parent.createChild(ELEMENT.div).setClasses("service");

        container.createChild(ELEMENT.p)
                .setClasses("service-type")
                .setContent(service.getServiceType().toString());

        String descriptorURI = getNamespace().getDescriptorPath(service).toString();
        container.createChild(ELEMENT.a)
                .setId("descriptor-location")
                .setAttribute(ATTR.href, appendLocalCredentials(descriptorURI))
                .setContent(descriptorURI);

        if (!service.hasActions()) return;

        container.createChild(ELEMENT.h2).setContent("Actions");
        representActions(container, service);
    }

    protected void representActions(XHTMLElement parent, Service service) {
        XHTMLElement container = parent.createChild(ELEMENT.ul).setClasses("actions");

        for (Action action : service.getActions()) {

            UriBuilder uriBuilder = UriBuilder.fromUri(getUriInfo().getAbsolutePath().getPath());
            uriBuilder.segment("action").segment(action.getName());
            String actionURI = uriBuilder.build().toString();

            container.createChild(ELEMENT.li)
                    .createChild(ELEMENT.a)
                    .setAttribute(ATTR.href, appendLocalCredentials(actionURI))
                    .setClasses("action-location")
                    .setContent(actionURI);
        }

    }

}
