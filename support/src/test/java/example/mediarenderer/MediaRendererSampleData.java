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
package example.mediarenderer;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

/**
 * @author Christian Bauer
 */
public class MediaRendererSampleData {

    public static LocalService<AVTransportService> createAVTransportService() throws Exception {

        LocalService<AVTransportService> service =                                      // DOC:INC1
                new AnnotationLocalServiceBinder().read(AVTransportService.class);

        // Service's which have "logical" instances are very special, they use the
        // "LastChange" mechanism for eventing. This requires some extra wrappers.
        LastChangeParser lastChangeParser = new AVTransportLastChangeParser();

        service.setManager(
                new LastChangeAwareServiceManager<AVTransportService>(service, lastChangeParser) {
                    @Override
                    protected AVTransportService createServiceInstance() throws Exception {
                        return new AVTransportService(
                                MyRendererStateMachine.class,   // All states
                                MyRendererNoMediaPresent.class  // Initial state
                        );
                    }
                }
        );                                                                              // DOC:INC1
        return service;
    }
    
    public static LocalService<AudioRenderingControlService> createRenderingControlService() throws Exception {

        LocalService<AudioRenderingControlService> service =
                new AnnotationLocalServiceBinder().read(AudioRenderingControlService.class);

        LastChangeParser lastChangeParser = new RenderingControlLastChangeParser();

        service.setManager(
                new LastChangeAwareServiceManager<>(
                        service,
                        AudioRenderingControlService.class,
                        lastChangeParser
                )
        );
        return service;
    }

    public static LocalDevice createDevice() throws Exception {
        return new LocalDevice(
                new DeviceIdentity(new UDN("1111")),
                new UDADeviceType("MediaRenderer"),
                new DeviceDetails("My MediaRenderer"),
                new LocalService[]{
                        createAVTransportService(),
                        createRenderingControlService()
                }
        );
    }
    
    public static class AudioRenderingControlService extends AbstractAudioRenderingControl {

        @Override
        public boolean getMute(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
            return false;
        }

        @Override
        public void setMute(UnsignedIntegerFourBytes instanceId, String channelName, boolean desiredMute) throws RenderingControlException {

        }

        @Override
        public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
            return new UnsignedIntegerTwoBytes(50);
        }

        @Override
        public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName, UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {

        }

        @Override
        protected Channel[] getCurrentChannels() {
            return new Channel[] {
                    Channel.Master
            };
        }

        @Override
        public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
            return new UnsignedIntegerFourBytes[0];
        }
    }

}
