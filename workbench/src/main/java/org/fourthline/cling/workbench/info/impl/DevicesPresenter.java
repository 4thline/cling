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

package org.fourthline.cling.workbench.info.impl;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.workbench.info.*;
import org.fourthline.cling.workbench.browser.RootDeviceSelected;
import org.fourthline.cling.workbench.spi.UseService;
import org.seamless.swing.Application;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DevicesPresenter implements DevicesView.Presenter, DeviceView.Presenter {

    @Inject
    DevicesView view;

    @Inject
    Instance<DeviceView> deviceViewInstance;

    @Inject
    UpnpServiceConfiguration configuration;

    @Inject
    Event<DeviceInfoSelectionChanged> deviceSelectionChangedEvent;

    @Inject
    Event<InvokeAction> actionInvocationRequestEvent;

    @Inject
    Event<MonitorService> monitorServiceEvent;

    @Inject
    Event<UseService> useServiceEvent;

    public void init() {
        view.setPresenter(this);
    }

    public void onRootDeviceSelected(@Observes RootDeviceSelected rootDeviceSelected) {
        if (!view.switchDeviceView(rootDeviceSelected.device)) {
            DeviceView deviceView = deviceViewInstance.select().get();
            deviceView.setPresenter(this);

            deviceView.setDevice(
                    configuration.getNamespace(),
                    rootDeviceSelected.icon,
                    rootDeviceSelected.device
            );

            view.addDeviceView(deviceView);
        }
    }

    @Override
    public void onDeviceViewChanged(DeviceView deviceView) {
        deviceSelectionChangedEvent.fire(
                new DeviceInfoSelectionChanged(deviceView.getDevice())
        );
    }

    @Override
    public void onDeviceViewClosed(DeviceView deviceView) {
        view.removeDeviceView(deviceView);
    }

    @Override
    public void onUseService(Service service) {
        useServiceEvent.fire(new UseService(service));
    }

    @Override
    public void onMonitorService(Service service) {
        monitorServiceEvent.fire(new MonitorService(service));
    }

    @Override
    public void onActionInvoke(Action action) {
        actionInvocationRequestEvent.fire(new InvokeAction(action));
    }

    @Override
    public void onQueryStateVariable(StateVariable stateVar) {
        Action action = stateVar.getService().getQueryStateVariableAction();
        actionInvocationRequestEvent.fire(new InvokeAction(
                action,
                new ActionArgumentValue(
                        action.getInputArgument(QueryStateVariableAction.INPUT_ARG_VAR_NAME),
                        stateVar.getName() // The literal variable name is the argument
                )
        ));
    }

    @Override
    public void onCopyInfoItem(InfoItem item) {
        Application.copyToClipboard(
                item.getData() != null ? item.getData().toString() : item.getInfo()
        );
    }

}
