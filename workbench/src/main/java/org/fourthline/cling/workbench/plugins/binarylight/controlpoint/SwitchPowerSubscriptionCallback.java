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

package org.fourthline.cling.workbench.plugins.binarylight.controlpoint;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.logging.LogMessage;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public class SwitchPowerSubscriptionCallback  extends SubscriptionCallback {

    private static Logger log = Logger.getLogger(SwitchPowerSubscriptionCallback.class.getName());

    protected final SwitchPowerPresenter presenter;

    public SwitchPowerSubscriptionCallback(Service service, SwitchPowerPresenter presenter) {
        super(service);
        this.presenter = presenter;
    }

    @Override
    protected void failed(GENASubscription subscription,
                          UpnpResponse responseStatus,
                          Exception exception,
                          String defaultMsg) {
        presenter.onConnectionFailure(defaultMsg);
    }

    public void established(GENASubscription subscription) {
        Workbench.log(
                "SwitchPower ControlPoint",
                "Subscription with service established, listening for events, renewing in seconds: " + subscription.getActualDurationSeconds()
        );
        presenter.onConnected();
    }

    public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse responseStatus) {
        Workbench.log(new LogMessage(
                reason != null ? Level.WARNING : Level.INFO,
                "SwitchPower ControlPoint",
                "Subscription with service ended. " + (reason != null ? "Reason: " + reason : "")
        ));
        presenter.onDisconnected();
    }

    public void eventReceived(GENASubscription subscription) {
        log.finer("Event received, sequence number: " + subscription.getCurrentSequence());

        Map<String, StateVariableValue> map = subscription.getCurrentValues();
        final StateVariableValue stateValue = map.get("Status");
        if (stateValue != null) {
            presenter.onStatusChange((Boolean)stateValue.getValue());
        }
    }

    public void eventsMissed(GENASubscription subscription, int numberOfMissedEvents) {
        log.warning("Events missed (" + numberOfMissedEvents + "), consider restarting this control point!");
    }

}

