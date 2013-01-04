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

import org.fourthline.cling.support.shared.Main;
import org.fourthline.cling.support.shared.log.LogView;
import org.fourthline.cling.workbench.main.impl.WorkbenchPresenter;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.seamless.cdi.weld.SeamlessWeldSEDeployment;
import org.seamless.swing.logging.LogMessage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Level;

// TODO: https://issues.jboss.org/browse/WELD-891
//@Singleton
@ApplicationScoped
public class Workbench extends Main {

    public static final String APPNAME = "Cling Workbench";
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

    @Override
    public void init() {
        super.init();
        rootPresenter.init();
    }

    @Override
    protected String getAppName() {
        return APPNAME;
    }

    @Override
    public void shutdown() {
        weld.shutdown();
        super.shutdown();
    }

    public void onMainViewDisposed(@Observes WorkbenchPresenter.ViewDisposed vd) {
        shutdown();
    }

    // This is legacy static code. We push log messages directly into
    // the output window, without going through JUL.

    static public void log(String source, String msg) {
        log(Level.INFO, source, msg);
    }

    static public void log(Level level, String msg) {
        log(Level.INFO, null, msg);
    }

    static public void log(Level level, String source, String msg) {
        log(new LogMessage(level, source, msg));
    }

    static public void log(LogMessage logMessage) {
        weldContainer.instance().select(LogView.Presenter.class).get()
                .pushMessage(logMessage);
    }
}
