/*
 * Copyright (C) 2011 4th Line GmbH, Switzerland
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
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportView;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;

/**
 *
 */
public class AVTransportPresenter implements AVTransportView.Presenter {

    final private static Logger log = Logger.getLogger(AVTransportPresenter.class.getName());

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
            protected void onCurrentTrackURIChange(int instanceId, String uri) {
                view.getInstanceView(instanceId).setCurrentTrackURI(uri);

            }
        };


        view.setPresenter(this);
        view.setTitle(service.getDevice().getDetails().getFriendlyName());

        // Register with the service for future LAST CHANGE events
        controlPoint.execute(eventCallback);

        // TODO: The initial event should contain values, section 2.3.1 rendering control spec
        log.info("Querying initial state of AVTransport service");
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
                        Workbench.log(new LogMessage(
                                "AVTransport ControlPointAdapter", "New transport URI set: " + uri
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe(defaultMsg);
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
                        Workbench.log(new LogMessage(
                                "AVTransport ControlPointAdapter", "Called 'Pause' action successfully"
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe(defaultMsg);
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
                        Workbench.log(new LogMessage(
                                "AVTransport ControlPointAdapter", "Called 'Play' action successfully"
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe(defaultMsg);
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
                        Workbench.log(new LogMessage(
                                "AVTransport ControlPointAdapter", "Called 'Stop' action successfully"
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe(defaultMsg);
                    }
                }
        );
    }

    @Override
    public void onSeekSelected(int instanceId, String target) {
        log.fine("Seeking to target time: " + target);
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
                        Workbench.log(new LogMessage(
                                "AVTransport ControlPointAdapter", "Called 'Seek' action successfully"
                        ));
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe(defaultMsg);
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
    public void onUpdatePositionInfo(final int instanceId) {
        controlPoint.execute(
                new GetPositionInfo(new UnsignedIntegerFourBytes(instanceId), service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, final PositionInfo positionInfo) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                log.info("Setting PositionInfo: " + positionInfo);
                                view.getInstanceView(instanceId).setProgress(positionInfo);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        // TODO: Is this really severe?
                        log.severe("Can't retrieve PositionInfo: " + defaultMsg);
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
                                log.info("Setting TransportState: " + transportInfo.getCurrentTransportState());
                                view.getInstanceView(instanceId).setState(transportInfo.getCurrentTransportState());
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        log.severe("Can't retrieve TransportInfo: " + defaultMsg);
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
                                log.info("Setting CurrentURI: " + mediaInfo.getCurrentURI());
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
                        log.severe("Can't retrieve initial MediaInfo: " + defaultMsg);
                    }
                }
        );
    }

}
