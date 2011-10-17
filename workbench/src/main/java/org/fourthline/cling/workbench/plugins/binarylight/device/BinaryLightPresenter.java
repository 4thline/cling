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
import javax.swing.SwingUtilities;
import java.util.logging.Level;

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
            Workbench.log(Level.INFO, "Local demo device already exists!");
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
