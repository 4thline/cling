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

package org.fourthline.cling.model.gena;

import java.util.LinkedHashMap;
import java.util.Map;

import org.fourthline.cling.model.UserConstants;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 * An established subscription, with identifer, expiration duration, sequence handling, and state variable values.
 * <p>
 * For every subscription, no matter if it's an incoming subscription to a local service,
 * or a local control point subscribing to a remote servce, an instance is maintained by
 * the {@link org.fourthline.cling.registry.Registry}.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class GENASubscription<S extends Service> {

    protected S service;
    protected String subscriptionId;
    protected int requestedDurationSeconds = UserConstants.DEFAULT_SUBSCRIPTION_DURATION_SECONDS;
    protected int actualDurationSeconds;
    protected UnsignedIntegerFourBytes currentSequence;
    protected Map<String, StateVariableValue<S>> currentValues = new LinkedHashMap<>();

    /**
     * Defaults to {@link org.fourthline.cling.model.UserConstants#DEFAULT_SUBSCRIPTION_DURATION_SECONDS}.
     */
    protected GENASubscription(S  service) {
        this.service = service;
    }

    public GENASubscription(S service, int requestedDurationSeconds) {
        this(service);
        this.requestedDurationSeconds = requestedDurationSeconds;
    }

    synchronized public S getService() {
        return service;
    }

    synchronized public String getSubscriptionId() {
        return subscriptionId;
    }

    synchronized public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    synchronized public int getRequestedDurationSeconds() {
        return requestedDurationSeconds;
    }

    synchronized public int getActualDurationSeconds() {
        return actualDurationSeconds;
    }

    synchronized public void setActualSubscriptionDurationSeconds(int seconds) {
        this.actualDurationSeconds = seconds;
    }

    synchronized public UnsignedIntegerFourBytes getCurrentSequence() {
        return currentSequence;
    }

    synchronized public Map<String, StateVariableValue<S>> getCurrentValues() {
        return currentValues;
    }

    public abstract void established();
    public abstract void eventReceived();

    @Override
    public String toString() {
        return "(GENASubscription, SID: " + getSubscriptionId() + ", SEQUENCE: " + getCurrentSequence() + ")";
    }
}
