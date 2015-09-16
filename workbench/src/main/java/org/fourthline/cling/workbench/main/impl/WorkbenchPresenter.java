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

import org.fourthline.cling.support.shared.CenterWindow;
import org.fourthline.cling.support.shared.TextExpand;
import org.fourthline.cling.support.shared.TextExpandDialog;
import org.fourthline.cling.support.shared.log.LogView;
import org.fourthline.cling.workbench.browser.BrowserView;
import org.fourthline.cling.workbench.info.DevicesView;
import org.fourthline.cling.workbench.main.WorkbenchToolbarView;
import org.fourthline.cling.workbench.main.WorkbenchView;
import org.seamless.swing.Application;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

@ApplicationScoped
public class WorkbenchPresenter implements WorkbenchViewImpl.Presenter {

    static public class ViewDisposed {
    }

    @Inject
    protected WorkbenchView view;

    @Inject
    protected Event<ViewDisposed> viewDisposedEvent;

    @Inject
    protected WorkbenchToolbarView.Presenter toolbarPresenter;

    @Inject
    protected BrowserView.Presenter browserPresenter;

    @Inject
    protected DevicesView.Presenter deviceInfosPresenter;

    @Inject
    protected LogView.Presenter logPresenter;

    public void init() {
        view.setPresenter(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.setVisible();
            }
        });

        toolbarPresenter.init();
        browserPresenter.init();
        deviceInfosPresenter.init();
        logPresenter.init();
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
    public void onViewDisposed() {
        viewDisposedEvent.fire(new ViewDisposed());
    }

    protected void onTextExpand(@Observes TextExpand textExpand) {
        new TextExpandDialog(view.getFrame(), textExpand.getText());
    }

    public void onCenterWindow(@Observes CenterWindow centerWindow) {
        Application.center(centerWindow.getWindow(), view.getFrame());
    }

}
