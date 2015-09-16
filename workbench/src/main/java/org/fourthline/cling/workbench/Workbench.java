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
package org.fourthline.cling.workbench;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.support.shared.Main;
import org.fourthline.cling.workbench.main.impl.WorkbenchPresenter;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.seamless.cdi.weld.SeamlessWeldSEDeployment;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Logger;

// TODO: https://issues.jboss.org/browse/WELD-891
//@Singleton
@ApplicationScoped
public class Workbench extends Main {

    public static final String APPNAME = "Cling Workbench";

    public interface Log {
        Logger MAIN = Logger.getLogger("Workbench");
        Logger ACTION_INVOCATION = Logger.getLogger("Action Invocation");
        Logger EVENT_MONITOR = Logger.getLogger("Event Monitor");
    }

    public static final Weld weld = new Weld() {
        @Override
        protected Deployment createDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap) {
            return new SeamlessWeldSEDeployment(resourceLoader, bootstrap);
        }
    };

    public static final WeldContainer weldContainer = weld.initialize();

    public static void main(final String[] args) throws Exception {
        weldContainer.instance().select(Workbench.class).get().init();
    }

    @Inject
    WorkbenchPresenter rootPresenter;

    @Inject
    Event<UpnpService.Start> upnpServiceStartEvent;

    @Inject
    Event<UpnpService.Shutdown> upnpServiceShutdownEvent;

    @Override
    protected String getAppName() {
        return APPNAME;
    }

    @Override
    public void init() {
        super.init();
        upnpServiceStartEvent.fire(new UpnpService.Start());
        rootPresenter.init();
    }

    @Override
    public void shutdown() {
        upnpServiceShutdownEvent.fire(new UpnpService.Shutdown());
        removeLoggingHandler(); // Disable CDI logging handler before stopping CDI container
        weld.shutdown();
        super.shutdown();
    }

    public void onMainViewDisposed(@Observes WorkbenchPresenter.ViewDisposed vd) {
        shutdown();
    }
}
