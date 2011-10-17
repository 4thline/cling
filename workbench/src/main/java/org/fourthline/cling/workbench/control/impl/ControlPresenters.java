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

package org.fourthline.cling.workbench.control.impl;

import org.fourthline.cling.workbench.control.ControlView;
import org.fourthline.cling.workbench.info.InvokeAction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * TODO: Is this injection/lifecycle behavior of CDI specified? The newly created
 * instances are dependent on the lifecycle of this application scoped instance.
 *
 * @author Christian Bauer
 */

@ApplicationScoped
public class ControlPresenters {

    @Inject
    Instance<ControlView.Presenter> controlPresenterInstance;

    public void onActionInvocationRequest(@Observes InvokeAction request) {
        controlPresenterInstance.get().init(
                request.action, request.presetInputValues
        );
    }
}
