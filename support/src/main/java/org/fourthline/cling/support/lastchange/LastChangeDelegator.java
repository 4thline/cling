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

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 * Any service implementation using the "LastChange" mechanism.
 * <p>
 * Required by the {@link LastChangeAwareServiceManager} to handle state readouts
 * of "LastChange"-using services for initial events GENA subscriptions. If you
 * want a specification compliant <em>AVTransport</em> or <em>RenderingControl</em>
 * UPnP service, your service implementation should implement this interface as well.
 * </p>
 *
 * @author Christian Bauer
 */
public interface LastChangeDelegator {

    public LastChange getLastChange();

    public void appendCurrentState(LastChange lc, UnsignedIntegerFourBytes instanceId) throws Exception;

    public abstract UnsignedIntegerFourBytes[] getCurrentInstanceIds();


}
