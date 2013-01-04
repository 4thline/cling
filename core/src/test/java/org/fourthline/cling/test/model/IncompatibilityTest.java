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

package org.fourthline.cling.test.model;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.model.meta.StateVariableTypeDetails;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.resource.ServiceEventCallbackResource;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.IntegerDatatype;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public class IncompatibilityTest {

    @Test
    public void validateMSFTServiceType() {
        // TODO: UPNP VIOLATION: Microsoft violates the spec and sends periods in domain names instead of hyphens!
        ServiceType serviceType = ServiceType.valueOf("urn:microsoft.com:service:X_MS_MediaReceiverRegistrar:1");
        assertEquals(serviceType.getNamespace(), "microsoft.com");
        assertEquals(serviceType.getType(), "X_MS_MediaReceiverRegistrar");
        assertEquals(serviceType.getVersion(), 1);
    }

    // TODO: UPNP VIOLATION: Azureus sends a URN as the service ID suffix. This doesn't violate the spec but it's unusual...
    @Test
    public void validateAzureusServiceId() {
        ServiceId serviceId = ServiceId.valueOf("urn:upnp-org:serviceId:urn:schemas-upnp-org:service:ConnectionManager");
        assertEquals(serviceId.getNamespace(), "upnp-org");
        assertEquals(serviceId.getId(), "urn:schemas-upnp-org:service:ConnectionManager");
    }

    // TODO: UPNP VIOLATION: PS Audio Bridge has invalid service IDs
    @Test
    public void validatePSAudioBridgeServiceId() {
        ServiceId serviceId = ServiceId.valueOf("urn:foo:ThisSegmentShouldBeNamed'service':baz");
        assertEquals(serviceId.getNamespace(), "foo");
        assertEquals(serviceId.getId(), "baz");
    }

    // TODO: UPNP VIOLATION: Some devices send spaces in URNs
    @Test
    public void validateSpacesInServiceType() {
        String st = "urn:schemas-upnp-org:service: WANDSLLinkConfig:1";
        ServiceType serviceType = ServiceType.valueOf(st);
        assertEquals(serviceType.getNamespace(), "schemas-upnp-org");
        assertEquals(serviceType.getType(), "WANDSLLinkConfig");
        assertEquals(serviceType.getVersion(), 1);
    }


    @Test
    public void validateIntelServiceId() {
        // The Intel UPnP tools NetworkLight sends a valid but weird identifier with a dot
        ServiceId serviceId = ServiceId.valueOf("urn:upnp-org:serviceId:DimmingService.0001");
        assertEquals(serviceId.getNamespace(), "upnp-org");
        assertEquals(serviceId.getId(), "DimmingService.0001");

        // TODO: UPNP VIOLATION: The Intel UPnP tools MediaRenderer sends an invalid identifier, we need to deal
        serviceId = ServiceId.valueOf("urn:schemas-upnp-org:service:AVTransport");
        assertEquals(serviceId.getNamespace(), "upnp-org");
        assertEquals(serviceId.getId(), "AVTransport");
    }

    @Test
    public void readColonRelativePaths() throws Exception {
        // Funny URI paths for services, breaks the java.net.URI parser so we deal with this special, see UDA10DeviceDescriptorBinderImpl
        String descriptor =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\n" +
                        "   <specVersion>\n" +
                        "      <major>1</major>\n" +
                        "      <minor>0</minor>\n" +
                        "   </specVersion>\n" +
                        "   <device>\n" +
                        "      <deviceType>urn:schemas-upnp-org:device:BinaryLight:1</deviceType>\n" +
                        "      <presentationURL>/</presentationURL>\n" +
                        "      <friendlyName>Network Light (CB-1CE8FF0B14FA)</friendlyName>\n" +
                        "      <manufacturer>OpenSource</manufacturer>\n" +
                        "      <manufacturerURL>http://www.sourceforge.org</manufacturerURL>\n" +
                        "      <modelDescription>Software Emulated Light Bulb</modelDescription>\n" +
                        "      <modelName>Network Light Bulb</modelName>\n" +
                        "      <modelNumber>XPC-L1</modelNumber>\n" +
                        "      <modelURL>http://www.sourceforge.org/</modelURL>\n" +
                        "      <UDN>uuid:872843be-9fb4-4eb4-8250-0b629c047a27</UDN>\n" +
                        "      <iconList>\n" +
                        "         <icon>\n" +
                        "            <mimetype>image/png</mimetype>\n" +
                        "            <width>32</width>\n" +
                        "            <height>32</height>\n" +
                        "            <depth>32</depth>\n" +
                        "            <url>/icon.png</url>\n" +
                        "         </icon>\n" +
                        "         <icon>\n" +
                        "            <mimetype>image/jpg</mimetype>\n" +
                        "            <width>32</width>\n" +
                        "            <height>32</height>\n" +
                        "            <depth>32</depth>\n" +
                        "            <url>/icon.jpg</url>\n" +
                        "         </icon>\n" +
                        "      </iconList>\n" +
                        "      <serviceList>\n" +
                        "         <service>\n" +
                        "            <serviceType>urn:schemas-upnp-org:service:DimmingService:1</serviceType>\n" +
                        "            <serviceId>urn:upnp-org:serviceId:DimmingService.0001</serviceId>\n" +
                        "            <SCPDURL>_urn:upnp-org:serviceId:DimmingService.0001_scpd.xml</SCPDURL>\n" +
                        "            <controlURL>_urn:upnp-org:serviceId:DimmingService.0001_control</controlURL>\n" +
                        "            <eventSubURL>_urn:upnp-org:serviceId:DimmingService.0001_event</eventSubURL>\n" +
                        "         </service>\n" +
                        "         <service>\n" +
                        "            <serviceType>urn:schemas-upnp-org:service:SwitchPower:1</serviceType>\n" +
                        "            <serviceId>urn:upnp-org:serviceId:SwitchPower.0001</serviceId>\n" +
                        "            <SCPDURL>_urn:upnp-org:serviceId:SwitchPower.0001_scpd.xml</SCPDURL>\n" +
                        "            <controlURL>_urn:upnp-org:serviceId:SwitchPower.0001_control</controlURL>\n" +
                        "            <eventSubURL>_urn:upnp-org:serviceId:SwitchPower.0001_event</eventSubURL>\n" +
                        "         </service>\n" +
                        "      </serviceList>\n" +
                        "   </device>\n" +
                        "</root>";

        DeviceDescriptorBinder binder = new UDA10DeviceDescriptorBinderImpl();
        RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        device = binder.describe(device, descriptor);
        assertEquals(device.findServices().length, 2);
    }

    // TODO: UPNP VIOLATION: Roku Soundbridge cuts off callback URI path after 100 characters.
    @Test
    public void validateCallbackURILength() throws Exception {
        UpnpService upnpService = new MockUpnpService();
        Device dev = SampleData.createRemoteDevice(
                new RemoteDeviceIdentity(
                UDN.uniqueSystemIdentifier("I'mARokuSoundbridge"),
                1800,
                SampleDeviceRoot.getDeviceDescriptorURL(),
                null,
                SampleData.getLocalBaseAddress()
        ));
        Resource[] resources = upnpService.getConfiguration().getNamespace().getResources(dev);
        boolean test = false;
        for (Resource resource : resources) {
            if (!(resource instanceof ServiceEventCallbackResource)) {
                continue;
            }
            if (resource.getPathQuery().toString().length() < 100) test = true;
        }
        assertTrue(test);
    }

    // TODO: UPNP VIOLATION: Some devices use non-integer service/device type versions
    @Test
    public void parseUDADeviceTypeFractions() {
        UDADeviceType deviceType = (UDADeviceType) DeviceType.valueOf("urn:schemas-upnp-org:device:MyDeviceType:1.0");
        assertEquals(deviceType.getType(), "MyDeviceType");
        assertEquals(deviceType.getVersion(), 1);
        deviceType = (UDADeviceType) DeviceType.valueOf("urn:schemas-upnp-org:device:MyDeviceType:2.5");
        assertEquals(deviceType.getType(), "MyDeviceType");
        assertEquals(deviceType.getVersion(), 2);
    }

    // TODO: UPNP VIOLATION: Of course, adding more rules makes more devices compatible! DLNA genuises ftw!
    @Test
    public void parseInvalidDLNADoc() {
        DLNADoc doc = DLNADoc.valueOf("DMS 1.50"); // No hyphen
        assertEquals(doc.getDevClass(), "DMS");
        assertEquals(doc.getVersion(), DLNADoc.Version.V1_5.toString());
        assertEquals(doc.toString(), "DMS-1.50");
    }

    // TODO: UPNP VIOLATION: DirecTV HR23/700 High Definition DVR Receiver has invalid default value for statevar
    @Test
    public void invalidStateVarDefaultValue() {
        StateVariable stateVariable = new StateVariable(
                "Test",
                new StateVariableTypeDetails(
                        Datatype.Builtin.STRING.getDatatype(),
                        "A",
                        new String[] {"B", "C"},
                        null
                )
        );

        boolean foundA = false;
        for (String s : stateVariable.getTypeDetails().getAllowedValues()) {
            if (s.equals("A")) foundA = true;
        }
        assertEquals(foundA, true);
        assertEquals(stateVariable.getTypeDetails().getAllowedValues().length, 3);
        assertEquals(stateVariable.validate().size(), 0);
    }

    // TODO: UPNP VIOLATION: Onkyo NR-TX808 has a bug in RenderingControl service, switching maximum/minimum value range
    @Test
    public void switchedMinimumMaximumValueRange() {
        StateVariable stateVariable = new StateVariable(
                "Test",
                new StateVariableTypeDetails(
                        Datatype.Builtin.I2.getDatatype(),
                        null,
                        null,
                        new StateVariableAllowedValueRange(100, 0)
                )
        );

        assertEquals(stateVariable.validate().size(), 0);
        assertEquals(stateVariable.getTypeDetails().getAllowedValueRange().getMinimum(), 0);
        assertEquals(stateVariable.getTypeDetails().getAllowedValueRange().getMaximum(), 100);
    }

    // TODO: UPNP VIOLATION: Some renderers (like PacketVideo TMM Player) send
    // RelCount and AbsCount as "NOT_IMPLEMENTED" in GetPositionInfoResponse action.
    @Test
    public void invalidIntegerValue() {
        assertEquals(new IntegerDatatype(1).valueOf("NOT_IMPLEMENTED").intValue(), Byte.MAX_VALUE);
        assertEquals(new IntegerDatatype(2).valueOf("NOT_IMPLEMENTED").intValue(), Short.MAX_VALUE);
        assertEquals(new IntegerDatatype(4).valueOf("NOT_IMPLEMENTED").intValue(), Integer.MAX_VALUE);
    }

    @Test
    public void parseBrokenServiceType() {
        ServiceType serviceType = ServiceType.valueOf("urn:schemas-upnp-org:serviceId:Foo:1");
        assertEquals(serviceType.getNamespace(), "schemas-upnp-org");
        assertEquals(serviceType.getType(), "Foo");
        assertEquals(serviceType.getVersion(), 1);
    }

    @Test
    public void parseBrokenServiceId() {
        ServiceId serviceId = ServiceId.valueOf("urn:my-domain-namespace:service:MyService123");
        assertEquals(serviceId.getNamespace(), "my-domain-namespace");
        assertEquals(serviceId.getId(), "MyService123");
        assertEquals(serviceId.toString(), "urn:my-domain-namespace:serviceId:MyService123");
    }

    @Test
    public void parseServiceNameAsServiceId() {
        UDAServiceId serviceId = UDAServiceId.valueOf("ContentDirectory");
        assertEquals(serviceId.getNamespace(), UDAServiceId.DEFAULT_NAMESPACE);
        assertEquals(serviceId.getId(), "ContentDirectory");
        assertEquals(serviceId.toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:ContentDirectory");

        serviceId = UDAServiceId.valueOf("ConnectionManager");
        assertEquals(serviceId.getNamespace(), UDAServiceId.DEFAULT_NAMESPACE);
        assertEquals(serviceId.getId(), "ConnectionManager");
        assertEquals(serviceId.toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:ConnectionManager");

        serviceId = UDAServiceId.valueOf("RenderingControl");
        assertEquals(serviceId.getNamespace(), UDAServiceId.DEFAULT_NAMESPACE);
        assertEquals(serviceId.getId(), "RenderingControl");
        assertEquals(serviceId.toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:RenderingControl");

        serviceId = UDAServiceId.valueOf("AVTransport");
        assertEquals(serviceId.getNamespace(), UDAServiceId.DEFAULT_NAMESPACE);
        assertEquals(serviceId.getId(), "AVTransport");
        assertEquals(serviceId.toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:AVTransport");
    }

    // TODO: UPNP VIOLATION: EyeTV Netstream uses colons in device type token
    @Test
    public void parseEyeTVDeviceType() {
        DeviceType deviceType= DeviceType.valueOf("urn:schemas-microsoft-com:device:pbda:tuner:1");
        assertEquals(deviceType.getNamespace(), "schemas-microsoft-com");
        assertEquals(deviceType.getType(), "pbda-tuner");
        assertEquals(deviceType.getVersion(), 1);
    }

    // TODO: UPNP VIOLATION: EyeTV Netstream uses colons in service type token
    @Test
    public void parseEyeTVServiceType() {
        ServiceType serviceType= ServiceType.valueOf("urn:schemas-microsoft-com:service:pbda:tuner:1");
        assertEquals(serviceType.getNamespace(), "schemas-microsoft-com");
        assertEquals(serviceType.getType(), "pbda-tuner");
        assertEquals(serviceType.getVersion(), 1);
    }

    // TODO: UPNP VIOLATION: Ceyton InfiniTV uses colons in service type token and 'serviceId' instead of 'service'
    @Test
    public void parseCeytonInfiniTVServiceType() {
        ServiceType serviceType= ServiceType.valueOf("urn:schemas-opencable-com:serviceId:dri2:debug:1");
        assertEquals(serviceType.getNamespace(), "schemas-opencable-com");
        assertEquals(serviceType.getType(), "dri2-debug");
        assertEquals(serviceType.getVersion(), 1);
    }

    // TODO: UPNP VIOLATION: Handle garbage sent by Eyecon Android app
    @Test
    public void parseEyeconServiceId() {
        ServiceId serviceId = ServiceId.valueOf("urn:upnp-orgerviceId:urnchemas-upnp-orgervice:Foo");
        assertEquals(serviceId.getNamespace(), UDAServiceId.DEFAULT_NAMESPACE);
        assertEquals(serviceId.getId(), "Foo");
        assertEquals(serviceId.toString(), "urn:" + UDAServiceId.DEFAULT_NAMESPACE + ":serviceId:Foo");
    }

    // TODO: UPNP VIOLATION: Escient doesn't provide any device type token
    @Test
    public void parseEscientDeviceType() {
        DeviceType deviceType= DeviceType.valueOf("urn:schemas-upnp-org:device::1");
        assertEquals(deviceType.getNamespace(), "schemas-upnp-org");
        assertEquals(deviceType.getType(), DeviceType.UNKNOWN);
        assertEquals(deviceType.getVersion(), 1);
    }

    // TODO: UPNP VIOLATION: Kodak Media Server doesn't provide any service ID token
    @Test
    public void parseKodakServiceId() {
        ServiceId serviceId = ServiceId.valueOf("urn:upnp-org:serviceId:");
        assertEquals(serviceId.getNamespace(), "upnp-org");
        assertEquals(serviceId.getId(), ServiceId.UNKNOWN);
        assertEquals(serviceId.toString(), "urn:upnp-org:serviceId:" + ServiceId.UNKNOWN);
    }

}
