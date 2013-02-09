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

package org.fourthline.cling.test.control;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.mock.MockRouter;
import org.fourthline.cling.mock.MockUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.SoapActionHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import org.fourthline.cling.model.meta.StateVariableTypeDetails;
import org.fourthline.cling.model.profile.ClientInfo;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.test.data.SampleData;
import org.fourthline.cling.test.data.SampleServiceOne;
import org.fourthline.cling.transport.RouterException;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;


public class ActionInvokeOutgoingTest {

    public static final String RESPONSE_SUCCESSFUL = "<?xml version=\"1.0\"?>\n" +
        " <s:Envelope\n" +
        "     xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "     s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "   <s:Body>\n" +
        "     <u:GetTargetResponse xmlns:u=\"urn:schemas-upnp-org:service:SwitchPower:1\">\n" +
        "       <RetTargetValue>0</RetTargetValue>\n" +
        "     </u:GetTargetResponse>\n" +
        "   </s:Body>\n" +
        " </s:Envelope>";

    public static final String RESPONSE_QUERY_VARIABLE = "<?xml version=\"1.0\"?>\n" +
        " <s:Envelope\n" +
        "     xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "     s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "   <s:Body>\n" +
        "     <u:QueryStateVariableResponse xmlns:u=\"urn:schemas-upnp-org:control-1-0\">\n" +
        "       <return>0</return>\n" +
        "     </u:QueryStateVariableResponse>\n" +
        "   </s:Body>\n" +
        " </s:Envelope>";

    public static final String RESPONSE_FAILURE = "<?xml version=\"1.0\"?>\n" +
        " <s:Envelope\n" +
        "     xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "     s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "   <s:Body>\n" +
        "     <s:Fault>\n" +
        "       <faultcode>s:Client</faultcode>\n" +
        "       <faultstring>UPnPError</faultstring>\n" +
        "       <detail>\n" +
        "         <UPnPError xmlns=\"urn:schemas-upnp-org:control-1-0\">\n" +
        "           <errorCode>611</errorCode>\n" +
        "           <errorDescription>A test string</errorDescription>\n" +
        "         </UPnPError>\n" +
        "       </detail>\n" +
        "     </s:Fault>\n" +
        "   </s:Body>\n" +
        " </s:Envelope>";

    public static final String RESPONSE_NEGATIVE_VALUE = "<?xml version=\"1.0\"?>\n" +
        " <s:Envelope\n" +
        "     xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "     s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "   <s:Body>\n" +
        "     <u:GetNegativeValueResponse xmlns:u=\"urn:schemas-upnp-org:service:MyService:1\">\n" +
        "       <Result>-1</Result>\n" + // That's an illegal value for this state var!
        "     </u:GetNegativeValueResponse>\n" +
        "   </s:Body>\n" +
        " </s:Envelope>";

    @Test
    public void callLocalGet() throws Exception {

        // Registery local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice ld = ActionSampleData.createTestDevice();
        LocalService service = ld.getServices()[0];
        upnpService.getRegistry().addDevice(ld);

        Action action = service.getAction("GetTarget");
        ActionInvocation actionInvocation = new ActionInvocation(action);

        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = true;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertions[0] = false;
            }

        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() == null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
        assertEquals(assertions[0], true);
        assertEquals(actionInvocation.getOutput().length, 1);
        assertEquals(actionInvocation.getOutput()[0].toString(), "0");

    }


    @Test
    public void callLocalWrongAction() throws Exception {

        // Registery local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice ld = ActionSampleData.createTestDevice();
        LocalService service = ld.getServices()[0];
        upnpService.getRegistry().addDevice(ld);

        assertEquals(service.getAction("NonExistentAction"), null);
    }

    @Test
    public void callLocalSetException() throws Exception {

        // Registery local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice ld = ActionSampleData.createTestDevice(ActionSampleData.LocalTestServiceThrowsException.class);
        LocalService service = ld.getServices()[0];
        upnpService.getRegistry().addDevice(ld);

        Action action = service.getAction("SetTarget");
        ActionInvocation actionInvocation = new ActionInvocation(action);

        actionInvocation.setInput("NewTargetValue", true);

        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = false;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assert operation == null; // Local calls don't have an operation
                assertions[0] = true;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() != null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
        assertEquals(assertions[0], true);

        assertEquals(actionInvocation.getFailure().getErrorCode(), ErrorCode.ACTION_FAILED.getCode());
        assertEquals(
            actionInvocation.getFailure().getMessage(),
            ErrorCode.ACTION_FAILED.getDescription() + ". Something is wrong."
        );
    }

    @Test
    public void callRemoteGet() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {

                    @Override
                    public StreamResponseMessage send(StreamRequestMessage msg) throws RouterException {
                        return super.send(msg);
                    }

                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            new StreamResponseMessage(RESPONSE_SUCCESSFUL)
                        };
                    }
                };
            }
        };

        // Register remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        UpnpHeaders extraHeaders = new UpnpHeaders();
        extraHeaders.add(UpnpHeader.Type.USER_AGENT.getHttpName(), "MyCustom/Agent");
        extraHeaders.add("X-Custom-Header", "foo");

        ActionInvocation actionInvocation =
            new ActionInvocation(
                action,
                new ClientInfo(extraHeaders)
            );

        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = true;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertions[0] = false;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() == null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
        assertEquals(assertions[0], true);

        StreamRequestMessage request = upnpService.getRouter().getSentStreamRequestMessages().get(0);

        // Mandatory headers
        assertEquals(
            request.getHeaders().getFirstHeaderString(UpnpHeader.Type.CONTENT_TYPE),
            ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );
        assertEquals(
            request.getHeaders().getFirstHeaderString(UpnpHeader.Type.SOAPACTION),
            "\"" + SampleServiceOne.getThisServiceType().toString() + "#GetTarget\""
        );

        // The extra headers
        assertEquals(
            request.getHeaders().getFirstHeaderString(UpnpHeader.Type.USER_AGENT),
            "MyCustom/Agent"
        );
        assertEquals(
            request.getHeaders().getFirstHeader("X-CUSTOM-HEADER"),
            "foo"
        );

        assertEquals(actionInvocation.getOutput().length, 1);
        assertEquals(actionInvocation.getOutput()[0].toString(), "0");

    }

    @Test
    public void callRemoteGetFailure() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR), RESPONSE_FAILURE)
                        };
                    }
                };
            }
        };

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = false;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertEquals(operation.getStatusCode(), UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                assertions[0] = true;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() != null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
        assertEquals(assertions[0], true);
        assertEquals(actionInvocation.getFailure().getErrorCode(), ErrorCode.INVALID_CONTROL_URL.getCode());
        assertEquals(
            actionInvocation.getFailure().getMessage(),
            "A test string"
        );

    }

    @Test
    public void callRemoteGetNotFoundFailure() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.NOT_FOUND))
                        };
                    }
                };
            }
        };

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = false;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertEquals(operation.getStatusCode(), UpnpResponse.Status.NOT_FOUND.getStatusCode());
                assertions[0] = true;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() != null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
        assertEquals(assertions[0], true);
        assertEquals(actionInvocation.getFailure().getErrorCode(), ErrorCode.ACTION_FAILED.getCode());
        assertEquals(
            actionInvocation.getFailure().getMessage(),
            ErrorCode.ACTION_FAILED.getDescription() + ". Non-recoverable remote execution failure: 404 Not Found."
        );

    }

    @Test
    public void callRemoteGetNoResponse() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = false;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assert operation == null;
                assertions[0] = true;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() != null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
        assertEquals(assertions[0], true);
        assertEquals(actionInvocation.getFailure().getErrorCode(), ErrorCode.ACTION_FAILED.getCode());
        assertEquals(
            actionInvocation.getFailure().getMessage(),
            ErrorCode.ACTION_FAILED.getDescription() + ". Connection error or no response received."
        );
    }

    @Test
    public void callRemoteNegativeValue() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            new StreamResponseMessage(RESPONSE_NEGATIVE_VALUE)
                        };
                    }
                };
            }
        };

        // Registery remote device and its service
        RemoteDevice device = new RemoteDevice(
            SampleData.createRemoteDeviceIdentity(),
            new UDADeviceType("MyDevice"),
            new DeviceDetails("JustATest"),
            new RemoteService(
                new UDAServiceType("MyService"),
                new UDAServiceId("MyService"),
                URI.create("/scpd.xml"),
                URI.create("/control"),
                URI.create("/events"),
                new Action[]{
                    new Action(
                        "GetNegativeValue",
                        new ActionArgument[]{
                            new ActionArgument("Result", "NegativeValue", ActionArgument.Direction.OUT)
                        }
                    )
                },
                new StateVariable[]{
                    new StateVariable(
                        "NegativeValue",
                        new StateVariableTypeDetails(Datatype.Builtin.UI4.getDatatype()),
                        new StateVariableEventDetails(false)
                    )
                }
            )
        );

        upnpService.getRegistry().addDevice(device);

        Action action = device.getServices()[0].getAction("GetNegativeValue");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = true;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertions[0] = false;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() == null;
        // The illegal "-1" value should have been converted (with warning) to 0
        assertEquals(actionInvocation.getOutput("Result").getValue(), new UnsignedIntegerFourBytes(0));
    }

    /* TODO: M-POST support
    @Test
    public void callRemoteGetMethodNotSupported() throws Exception {


        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage msg) {
                if (msg.getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
                    return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.METHOD_NOT_SUPPORTED));
                } else if (msg.getOperation().getMethod().equals(UpnpRequest.Method.MPOST)) {
                    return new StreamResponseMessage(RESPONSE_SUCCESSFUL);
                } else {
                    throw new RuntimeException("Received unknown msg: " + msg);
                }

            }
        };

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            public void failure(ActionInvocation invocation, UpnpResponse operation) {
                assertions[0] = false;
            }

            public void success(ActionInvocation invocation) {
                assertions[0] = true;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assertEquals(upnpService.getSentStreamRequestMessages().size(), 2);
        assertEquals(assertions[0], true);

        assertEquals(upnpService.getSentStreamRequestMessages().get(0).getOperation().getMethod(), UpnpRequest.Method.POST);
        assertEquals(upnpService.getSentStreamRequestMessages().get(0).getHeaders().getPrefix(UpnpHeader.Type.SOAPACTION), null);
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).getString(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );


        assertEquals(upnpService.getSentStreamRequestMessages().get(1).getOperation().getMethod(), UpnpRequest.Method.MPOST);
        MANHeader manHeader = upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.MAN, MANHeader.class);
        assert manHeader != null;
        assertEquals(upnpService.getSentStreamRequestMessages().get(1).getHeaders().getPrefix(UpnpHeader.Type.SOAPACTION), manHeader.getNamespace());
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).getString(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );

        assert actionInvocation.getFailure() == null;
        assertEquals(actionInvocation.getOutput().length, 1);
        assertEquals(actionInvocation.getOutput()[0].toString(), "0");

    }

    @Test
    public void callRemoteGetDoubleMethodNotSupported() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            public StreamResponseMessage[] getStreamResponseMessages() {
                return new StreamResponseMessage[] {
                    new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.METHOD_NOT_SUPPORTED)),
                    new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.METHOD_NOT_SUPPORTED))
                };
            }
        };

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getAction("GetTarget");

        ActionInvocation actionInvocation = new ActionInvocation(action);
        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            public void failure(ActionInvocation invocation, UpnpResponse operation) {
                assertEquals(operation.getStatusCode(), UpnpResponse.Status.METHOD_NOT_SUPPORTED.getStatusCode());
                assertions[0] = true;
            }

            public void success(ActionInvocation invocation) {
                assertions[0] = false;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assertEquals(upnpService.getSentStreamRequestMessages().size(), 2);
        assertEquals(assertions[0], true);

        assertEquals(upnpService.getSentStreamRequestMessages().get(0).getOperation().getMethod(), UpnpRequest.Method.POST);
        assertEquals(upnpService.getSentStreamRequestMessages().get(0).getHeaders().getPrefix(UpnpHeader.Type.SOAPACTION), null);
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).getString(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );


        assertEquals(upnpService.getSentStreamRequestMessages().get(1).getOperation().getMethod(), UpnpRequest.Method.MPOST);
        MANHeader manHeader = upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.MAN, MANHeader.class);
        assert manHeader != null;
        assertEquals(upnpService.getSentStreamRequestMessages().get(1).getHeaders().getPrefix(UpnpHeader.Type.SOAPACTION), manHeader.getNamespace());
        assertEquals(
                upnpService.getSentStreamRequestMessages().get(1).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).getString(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );

        assert actionInvocation.getFailure() != null;
        assertEquals(actionInvocation.getFailure().getErrorCode(), ErrorCode.ACTION_FAILED.getCode());
        assertEquals(
                actionInvocation.getFailure().getMessage(),
                ErrorCode.ACTION_FAILED.getDescription() + ". Second request (with MPOST) also failed, received Method Not Allowed again."
        );
    }
    */

    @Test
    public void callRemoteQueryStateVariable() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage[] getStreamResponseMessages() {
                        return new StreamResponseMessage[]{
                            new StreamResponseMessage(RESPONSE_QUERY_VARIABLE)
                        };
                    }
                };
            }
        };

        // Registery remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        Service service = SampleData.getFirstService(device);
        upnpService.getRegistry().addDevice(device);

        Action action = service.getQueryStateVariableAction();
        ActionInvocation actionInvocation = new ActionInvocation(action);
        actionInvocation.setInput("varName", "Target");

        final boolean[] assertions = new boolean[1];
        ActionCallback callback = new ActionCallback(actionInvocation) {
            @Override
            public void success(ActionInvocation invocation) {
                assertions[0] = true;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                assertions[0] = false;
            }
        };

        upnpService.getControlPoint().execute(callback);

        assert actionInvocation.getFailure() == null;
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 1);
        assertEquals(assertions[0], true);
        assertEquals(
            upnpService.getRouter().getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).getString(),
            ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8.toString()
        );
        assertEquals(
            upnpService.getRouter().getSentStreamRequestMessages().get(0).getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION, SoapActionHeader.class).getString(),
            "\"urn:schemas-upnp-org:control-1-0#QueryStateVariable\""
        );
        assertEquals(actionInvocation.getOutput().length, 1);
        assertEquals(actionInvocation.getOutput()[0].getArgument().getName(), "return");
        assertEquals(actionInvocation.getOutput()[0].toString(), "0");
    }


}
