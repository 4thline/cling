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

package org.fourthline.cling.workbench.main.impl;


import org.fourthline.cling.transport.DisableRouter;
import org.fourthline.cling.transport.EnableRouter;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.main.CreateDemoDevice;
import org.fourthline.cling.workbench.main.WorkbenchToolbarView;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * @author Christian Bauer
 */
@ApplicationScoped
public class WorkbenchToolbarPresenter implements WorkbenchToolbarViewImpl.Presenter {

    @Inject
    protected WorkbenchToolbarView view;

    @Inject
    protected Event<CreateDemoDevice> createDemoDeviceEvent;

    @Inject
    protected Router router;

    @Inject
    protected Event<EnableRouter> enableRouterEvent;

    @Inject
    protected Event<DisableRouter> disableRouterEvent;

    @Override
    public void init() {
        view.setPresenter(this);
    }

    @Override
    public void onCreateDemoDevice() {
        createDemoDeviceEvent.fire(new CreateDemoDevice());
    }

    @Override
    public void onDisableNetwork() {
        disableRouterEvent.fire(new DisableRouter());
        try {
            view.onNetworkSwitch(router.isEnabled());
        } catch (RouterException ex) {
            Workbench.Log.MAIN.warning("Can't get current router state: " + ex);
        }
    }

    @Override
    public void onEnableNetwork() {
        enableRouterEvent.fire(new EnableRouter());
        try {
            view.onNetworkSwitch(router.isEnabled());
        } catch (RouterException ex) {
            Workbench.Log.MAIN.warning("Can't get current router state: " + ex);
        }
    }
}
