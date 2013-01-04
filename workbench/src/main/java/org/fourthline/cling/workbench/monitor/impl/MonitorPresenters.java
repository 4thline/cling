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

package org.fourthline.cling.workbench.monitor.impl;

import org.fourthline.cling.workbench.info.MonitorService;
import org.fourthline.cling.workbench.monitor.MonitorView;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Christian Bauer
 */
@ApplicationScoped
public class MonitorPresenters {

    @Inject
    Instance<MonitorView.Presenter> monitorPresenterInstance;

    public void onMonitorServiceRequest(@Observes MonitorService request) {
        monitorPresenterInstance.get().init(
                request.service
        );
    }
}
