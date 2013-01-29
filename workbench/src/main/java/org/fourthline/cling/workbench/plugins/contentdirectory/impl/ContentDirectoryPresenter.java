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

package org.fourthline.cling.workbench.plugins.contentdirectory.impl;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.workbench.plugins.contentdirectory.ContentDirectoryView;
import org.fourthline.cling.workbench.plugins.contentdirectory.DetailView;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

/**
 * @author Christian Bauer
 */
public class ContentDirectoryPresenter implements ContentDirectoryView.Presenter {

    @Inject
    protected UpnpService upnpService;

    @Inject
    protected ControlPoint controlPoint;

    @Inject
    protected ContentDirectoryView view;

    @Inject
    protected DetailView.Presenter detailPresenter;

    protected Service service;

    public void init(Service service) {
        view.setPresenter(this);
        view.setTitle("Content Directory on " + service.getDevice().getDetails().getFriendlyName());

        view.getTreeView().setPresenter(detailPresenter);
        view.getTreeView().init(controlPoint, service);

        detailPresenter.init(view.getDetailView());
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
