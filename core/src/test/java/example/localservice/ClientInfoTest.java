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

package example.localservice;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.UserAgentHeader;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.profile.ClientInfo;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.InetAddress;

import static org.testng.Assert.assertEquals;

/**
 * Accessing client information
 * <p>
 * Theoretically, your service implementation should work with any client, as UPnP is
 * supposed to provide a compatibility layer. In practice, this never works as no
 * UPnP client and server is fully compatible with the specifications (except Cling, of
 * course).
 * </p>
 * <p>
 * If your action method has a last (or only parameter) of type <code>ClientInfo</code>,
 * Cling will provide details about the control point calling your service:
 * </p>
 * <a class="citation" href="javacode://example.localservice.SwitchPowerWithClientInfo" style="include:CLIENT_INFO"/>
 * <p>
 * The <code>ClientInfo</code> argument will only be available when this action method
 * is processing a remote client call, an <code>ActionInvocation</code> executed by the
 * local UPnP stack on a local service does not have client information and the argument
 * will be <code>null</code>.
 * </p>
 * <p>
 * Note that a client's remote address might be <code>null</code> if the Cling transport
 * layer was not able to obtain the connection's address.
 * </p>
 * <p>
 * You can set extra response headers on the <code>ClientInfo</code>, which will be
 * returned to the client with the response of your UPnP action.
 * </p>
 * <p>
 * If you want to set the user-agent header for requests made by your UPnP stack,
 * override the <code>StreamClientConfiguration#getUserAgentValue()</code> method
 * as explained in <a href="#section.BasicAPI.UpnpService.Configuration">UPnP
 * Service Configuration</a>.
 * </p>
 */
public class ClientInfoTest {

    public LocalDevice createTestDevice(Class serviceClass) throws Exception {

        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        LocalService svc = binder.read(serviceClass);
        svc.setManager(new DefaultServiceManager(svc, serviceClass));

        return new LocalDevice(
            SampleData.createLocalDeviceIdentity(),
            new UDADeviceType("BinaryLight", 1),
            new DeviceDetails("Example Binary Light"),
            svc
        );
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() {


        try {
            return new LocalDevice[][]{
                {createTestDevice(SwitchPowerWithClientInfo.class)}
            };
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            // Damn TestNG swallows exceptions in provider/factory methods
            throw new RuntimeException(ex);
        }
    }


    @Test(dataProvider = "devices")
    public void invokeActions(LocalDevice device) throws Exception {
        LocalService svc = device.getServices()[0];

        UpnpHeaders requestHeaders = new UpnpHeaders();
        requestHeaders.add(UpnpHeader.Type.USER_AGENT, new UserAgentHeader("foo/bar"));
        requestHeaders.add("X-MY-HEADER", "foo");

        ClientInfo clientInfo = new ClientInfo(
            InetAddress.getByName("10.0.0.1"),
            InetAddress.getByName("10.0.0.2"),
            requestHeaders
        );

        ActionInvocation setTargetInvocation = new ActionInvocation(
            svc.getAction("SetTarget"), clientInfo
        );

        setTargetInvocation.setInput("NewTargetValue", true);
        svc.getExecutor(setTargetInvocation.getAction()).execute(setTargetInvocation);
        assertEquals(setTargetInvocation.getFailure(), null);
        assertEquals(setTargetInvocation.getOutput().length, 0);

        assertEquals(clientInfo.getExtraResponseHeaders().getFirstHeader("X-MY-HEADER"), "foobar");
    }

}
