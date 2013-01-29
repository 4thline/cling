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

package org.fourthline.cling.workbench.control.impl;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.support.shared.TextExpand;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.control.ControlView;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.swing.*;
import java.util.concurrent.Future;

public class ControlPresenter implements ControlView.Presenter {

    @Inject
    ControlView view;

    @Inject
    ControlPoint controlPoint;

    @Inject
    Event<TextExpand> textExpandEvent;

    protected Action action;
    protected Future actionExecutionFuture;

    @Override
    public void init(Action action, ActionArgumentValue[] presetInputValues) {
        this.action = action;
        view.setPresenter(this);
        view.init(action, presetInputValues);
    }

    @Override
    public void onInvoke() {

        ActionInvocation actionInvocation =
                new ActionInvocation(action, view.getInputValues());

        // Starts background thread
        Workbench.Log.ACTION_INVOCATION.info("Executing action: " + action.getName());
        ActionCallback actionCallback =
                new ControlActionCallback(actionInvocation) {
                    @Override
                    protected void onSuccess(final ActionArgumentValue[] values) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                view.setCancelEnabled(false);
                                view.setOutputValues(values);
                            }
                        });
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                view.setCancelEnabled(false);
                            }
                        });
                        super.failure(invocation, operation, defaultMsg);
                    }
                };
        actionExecutionFuture = controlPoint.execute(actionCallback);
        view.setCancelEnabled(true);
    }

    @Override
    public void onCancel() {
        view.setCancelEnabled(false);
        if (actionExecutionFuture != null) {
            Workbench.Log.ACTION_INVOCATION.info(
                "Interrupting action execution: " + action.getName()
            );
            actionExecutionFuture.cancel(true);
        }
    }

    @PreDestroy
    public void destroy() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.dispose();
            }
        });
    }

    @Override
    public void onExpandText(String text) {
        textExpandEvent.fire(new TextExpand(text));
    }
}
