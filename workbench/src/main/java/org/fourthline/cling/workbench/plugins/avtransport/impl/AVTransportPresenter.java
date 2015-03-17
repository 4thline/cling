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

package org.fourthline.cling.workbench.plugins.avtransport.impl;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Next;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Previous;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.SetPlayMode;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportView;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.*;

/**
 * @author Christian Bauer
 */
public class AVTransportPresenter implements AVTransportView.Presenter {

    @Inject
    protected ControlPoint controlPoint;

    @Inject
    protected AVTransportView view;

    protected Service service;
    protected AVTransportCallback eventCallback;

    public void init(Service service) {
        this.service = service;

        this.eventCallback = new AVTransportCallback(service) {

            @Override
            protected void onDisconnect(final CancelReason reason) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String title = "DISCONNECTED: " + (reason != null ? reason.toString() : "");
                        view.setTitle(title);
                        view.setSelectionEnabled(false);
                    }
                });
            }

            @Override
            protected void onStateChange(int instanceId, TransportState state) {
                view.getInstanceView(instanceId).setState(state);
            }

            @Override
            protected void onPlayModeChange(int instanceId, PlayMode playMode) {
                view.getInstanceView(instanceId).setPlayMode(playMode);
            }

            @Override
            protected void onCurrentTrackURIChange(int instanceId, String uri) {
                view.getInstanceView(instanceId).setCurrentTrackURI(uri);

            }
        };


        view.setPresenter(this);
        view.setTitle(service.getDevice().getDetails().getFriendlyName());

        // Register with the service for future LAST CHANGE events
        controlPoint.execute(eventCallback);

        // TODO: The initial event should contain values, section 2.3.1 rendering control spec
        AVTransportControlPoint.LOGGER.info("Querying initial state of AVTransport service");
        for (int i = 0; i < AVTransportView.SUPPORTED_INSTANCES; i++) {
            updateTransportInfo(i);
            updateMediaInfo(i);
            onUpdatePositionInfo(i);
        }
    }

    @Override
    public void onViewDisposed() {
        if (eventCallback != null)
            eventCallback.end();
    }

    @PreDestroy
    public void destroy() {
        if (eventCallback != null)
            eventCallback.end();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.dispose();
            }
        });
    }

    @Override
    public void onSetAVTransportURISelected(int instanceId, final String uri) {
        controlPoint.execute(
                new SetAVTransportURI(new UnsignedIntegerFourBytes(instanceId), service, uri) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info("New transport URI set: " + uri);
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );

    }

    @Override
    public void onPauseSelected(int instanceId) {
        controlPoint.execute(
                new Pause(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                            "Called 'Pause' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onPlaySelected(int instanceId) {
        controlPoint.execute(
                new Play(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                            "Called 'Play' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onStopSelected(int instanceId) {
        controlPoint.execute(
                new Stop(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                            "Called 'Stop' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onSeekSelected(int instanceId, String target) {
        AVTransportControlPoint.LOGGER.fine("Seeking to target time: " + target);
        // First update the internal model, so fast clicks will trigger seeks with the right offset
        view.getInstanceView(instanceId).setProgress(
                new PositionInfo(
                        view.getInstanceView(instanceId).getProgress(),
                        target,
                        target
                )
        );

        // Now do the asynchronous remote seek
        controlPoint.execute(
                new Seek(new UnsignedIntegerFourBytes(instanceId), service, target) {
                    @Override
                    public void success(final ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                            "Called 'Seek' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onSeekSelected(int instanceId, int deltaSeconds, boolean forwards) {
        long currentSeconds = view.getInstanceView(instanceId).getProgress() != null
                ? view.getInstanceView(instanceId).getProgress().getTrackElapsedSeconds()
                : 0;
        long targetSeconds;
        if (forwards) {
            targetSeconds = currentSeconds + deltaSeconds;
        } else {
            targetSeconds = Math.min(0, currentSeconds - deltaSeconds);
        }
        onSeekSelected(instanceId, ModelUtil.toTimeString(targetSeconds));
    }


    @Override
    public void onPreviousSelected(int instanceId) {
        controlPoint.execute(
                new Previous(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                                "Called 'Previous' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onNextSelected(int instanceId) {
        controlPoint.execute(
                new Next(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                                "Called 'Next' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onUpdatePositionInfo(final int instanceId) {
        controlPoint.execute(
                new GetPositionInfo(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, final PositionInfo positionInfo) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AVTransportControlPoint.LOGGER.info("Setting PositionInfo: " + positionInfo);
                                view.getInstanceView(instanceId).setProgress(positionInfo);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        // TODO: Is this really severe?
                        AVTransportControlPoint.LOGGER.severe("Can't retrieve PositionInfo: " + defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onSetPlayModeSelected(final int instanceId, PlayMode playMode) {
        controlPoint.execute(
                new SetPlayMode(new UnsignedIntegerFourBytes(instanceId), service, playMode) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        AVTransportControlPoint.LOGGER.info(
                                "Called 'SetPlayMode' action successfully"
                        );
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        // TODO: Is this really severe?
                        AVTransportControlPoint.LOGGER.severe("Can't retrieve PositionInfo: " + defaultMsg);
                    }
                }
        );
    }

    public void updateTransportInfo(final int instanceId) {
        controlPoint.execute(
                new GetTransportInfo(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, final TransportInfo transportInfo) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AVTransportControlPoint.LOGGER.info(
                                    "Setting TransportState: " + transportInfo.getCurrentTransportState()
                                );
                                view.getInstanceView(instanceId).setState(transportInfo.getCurrentTransportState());
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe("Can't retrieve TransportInfo: " + defaultMsg);
                    }
                }
        );
    }

    public void updateMediaInfo(final int instanceId) {
        controlPoint.execute(
                new GetMediaInfo(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, final MediaInfo mediaInfo) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AVTransportControlPoint.LOGGER.info(
                                    "Setting CurrentURI: " + mediaInfo.getCurrentURI()
                                );
                                view.getInstanceView(instanceId).setCurrentTrackURI(
                                        mediaInfo.getCurrentURI()
                                );
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        AVTransportControlPoint.LOGGER.severe("Can't retrieve initial MediaInfo: " + defaultMsg);
                    }
                }
        );
    }

}
