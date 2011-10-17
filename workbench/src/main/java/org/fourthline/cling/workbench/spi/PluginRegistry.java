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

package org.fourthline.cling.workbench.spi;

import org.fourthline.cling.model.meta.Service;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.ServiceLoader;

@Singleton
public class PluginRegistry {

    ServiceLoader<ControlPointAdapter> controlPointLoader;

    @PostConstruct
    public void init() {
        controlPointLoader = ServiceLoader.load(ControlPointAdapter.class);
    }

/*
    public void onUseServiceRequest(@Observes UseService request) {
        ControlPointAdapter controlPointAdapter =
                getControlPointAdapter(request.service);
        if (controlPointAdapter != null) {
            controlPointAdapter.start(request.service);
        } else {
            Workbench.log(
                    Level.WARNING,
                    "PluginRegistry",
                    "No control point plugin available for service type: " + request.service.getServiceType()
            );
        }
    }
*/

    public ControlPointAdapter getControlPointAdapter(Service service) {
        // TODO: Just take the first, we really should provide a drop-down menu
        for (ControlPointAdapter controlPointAdapter : controlPointLoader) {
            if (controlPointAdapter.getServiceType().implementsVersion(service.getServiceType())) {
                return controlPointAdapter;
            }
        }
        return null;
    }

}
