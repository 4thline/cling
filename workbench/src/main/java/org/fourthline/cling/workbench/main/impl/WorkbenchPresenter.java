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

package org.fourthline.cling.workbench.main.impl;

import org.fourthline.cling.UpnpService;
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

    @Inject
    protected Event<UpnpService.Start> upnpServiceStartEvent;

    public void init() {

        upnpServiceStartEvent.fire(new UpnpService.Start());

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
