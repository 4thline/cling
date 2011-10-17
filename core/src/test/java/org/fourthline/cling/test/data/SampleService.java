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

package org.fourthline.cling.test.data;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * @author Christian Bauer
 */
public abstract class SampleService {

    public abstract ServiceType getServiceType();
    public abstract ServiceId getServiceId();
    public abstract URI getDescriptorURI();
    public abstract URI getControlURI();
    public abstract URI getEventSubscriptionURI();
    public abstract Action[] getActions();
    public abstract StateVariable[] getStateVariables();

    public <S extends Service> S newInstanceLocal(Constructor<S> ctor) {
        try {
            return ctor.newInstance(
                    getServiceType(), getServiceId(),
                    getActions(), getStateVariables()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <S extends Service> S newInstanceRemote(Constructor<S> ctor) {
        try {
            return ctor.newInstance(
                    getServiceType(), getServiceId(),
                    getDescriptorURI(), getControlURI(), getEventSubscriptionURI(),
                    getActions(), getStateVariables()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
