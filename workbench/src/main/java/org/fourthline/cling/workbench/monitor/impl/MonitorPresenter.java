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

package org.fourthline.cling.workbench.monitor.impl;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.monitor.MonitorView;
import org.seamless.swing.logging.LogMessage;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class MonitorPresenter implements MonitorView.Presenter {

    @Inject
    protected MonitorView view;

    @Inject
    protected ControlPoint controlPoint;

    protected Service service;
    protected MonitorSubscriptionCallback callback;

    @Override
    public void init(Service service) {
        this.service = service;
        this.callback = new MonitorSubscriptionCallback(service);
        view.setPresenter(this);
        view.setTitle("Monitoring Service: " + service.getServiceType().toFriendlyString());
    }

    @Override
    public void onStartMonitoring() {
        Workbench.log(new LogMessage("Monitor Controller", "Subscribing monitor to: " + callback.getService()));
        controlPoint.execute(callback);
    }

    @Override
    public void onStopMonitoring() {
        Workbench.log(new LogMessage("Monitor Controller", "Unsubscribing from: " + callback.getService()));
        callback.end();
    }

    protected class MonitorSubscriptionCallback extends SubscriptionCallback {

        public MonitorSubscriptionCallback(Service service) {
            super(service);
        }

        public void eventReceived(final GENASubscription subscription) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    List<StateVariableValue> values = new ArrayList();
                    for (Map.Entry<String, StateVariableValue> entry :
                            ((Map<String, StateVariableValue>) subscription.getCurrentValues()).entrySet()) {
                        values.add(entry.getValue());
                    }
                    view.setValues(values);
                }
            });

            Workbench.log(new LogMessage("Monitor Controller", "Event received: " + new Date()));
        }

        public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    view.setStartStopEnabled(false, true);
                }
            });
            Workbench.log(new LogMessage("Monitor Controller", "Events missed: " + numberOfMissedEvents));
        }

        @Override
        protected void failed(final GENASubscription subscription,
                              final UpnpResponse responseStatus,
                              final Exception exception,
                              final String defaultMsg) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    String failureMessage;
                    if (responseStatus == null && exception == null) {
                        failureMessage = "Subscription failed: No response and no exception received";
                    } else {
                        failureMessage = responseStatus != null
                                ? "Subscription failed: " + responseStatus.getResponseDetails()
                                : "Subscription failed: " + exception.toString();
                    }

                    Workbench.log(new LogMessage(Level.SEVERE, "Monitor Controller", failureMessage));
                    view.setStartStopEnabled(true, false);

                }
            });
        }

        @Override
        public void established(GENASubscription subscription) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    view.setStartStopEnabled(false, true);
                }
            });
            Workbench.log(new LogMessage(
                    "Monitor Controller", "Subscription established for seconds: " + subscription.getActualDurationSeconds()
            ));
        }

        @Override
        public void ended(GENASubscription subscription, final CancelReason reason, UpnpResponse responseStatus) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    view.setStartStopEnabled(true, false);
                }
            });
            Workbench.log(new LogMessage(
                    "Monitor Controller", "Subscription ended" + (reason != null ? ": " + reason : "")
            ));
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
}