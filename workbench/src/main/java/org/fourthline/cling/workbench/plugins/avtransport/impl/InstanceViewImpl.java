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

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.workbench.plugins.avtransport.AVTransportControlPoint;
import org.fourthline.cling.workbench.plugins.avtransport.InstanceView;
import org.seamless.statemachine.StateMachineBuilder;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;

/**
 * @author Christian Bauer
 */
public class InstanceViewImpl extends JPanel implements InstanceView {

    final protected PlayerPanel playerPanel = new PlayerPanel();
    final protected PlayModePanel playModePanel = new PlayModePanel();
    final protected ProgressPanel progressPanel = new ProgressPanel();
    final protected URIPanel uriPanel = new URIPanel();

    protected InstanceViewStateMachine viewStateMachine;
    protected int instanceId;
    protected Presenter presenter;
    private ItemListener playModeSpinnerItemListener;

    @Override
    public void init(int instanceId) {

        this.instanceId = instanceId;

        this.viewStateMachine = StateMachineBuilder.build(
                InstanceViewStateMachine.class,
                NoMediaPresent.class,
                new Class[]{InstanceViewImpl.class},
                new Object[]{this}
        );

        playerPanel.getPreviousButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onPreviousSelected(getInstanceId());
            }
        });

        playerPanel.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onNextSelected(getInstanceId());
            }
        });

        playerPanel.getFwdButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onSeekSelected(getInstanceId(), 15, true);
            }
        });

        playerPanel.getRewButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onSeekSelected(getInstanceId(), 15, false);
            }
        });

        playerPanel.getPauseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // TODO: Should "Pause" when already paused send a "Play" action or another "Pause" action?
                presenter.onPauseSelected(getInstanceId());
            }
        });

        playerPanel.getStopButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onStopSelected(getInstanceId());
            }
        });

        playerPanel.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onPlaySelected(getInstanceId());
            }
        });

        playModeSpinnerItemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    final PlayMode playMode = (PlayMode) event.getItem();
                    presenter.onSetPlayModeSelected(getInstanceId(), playMode);
                }
            }
        };
        playModePanel.getPlayModeSpinner().addItemListener(playModeSpinnerItemListener);

        progressPanel.getPositionSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) return;
                PositionInfo positionInfo = getProgressPanel().getPositionInfo();
                if (positionInfo != null) {
                    int newValue = source.getValue();
                    double seekTargetSeconds = newValue * positionInfo.getTrackDurationSeconds() / 100;
                    final String targetTime =
                            ModelUtil.toTimeString(
                                    new Long(Math.round(seekTargetSeconds)).intValue()
                            );

                    presenter.onSeekSelected(getInstanceId(), targetTime);
                }
            }
        });

        uriPanel.getSetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Some validation
                final String uri = uriPanel.getUriTextField().getText();
                if (uri == null || uri.length() == 0) return;
                try {
                    URI.create(uri);
                } catch (IllegalArgumentException ex) {
                    AVTransportControlPoint.LOGGER.warning(
                        "Invalid URI, can't set on AVTransport: " + uri
                    );
                }
                presenter.onSetAVTransportURISelected(getInstanceId(), uri);
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(playerPanel);
        add(playModePanel);
        add(progressPanel);
        add(uriPanel);
    }

    @Override
    public Component asUIComponent() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    // All the other methods are called by the GENA subscriptions with synchronization, this
    // is called by other people as well, so we synchronize
    @Override
    synchronized public void setState(TransportState state) {
        Class<? extends InstanceViewState> newClientState = InstanceViewState.STATE_MAP.get(state);
        if (newClientState != null) {
            try {
                viewStateMachine.forceState(newClientState);
            } catch (Exception ex) {
                AVTransportControlPoint.LOGGER.severe(
                    "Error switching client instance state: " + ex
                );
            }
        }
    }

    @Override
    synchronized public void setPlayMode(PlayMode playMode) {
        playModePanel.getPlayModeSpinner().removeItemListener(playModeSpinnerItemListener);
        playModePanel.getPlayModeSpinner().setSelectedItem(playMode);
        playModePanel.getPlayModeSpinner().addItemListener(playModeSpinnerItemListener);
    }

    @Override
    public PositionInfo getProgress() {
        return progressPanel.getPositionInfo();
    }

    @Override
    public void setProgress(PositionInfo positionInfo) {
        progressPanel.setProgress(positionInfo);
    }

    @Override
    public void setCurrentTrackURI(String uri) {
        uriPanel.getUriTextField().setText(uri);
    }

    @Override
    public void setSelectionEnabled(boolean enabled) {
        playerPanel.setBorder(BorderFactory.createTitledBorder(enabled ? "" : "DISABLED"));
        playerPanel.setAllButtons(enabled);
        progressPanel.getPositionSlider().setEnabled(enabled);
    }

    @Override
    public void dispose() {
        // End everything we do (background polling)
        setState(TransportState.STOPPED);
    }

    public int getInstanceId() {
        return instanceId;
    }

    public PlayerPanel getPlayerPanel() {
        return playerPanel;
    }

    public ProgressPanel getProgressPanel() {
        return progressPanel;
    }

    public URIPanel getUriPanel() {
        return uriPanel;
    }

    public Presenter getPresenter() {
        return presenter;
    }
}