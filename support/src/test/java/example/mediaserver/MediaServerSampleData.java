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
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

/**
 * @author Christian Bauer
 */
public class MediaServerSampleData {

 public static LocalService readService(Class<?> serviceClass) throws Exception {
        LocalService service = new AnnotationLocalServiceBinder().read(serviceClass);
        service.setManager(
                new DefaultServiceManager(service, serviceClass)
        );
        return service;
    }

    public static LocalDevice createDevice(Class<?> serviceClass) throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("1111")),
                new UDADeviceType("MediaServer"),
                new DeviceDetails("My MediaServer"),
                readService(serviceClass)
        );
    }

    public static ProtocolInfos createSourceProtocols() {
        final ProtocolInfos sourceProtocols =                                           // DOC: PROT
                new ProtocolInfos(
                        new ProtocolInfo(
                                Protocol.HTTP_GET,
                                ProtocolInfo.WILDCARD,
                                "audio/mpeg",
                                "DLNA.ORG_PN=MP3;DLNA.ORG_OP=01"
                        ),
                        new ProtocolInfo(
                                Protocol.HTTP_GET,
                                ProtocolInfo.WILDCARD,
                                "video/mpeg",
                                "DLNA.ORG_PN=MPEG1;DLNA.ORG_OP=01;DLNA.ORG_CI=0"
                        )
                );                                                                      // DOC: PROT
        return sourceProtocols;
    }

    
}
