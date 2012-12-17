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

package org.fourthline.cling.bridge.link.proxy;

import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.seamless.util.io.Base64Coder;
import org.fourthline.cling.binding.staging.MutableService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.bridge.BridgeNamespace;
import org.fourthline.cling.bridge.BridgeUpnpServiceConfiguration;
import org.fourthline.cling.bridge.link.Endpoint;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDN;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class CombinedDescriptorBinder {

    private static Logger log = Logger.getLogger(CombinedDescriptorBinder.class.getName());

    final protected DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    final protected DocumentBuilder documentBuilder;

    final protected BridgeUpnpServiceConfiguration configuration;

    public static final String NAMESPACE_URN = "urn:fourthline-org:cling:bridge:combined-descriptor-1-0";

    public enum EL {
        deviceModel,
        deviceService,
        deviceIcon,
        root,
        scpd
    }

    public enum ATTR {
        maxAgeSeconds,
        serviceId,
        iconId
    }

    public CombinedDescriptorBinder(BridgeUpnpServiceConfiguration configuration) {
        try {
            this.documentBuilderFactory.setNamespaceAware(true);
            this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        this.configuration = configuration;

    }

    public BridgeUpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public String write(Device device) throws IOException {
        try {
            log.fine("Generating serialized XML of device graph: " + device);

            Document dom = documentBuilder.newDocument();

            Element deviceModelEl = dom.createElementNS(NAMESPACE_URN, EL.deviceModel.toString());
            dom.appendChild(deviceModelEl);

            deviceModelEl.setAttribute(
                    ATTR.maxAgeSeconds.toString(),
                    device.getIdentity().getMaxAgeSeconds().toString()
            );

            Document rootDescriptor = getConfiguration().getDeviceDescriptorBinderUDA10().buildDOM(
                    device.getRoot(),
                    new RemoteClientInfo(),
                    getConfiguration().getNamespace()
            );
            deviceModelEl.appendChild(dom.importNode(rootDescriptor.getDocumentElement(), true));

            Icon[] icons = device.getRoot().findIcons();
            for (int i = 0; i < icons.length; i++) {

                // Even for a remote device, the icon data should have been "prepared", so we can base64 encode it here
                byte[] iconData = icons[i].getData();
                if (iconData == null || iconData.length == 0) continue;

                Element iconEl = dom.createElement(EL.deviceIcon.toString());
                deviceModelEl.appendChild(iconEl);

                iconEl.setAttribute(
                        ATTR.iconId.toString(),
                        BridgeNamespace.getIconId(device, i)
                );

                iconEl.setTextContent(
                        new String(Base64Coder.encode(iconData))
                );
            }

            Service[] services = device.getRoot().findServices();
            for (Service service : services) {
                Element deviceServiceEl = dom.createElement(EL.deviceService.toString());
                deviceModelEl.appendChild(deviceServiceEl);

                deviceServiceEl.setAttribute(
                        ATTR.serviceId.toString(),
                        service.getServiceId().toString()
                );

                Document serviceDescriptor =
                        getConfiguration().getServiceDescriptorBinderUDA10().buildDOM(service);

                deviceServiceEl.appendChild(dom.importNode(serviceDescriptor.getDocumentElement(), true));
            }

            return documentToString(dom);
        } catch (Exception ex) {
            throw new IOException("Can't transform device graph into XML string: " + ex.toString(), ex);
        }
    }

    public ProxyLocalDevice read(String xml, Endpoint endpoint) throws IOException {

        if (xml == null || xml.length() == 0) {
            throw new IOException("Null or empty XML");
        }

        log.fine("Deserializing XML into device graph");
        //log.finest(xml);

        try {
            final ProxyDeviceDescriptorBinder deviceDescriptorBinder = new ProxyDeviceDescriptorBinder();

            ProxyServiceDescriptorBinder serviceDescriptorBinder =
                    new ProxyServiceDescriptorBinder(getConfiguration(), endpoint) {

                        @Override
                        protected void hydrateBasic(MutableService descriptor, Service undescribedService) {
                            descriptor.serviceId = undescribedService.getServiceId();
                            descriptor.serviceType = undescribedService.getServiceType();

                            ProxyServiceCoordinates serviceCoordinates =
                                    deviceDescriptorBinder.getServiceCoordinates().get(descriptor.serviceId);
                            if (serviceCoordinates == null) {
                                // Yeah, we could write a schema for the combined descriptor and validate...
                                throw new IllegalStateException("Can't read services before device metadata has been read");
                            }

                            descriptor.controlURI = serviceCoordinates.getControlURI();
                            descriptor.eventSubscriptionURI = serviceCoordinates.getEventSubscriptionURI();
                            descriptor.descriptorURI = serviceCoordinates.getDescriptorURI();
                        }
                    };

            Document d = documentBuilder.parse(new InputSource(new StringReader(xml)));
            Element deviceModelElement = d.getDocumentElement();

            ProxyDeviceIdentity identity = deserializeIdentity(deviceModelElement, endpoint);
            ProxyLocalDevice device = new ProxyLocalDevice(identity);

            Map<String, LocalService> services = new HashMap();
            Map<String, byte[]> icons = new HashMap();

            NodeList deviceModelChildren = deviceModelElement.getChildNodes();
            for (int i = 0; i < deviceModelChildren.getLength(); i++) {
                Node deviceModelChild = deviceModelChildren.item(i);

                if (deviceModelChild.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                if (EL.root.toString().equals(deviceModelChild.getNodeName())) {

                    Document doc = documentBuilder.newDocument();
                    doc.appendChild(doc.importNode(deviceModelChild, true));

                    device = deviceDescriptorBinder.describe(device, doc);
                }

                if (EL.deviceIcon.toString().equals(deviceModelChild.getNodeName())) {

                    String iconId = ((Element) deviceModelChild).getAttribute(ATTR.iconId.toString());

                    icons.put(iconId, Base64Coder.decode(deviceModelChild.getTextContent()));
                }

                if (EL.deviceService.toString().equals(deviceModelChild.getNodeName())) {

                    String serviceId = ((Element) deviceModelChild).getAttribute(ATTR.serviceId.toString());

                    LocalService undescribedService = device.findService(ServiceId.valueOf(serviceId));
                    if (undescribedService == null) {
                        throw new DescriptorBindingException("Device service not found in device graph: " + serviceId);
                    }

                    NodeList deviceServiceChildren = deviceModelChild.getChildNodes();
                    for (int j = 0; j < deviceServiceChildren.getLength(); j++) {
                        Node deviceServiceChild = deviceServiceChildren.item(j);

                        if (deviceServiceChild.getNodeType() != Node.ELEMENT_NODE)
                            continue;

                        if (EL.scpd.toString().equals(deviceServiceChild.getNodeName())) {

                            Document doc = documentBuilder.newDocument();
                            doc.appendChild(doc.importNode(deviceServiceChild, true));

                            undescribedService =
                                    serviceDescriptorBinder.describe(
                                            undescribedService,
                                            doc
                                    );
                            services.put(serviceId, undescribedService);
                        }
                    }
                }
            }

            return describeGraph(device, icons, services);

        } catch (ValidationException ex) {
            log.fine("Could not validate combined device model: " + ex.toString());
            for (ValidationError validationError : ex.getErrors()) {
                log.severe(validationError.toString());
            }
            throw new IOException("Could not parse combined descriptor: " + ex.toString(), ex);
        } catch (Exception ex) {
            throw new IOException("Could not parse combined descriptor: " + ex.toString(), ex);
        }
    }

    protected ProxyDeviceIdentity deserializeIdentity(Element deviceModelElement, Endpoint endpoint) throws IOException {
        Integer maxAgeSeconds;
        try {
            maxAgeSeconds = Integer.parseInt(deviceModelElement.getAttribute(ATTR.maxAgeSeconds.toString()));
        } catch (Exception ex) {
            throw new IOException(
                    "No maxAgeSeconds attribute or wrong value on root element: " + ex.toString(), ex
            );
        }

        try {
            return new ProxyDeviceIdentity(
                    UDN.valueOf("PLACEHOLDER-UNTIL-WE-COMPLETE-DESERIALIZATION"),
                    maxAgeSeconds,
                    endpoint
            );
        } catch (Exception ex) {
            throw new IOException("Can't create remote device identity: " + ex.toString(), ex);
        }
    }

    protected ProxyLocalDevice describeGraph(ProxyLocalDevice currentDevice,
                                             Map<String, byte[]> icons,
                                             Map<String, LocalService> services) throws ValidationException {

        List<Icon> describedIcons = new ArrayList();
        if (currentDevice.hasIcons()) {
            for (int i = 0; i < currentDevice.getIcons().length; i++) {
                Icon icon = currentDevice.getIcons()[i];
                String iconId = BridgeNamespace.getIconId(currentDevice, i);
                byte[] iconData = icons.get(iconId);
                if (iconData != null) {
                    describedIcons.add(
                            new Icon(
                                    icon.getMimeType(),
                                    icon.getWidth(),
                                    icon.getHeight(),
                                    icon.getDepth(),
                                    icon.getUri(),
                                    iconData
                            )
                    );
                }
            }
        }

        List<LocalService> describedServices = new ArrayList();
        if (currentDevice.hasServices()) {
            for (LocalService deviceService : currentDevice.getServices()) {
                describedServices.add(services.get(deviceService.getServiceId().toString()));
            }
        }

        List<LocalDevice> describedEmbeddedDevices = new ArrayList();
        if (currentDevice.hasEmbeddedDevices()) {
            for (LocalDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                describedEmbeddedDevices.add(describeGraph((ProxyLocalDevice)embeddedDevice, icons, services));
            }
        }

        // Yes, we create a completely new immutable graph here
        return currentDevice.newInstance(
                currentDevice.getIdentity().getUdn(),
                currentDevice.getVersion(),
                currentDevice.getType(),
                currentDevice.getDetails(),
                describedIcons.toArray(new Icon[describedIcons.size()]),
                currentDevice.toServiceArray(describedServices),
                describedEmbeddedDevices
        );
    }

    protected String documentToString(Document document) throws Exception {
        return XMLUtil.documentToString(document);
    }

}
