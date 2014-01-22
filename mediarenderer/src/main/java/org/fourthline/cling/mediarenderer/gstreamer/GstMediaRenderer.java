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

package org.fourthline.cling.mediarenderer.gstreamer;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.mediarenderer.MediaRenderer;
import org.fourthline.cling.mediarenderer.display.DisplayHandler;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class GstMediaRenderer {

    public static final long LAST_CHANGE_FIRING_INTERVAL_MILLISECONDS = 500;

    final protected LocalServiceBinder binder = new AnnotationLocalServiceBinder();

    // These are shared between all "logical" player instances of a single service
    final protected LastChange avTransportLastChange = new LastChange(new AVTransportLastChangeParser());
    final protected LastChange renderingControlLastChange = new LastChange(new RenderingControlLastChangeParser());

    final protected Map<UnsignedIntegerFourBytes, GstMediaPlayer> mediaPlayers;

    final protected ServiceManager<GstConnectionManagerService> connectionManager;
    final protected LastChangeAwareServiceManager<GstAVTransportService> avTransport;
    final protected LastChangeAwareServiceManager<GstAudioRenderingControl> renderingControl;

    final protected LocalDevice device;

    protected DisplayHandler displayHandler;

    public GstMediaRenderer(int numberOfPlayers, final DisplayHandler displayHandler) {
        this.displayHandler = displayHandler;

        // This is the backend which manages the actual player instances
        mediaPlayers = new GstMediaPlayers(
                numberOfPlayers,
                avTransportLastChange,
                renderingControlLastChange
        ) {
            // These overrides connect the player instances to the output/display
            @Override
            protected void onPlay(GstMediaPlayer player) {
                getDisplayHandler().onPlay(player);
            }

            @Override
            protected void onStop(GstMediaPlayer player) {
                getDisplayHandler().onStop(player);
            }
        };

        // The connection manager doesn't have to do much, HTTP is stateless
        LocalService connectionManagerService = binder.read(GstConnectionManagerService.class);
        connectionManager =
                new DefaultServiceManager(connectionManagerService) {
                    @Override
                    protected Object createServiceInstance() throws Exception {
                        return new GstConnectionManagerService();
                    }
                };
        connectionManagerService.setManager(connectionManager);

        // The AVTransport just passes the calls on to the backend players
        LocalService<GstAVTransportService> avTransportService = binder.read(GstAVTransportService.class);
        avTransport =
                new LastChangeAwareServiceManager<GstAVTransportService>(
                        avTransportService,
                        new AVTransportLastChangeParser()
                ) {
                    @Override
                    protected GstAVTransportService createServiceInstance() throws Exception {
                        return new GstAVTransportService(avTransportLastChange, mediaPlayers);
                    }
                };
        avTransportService.setManager(avTransport);

        // The Rendering Control just passes the calls on to the backend players
        LocalService<GstAudioRenderingControl> renderingControlService = binder.read(GstAudioRenderingControl.class);
        renderingControl =
                new LastChangeAwareServiceManager<GstAudioRenderingControl>(
                        renderingControlService,
                        new RenderingControlLastChangeParser()
                ) {
                    @Override
                    protected GstAudioRenderingControl createServiceInstance() throws Exception {
                        return new GstAudioRenderingControl(renderingControlLastChange, mediaPlayers);
                    }
                };
        renderingControlService.setManager(renderingControl);

        try {

            device = new LocalDevice(
                    new DeviceIdentity(UDN.uniqueSystemIdentifier("Cling MediaRenderer")),
                    new UDADeviceType("MediaRenderer", 1),
                    new DeviceDetails(
                            "MediaRenderer on " + ModelUtil.getLocalHostName(false),
                            new ManufacturerDetails("Cling", "http://4thline.org/projects/cling/"),
                            new ModelDetails("Cling MediaRenderer", MediaRenderer.APPNAME, "1", "http://4thline.org/projects/cling/mediarenderer/")
                    ),
                    new Icon[]{createDefaultDeviceIcon()},
                    new LocalService[]{
                            avTransportService,
                            renderingControlService,
                            connectionManagerService
                    }
            );

        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        runLastChangePushThread();
    }

    // The backend player instances will fill the LastChange whenever something happens with
    // whatever event messages are appropriate. This loop will periodically flush these changes
    // to subscribers of the LastChange state variable of each service.
    protected void runLastChangePushThread() {
        // TODO: We should only run this if we actually have event subscribers
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // These operations will NOT block and wait for network responses
                        avTransport.fireLastChange();
                        renderingControl.fireLastChange();
                        Thread.sleep(LAST_CHANGE_FIRING_INTERVAL_MILLISECONDS);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }.start();
    }

    public LocalDevice getDevice() {
        return device;
    }

    synchronized public DisplayHandler getDisplayHandler() {
        return displayHandler;
    }

    synchronized public void setDisplayHandler(DisplayHandler displayHandler) {
        this.displayHandler = displayHandler;
    }

    synchronized public Map<UnsignedIntegerFourBytes, GstMediaPlayer> getMediaPlayers() {
        return mediaPlayers;
    }

    synchronized public void stopAllMediaPlayers() {
        for (GstMediaPlayer mediaPlayer : mediaPlayers.values()) {
            TransportState state =
                mediaPlayer.getCurrentTransportInfo().getCurrentTransportState();
            if (!state.equals(TransportState.NO_MEDIA_PRESENT) ||
                    state.equals(TransportState.STOPPED)) {
                MediaRenderer.APP.log(Level.FINE, "Stopping player instance: " + mediaPlayer.getInstanceId());
                mediaPlayer.stop();
            }
        }
    }

    public ServiceManager<GstConnectionManagerService> getConnectionManager() {
        return connectionManager;
    }

    public ServiceManager<GstAVTransportService> getAvTransport() {
        return avTransport;
    }

    public ServiceManager<GstAudioRenderingControl> getRenderingControl() {
        return renderingControl;
    }

    protected Icon createDefaultDeviceIcon() {
        String iconPath = "img/48/mediarenderer.png";
        try {
            return new Icon(
                "image/png",
                48, 48, 8,
                MediaRenderer.class.getName(),
                MediaRenderer.class.getResourceAsStream(iconPath)
            );
        } catch (IOException ex) {
            throw new RuntimeException("Could not load icon: " + iconPath, ex);
        }
    }

}
