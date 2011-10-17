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

package org.fourthline.cling.workbench.plugins.contentdirectory;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;

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
