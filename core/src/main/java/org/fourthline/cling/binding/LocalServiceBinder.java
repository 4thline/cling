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

package org.fourthline.cling.binding;

import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

/**
 * Reads {@link org.fourthline.cling.model.meta.LocalService} metadata given a Java class.
 *
 * @author Christian Bauer
 */
public interface LocalServiceBinder {

    /**
     * @param clazz The Java class that is the source of the service metadata.
     * @return The produced metadata.
     * @throws LocalServiceBindingException If binding failed.
     */
    public LocalService read(Class<?> clazz) throws LocalServiceBindingException;

    /**
     *
     * @param clazz The Java class that is the source of the service metadata.
     * @param id The pre-defined identifier of the service.
     * @param type The pre-defined type of the service.
     * @param supportsQueryStateVariables <code>true</code> if the service should support the
     *                                    deprecated "query any state variable value" action.
     * @param stringConvertibleTypes A list of Java classes which map directly to string-typed
     *                               UPnP state variables.
     * @return The produced metadata.
     * @throws LocalServiceBindingException If binding failed.
     */
    public LocalService read(Class<?> clazz, ServiceId id, ServiceType type,
                              boolean supportsQueryStateVariables, Class[] stringConvertibleTypes) throws LocalServiceBindingException;
}