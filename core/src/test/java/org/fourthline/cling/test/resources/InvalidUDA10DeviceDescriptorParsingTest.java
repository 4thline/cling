/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.test.resources;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.binding.xml.RecoveringUDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.mock.MockUpnpServiceConfiguration;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.test.data.SampleData;
import org.seamless.util.io.IO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class InvalidUDA10DeviceDescriptorParsingTest {
	
    @DataProvider(name = "invalidXMLFile")
    public String[][] getInvalidXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/device/missing_namespaces.xml"},
            {"/invalidxml/device/ushare.xml"},
        };
    }

    /* ############################## TEST FAILURE ############################ */

    @Test(dataProvider = "invalidXMLFile", expectedExceptions = DescriptorBindingException.class)
    public void readDefaultFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        readDevice(invalidXMLFile, new MockUpnpService());
    }

    /* ############################## TEST SUCCESS ############################ */

    @Test(dataProvider = "invalidXMLFile")
    public void readRecovering(String invalidXMLFile) throws Exception {
        readDevice(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

	protected void readDevice(String invalidXMLFile, UpnpService upnpService) throws Exception {
		RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
		upnpService.getConfiguration().getDeviceDescriptorBinderUDA10()
            .describe(device, IO.readLines(getClass().getResourceAsStream(invalidXMLFile)));
	}

}

