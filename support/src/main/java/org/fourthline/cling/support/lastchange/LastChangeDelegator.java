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
