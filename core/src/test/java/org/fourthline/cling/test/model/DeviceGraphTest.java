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

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceEmbeddedOne;
import org.fourthline.cling.test.data.SampleDeviceEmbeddedTwo;
import org.fourthline.cling.test.data.SampleDeviceRootLocal;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class DeviceGraphTest {

    @Test
    public void findRoot() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        LocalDevice root = ld.getEmbeddedDevices()[0].getRoot();
        assertEquals(root.getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

        root = ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getRoot();
        assertEquals(root.getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

    }

    @Test
    public void findEmbeddedDevices() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        LocalDevice[] embedded = ld.findEmbeddedDevices();
        assertEquals(embedded.length, 2);

        boolean haveOne = false, haveTwo = false;

        for (LocalDevice em : embedded) {
            if (em.getIdentity().getUdn().equals(ld.getEmbeddedDevices()[0].getIdentity().getUdn())) haveOne = true;
            if (em.getIdentity().getUdn().equals(ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getIdentity().getUdn()))
                haveTwo = true;
        }

        assert haveOne;
        assert haveTwo;
    }

    @Test
    public void findDevicesWithUDN() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        LocalDevice ldOne = ld.findDevice(SampleDeviceRootLocal.getRootUDN());
        assertEquals(ldOne.getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

        LocalDevice ldTwo = ld.findDevice(SampleDeviceEmbeddedOne.getEmbeddedOneUDN());
        assertEquals(ldTwo.getIdentity().getUdn(), SampleDeviceEmbeddedOne.getEmbeddedOneUDN());

        LocalDevice ldThree = ld.findDevice(SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());
        assertEquals(ldThree.getIdentity().getUdn(), SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());

        RemoteDevice rd = SampleData.createRemoteDevice();

        RemoteDevice rdOne = rd.findDevice(SampleDeviceRootLocal.getRootUDN());
        assertEquals(rdOne.getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

        RemoteDevice rdTwo = rd.findDevice(SampleDeviceEmbeddedOne.getEmbeddedOneUDN());
        assertEquals(rdTwo.getIdentity().getUdn(), SampleDeviceEmbeddedOne.getEmbeddedOneUDN());

        RemoteDevice rdThree = rd.findDevice(SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());
        assertEquals(rdThree.getIdentity().getUdn(), SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());

    }

    @Test
    public void findDevicesWithDeviceType() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        LocalDevice[] ldOne = ld.findDevices(ld.getType());
        assertEquals(ldOne.length, 1);
        assertEquals(ldOne[0].getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

        LocalDevice[] ldTwo = ld.findDevices(ld.getEmbeddedDevices()[0].getType());
        assertEquals(ldTwo.length, 1);
        assertEquals(ldTwo[0].getIdentity().getUdn(), SampleDeviceEmbeddedOne.getEmbeddedOneUDN());

        LocalDevice[] ldThree = ld.findDevices(ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getType());
        assertEquals(ldThree.length, 1);
        assertEquals(ldThree[0].getIdentity().getUdn(), SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());

        RemoteDevice rd = SampleData.createRemoteDevice();

        RemoteDevice[] rdOne = rd.findDevices(rd.getType());
        assertEquals(rdOne.length, 1);
        assertEquals(rdOne[0].getIdentity().getUdn(), SampleDeviceRootLocal.getRootUDN());

        RemoteDevice[] rdTwo = rd.findDevices(rd.getEmbeddedDevices()[0].getType());
        assertEquals(rdTwo.length, 1);
        assertEquals(rdTwo[0].getIdentity().getUdn(), SampleDeviceEmbeddedOne.getEmbeddedOneUDN());

        RemoteDevice[] rdThree = rd.findDevices(rd.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getType());
        assertEquals(rdThree.length, 1);
        assertEquals(rdThree[0].getIdentity().getUdn(), SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN());

    }

    @Test
    public void findServicesAll() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        Service one = ld.getServices()[0];
        Service two = ld.getEmbeddedDevices()[0].getServices()[0];
        Service three = ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getServices()[0];

        Service[] services = ld.findServices();

        boolean haveOne = false, haveTwo = false, haveThree = false;
        for (Service service : services) {
            if (service.getServiceId().equals(one.getServiceId())) haveOne = true;
            if (service.getServiceId().equals(two.getServiceId())) haveTwo = true;
            if (service.getServiceId().equals(three.getServiceId())) haveThree = true;
        }
        assert haveOne;
        assert haveTwo;
        assert haveThree;
    }

    @Test
    public void findServicesType() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        Service one = ld.getServices()[0];
        Service two = ld.getEmbeddedDevices()[0].getServices()[0];
        Service three = ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getServices()[0];

        Service[] services = ld.findServices(one.getServiceType());
        assertEquals(services.length, 1);
        assertEquals(services[0].getServiceId(), one.getServiceId());

        services = ld.findServices(two.getServiceType());
        assertEquals(services.length, 1);
        assertEquals(services[0].getServiceId(), two.getServiceId());

        services = ld.findServices(three.getServiceType());
        assertEquals(services.length, 1);
        assertEquals(services[0].getServiceId(), three.getServiceId());
    }

    @Test
    public void findServicesId() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        Service one = ld.getServices()[0];
        Service two = ld.getEmbeddedDevices()[0].getServices()[0];
        Service three = ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getServices()[0];

        Service service = ld.findService(one.getServiceId());
        assertEquals(service.getServiceId(), one.getServiceId());

        service = ld.findService(two.getServiceId());
        assertEquals(service.getServiceId(), two.getServiceId());

        service = ld.findService(three.getServiceId());
        assertEquals(service.getServiceId(), three.getServiceId());
    }

    @Test
    public void findServicesFirst() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        Service one = ld.getServices()[0];
        Service two = ld.getEmbeddedDevices()[0].getServices()[0];
        Service three = ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getServices()[0];

        Service service = ld.findService(one.getServiceType());
        assertEquals(service.getServiceId(), one.getServiceId());

        service = ld.findService(two.getServiceType());
        assertEquals(service.getServiceId(), two.getServiceId());

        service = ld.findService(three.getServiceType());
        assertEquals(service.getServiceId(), three.getServiceId());
    }

    @Test
    public void findServiceTypes() throws Exception {
        LocalDevice ld = SampleData.createLocalDevice();

        ServiceType[] svcTypes = ld.findServiceTypes();
        assertEquals(svcTypes.length, 3);

        boolean haveOne = false, haveTwo = false, haveThree = false;

        for (ServiceType svcType : svcTypes) {
            if (svcType.equals(ld.getServices()[0].getServiceType())) haveOne = true;
            if (svcType.equals(ld.getEmbeddedDevices()[0].getServices()[0].getServiceType())) haveTwo = true;
            if (svcType.equals(ld.getEmbeddedDevices()[0].getEmbeddedDevices()[0].getServices()[0].getServiceType()))
                haveThree = true;
        }

        assert haveOne;
        assert haveTwo;
        assert haveThree;

    }
}
