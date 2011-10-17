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

package org.fourthline.cling.workbench.control.impl;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.support.shared.TextExpand;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.control.ControlView;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

public class ControlPresenter implements ControlView.Presenter {

    @Inject
    ControlView view;

    @Inject
    ControlPoint controlPoint;

    @Inject
    Event<TextExpand> textExpandEvent;

    protected Action action;

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
        Workbench.log(
                "Action Invocation",
                "Executing action: " + actionInvocation.getAction().getName()
        );
        ActionCallback actionCallback =
                new ControlActionCallback(actionInvocation) {
                    @Override
                    protected void onSuccess(final ActionArgumentValue[] values) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                view.setOutputValues(values);
                            }
                        });

                    }
                };
        controlPoint.execute(actionCallback);
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
