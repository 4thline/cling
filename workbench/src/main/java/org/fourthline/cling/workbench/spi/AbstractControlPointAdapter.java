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
import org.fourthline.cling.model.types.ServiceType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

/**
 * @author Christian Bauer
 */
@ApplicationScoped
public abstract class AbstractControlPointAdapter {


    public void onUseServiceRequest(@Observes UseService request) {
        for (ServiceType supportedServiceType : getSupportedServiceTypes()) {
            if (request.service.getServiceType().implementsVersion(supportedServiceType)) {
                onUseServiceRequest(request.service);
                break;
            }
        }
    }

    abstract protected ServiceType[] getSupportedServiceTypes();
    abstract protected void onUseServiceRequest(Service service);

}
