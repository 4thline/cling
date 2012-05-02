package org.fourthline.cling.test.model;

import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.control.IncomingActionRequestMessage;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.protocol.sync.ReceivingAction;
import org.fourthline.cling.protocol.sync.ReceivingRetrieval;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleDeviceRoot;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Jarmolowiczj
 */
public class RequestAdressTest {
    private static final String LOCAL_HOST = "testLocalHost";
    private static final String REMOTE_HOST = "testRemoteHost";

    @Test
    public void validateLocalAdress() throws LocalServiceBindingException, Exception {
        LocalDevice device = new LocalDevice(SampleData.createLocalDeviceIdentity(), new UDADeviceType("TestDevice", 1), new DeviceDetails("Test Device"),
                new AnnotationLocalServiceBinder().read(TestServiceLocal.class));

        MockUpnpService upnpService = new MockUpnpService();

        // Register a device
        upnpService.getRegistry().addDevice(device);

        // Retrieve the descriptor
        StreamRequestMessage descRetrievalMessage = new StreamRequestMessage(UpnpRequest.Method.GET, SampleDeviceRoot.getDeviceDescriptorURI());
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader(LOCAL_HOST, 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();

    }
    
    @Test
    public void validateRemoteAdress() throws LocalServiceBindingException, Exception {
        LocalDevice device = new LocalDevice(SampleData.createLocalDeviceIdentity(), new UDADeviceType("TestDevice", 1), new DeviceDetails("Test Device"),
                new AnnotationLocalServiceBinder().read(TestServiceRemote.class));
        
        MockUpnpService upnpService = new MockUpnpService();
        
        // Register a device
        upnpService.getRegistry().addDevice(device);
        
        // Retrieve the descriptor
        StreamRequestMessage descRetrievalMessage = new StreamRequestMessage(UpnpRequest.Method.GET, SampleDeviceRoot.getDeviceDescriptorURI());
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader(REMOTE_HOST, 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();
        
    }

    /*
     * ##########################################################################
     * #############################
     */

    @UpnpService(serviceId = @UpnpServiceId("SomeService"), serviceType = @UpnpServiceType(value = "SomeService", version = 1), supportsQueryStateVariables = false)
    public static class TestServiceLocal {

        @UpnpStateVariable(sendEvents = false)
        private String clientAddress;

        @UpnpAction(out = @UpnpOutputArgument(name = "ClientAddress"))
        public String getAddress() {
            IncomingActionRequestMessage requestMessage = ReceivingAction.getRequestMessage();
            if (requestMessage != null) {
                Assert.assertEquals(requestMessage.getLocalAddress(), LOCAL_HOST);
                return requestMessage.getLocalAddress();
            }
            Assert.fail();
            return null;
        }
    }

    @UpnpService(serviceId = @UpnpServiceId("SomeService"), serviceType = @UpnpServiceType(value = "SomeService", version = 1), supportsQueryStateVariables = false)
    public static class TestServiceRemote {

        @UpnpStateVariable(sendEvents = false)
        private String clientAddress;

        @UpnpAction(out = @UpnpOutputArgument(name = "ClientAddress"))
        public String getAddress() {
            IncomingActionRequestMessage requestMessage = ReceivingAction.getRequestMessage();
            if (requestMessage != null) {
                Assert.assertEquals(requestMessage.getRemoteAddress(), LOCAL_HOST);
                return requestMessage.getRemoteAddress();
            }
            Assert.fail();
            return null;
        }
    }
}
