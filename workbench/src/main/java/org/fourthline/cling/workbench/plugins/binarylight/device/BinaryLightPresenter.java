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

package org.fourthline.cling.workbench.plugins.binarylight.device;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.workbench.Workbench;
import org.fourthline.cling.workbench.main.CreateDemoDevice;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.swing.*;

/**
 * @author Christian Bauer
 */
@ApplicationScoped
public class BinaryLightPresenter implements BinaryLightView.Presenter {

    public static final UDN DEMO_DEVICE_UDN = UDN.uniqueSystemIdentifier("Demo Binary Light");

    @Inject
    protected Instance<BinaryLightView> viewInstance;

    @Inject
    protected UpnpService upnpService;

    protected BinaryLightView view;

    public void onCreate(@Observes CreateDemoDevice createDemoDevice) {

        if (upnpService.getRegistry().getLocalDevice(DEMO_DEVICE_UDN, true) != null) {
            Workbench.Log.MAIN.info("Local demo device already exists!");
            return;
        }

        LocalService service =
                new AnnotationLocalServiceBinder().read(DemoBinaryLight.class);

        service.setManager(
                new DefaultServiceManager(service) {
                    @Override
                    protected Object createServiceInstance() throws Exception {
                        return new DemoBinaryLight() {
                            @Override
                            public void setTarget(boolean newTargetValue) {
                                super.setTarget(newTargetValue);
                                view.setStatus(newTargetValue);
                            }
                        };
                    }
                }
        );

        try {
            upnpService.getRegistry().addDevice(
                    DemoBinaryLight.createDefaultDevice(DEMO_DEVICE_UDN, service)
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        view = viewInstance.get();
        view.setPresenter(this);
    }

    @Override
    public void onViewDisposed() {
        removeDevice();
    }

    @PreDestroy
    public void destroy() {
        removeDevice();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.dispose();
            }
        });
    }

    protected void removeDevice() {
        LocalDevice device;
        if ((device = upnpService.getRegistry().getLocalDevice(DEMO_DEVICE_UDN, true)) != null) {
            upnpService.getRegistry().removeDevice(device);
        }
    }

}
