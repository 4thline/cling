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
package example.mediaserver;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.connectionmanager.callback.GetProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.seamless.util.MimeType;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * A simple ConnectionManager for HTTP-GET
 * <p>
 * If your transmission protocol is based on GET requests with HTTP - that is, your
 * media player will download or stream the media file from an HTTP server - all
 * you need to provide with your <em>MediaServer:1</em> is a very simple
 * <em>ConnectionManager:1</em>.
 * </p>
 * <p>
 * This connection manager doesn't actually manage any connections, in fact, it doesn't
 * have to provide any functionality at all. This is how you can create and bind this
 * simple service with the Cling Support bundled <code>ConnectionManagerService</code>:
 * </p>
 * <a class="citation" href="javacode://this#retrieveProtocolInfo" style="include: BIND1;"/>
 * <p>
 * You can now add this service to your <em>MediaServer:1</em> device and everything will work.
 * </p>
 * <p>
 * Many media servers however provide at least a list of "source" protocols. This list contains
 * all the (MIME) protocol types your media server might potentially have resources for.
 * A sink (renderer) would obtain this protocol information and decide upfront if
 * any resource from your media server can be played at all, without having to browse
 * the content and looking at each resource's type.
 * </p>
 * <p>
 * First, create a list of protocol information that is supported:
 * </p>
 * <a class="citation" href="javacode://example.mediaserver.MediaServerSampleData#createSourceProtocols()" style="include: PROT;"/>
 * <p>
 * You now have to customize the instantiation of the connection manager service,
 * passing the list of procotols as a constructor argument:
 * </p>
 * <a class="citation" href="javacode://this#retrieveProtocolInfo" style="include: BIND2;" id="bind2"/>
 *
 */
public class ConnectionManagerSimpleTest {

    @Test
    public void retrieveProtocolInfo() {
        final ProtocolInfos sourceProtocols = MediaServerSampleData.createSourceProtocols();

        LocalService<ConnectionManagerService> service =                                                // DOC: BIND1
                new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);

        service.setManager(
                new DefaultServiceManager<>(
                        service,
                        ConnectionManagerService.class
                )
        );                                                                                              // DOC: BIND1

        service = new AnnotationLocalServiceBinder().read(ConnectionManagerService.class);

        service.setManager(                                                                             // DOC: BIND2
            new DefaultServiceManager<ConnectionManagerService>(service, null) {
                @Override
                protected ConnectionManagerService createServiceInstance() throws Exception {
                    return new ConnectionManagerService(sourceProtocols, null);
                }
            }
        );                                                                                              // DOC: BIND2

        final boolean[] assertions = new boolean[1];

        ActionCallback getProtInfo = new GetProtocolInfo(service) {                                     // DOC: CALL

            @Override
            public void received(ActionInvocation actionInvocation,
                                 ProtocolInfos sinkProtocolInfos,
                                 ProtocolInfos sourceProtocolInfos) {

                assertEquals(sourceProtocolInfos.size(), 2);
                assertEquals(
                        sourceProtocolInfos.get(0).getContentFormatMimeType(),
                        MimeType.valueOf("audio/mpeg")
                );

                MimeType supportedMimeType = MimeType.valueOf("video/mpeg");

                for (ProtocolInfo source : sourceProtocolInfos) {
                    if (source.getContentFormatMimeType().isCompatible(supportedMimeType))
                        // ... It's supported!
                        assertions[0] = true; // DOC: EXC1
                }
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                // Something is wrong
            }
        };                                                                                              // DOC: CALL

        getProtInfo.run();
        
        for (boolean assertion : assertions) {
            assertEquals(assertion, true);
        }
    }
}
