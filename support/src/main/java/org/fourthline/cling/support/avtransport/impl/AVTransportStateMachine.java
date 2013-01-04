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

package org.fourthline.cling.support.avtransport.impl;

import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.fourthline.cling.support.model.SeekMode;
import org.seamless.statemachine.StateMachine;

import java.net.URI;

public interface AVTransportStateMachine extends StateMachine<AbstractState> {

    public abstract void setTransportURI(URI uri, String uriMetaData);
    public abstract void setNextTransportURI(URI uri, String uriMetaData);
    public abstract void stop();
    public abstract void play(String speed);
    public abstract void pause();
    public abstract void record();
    public abstract void seek(SeekMode unit, String target);
    public abstract void next();
    public abstract void previous();

}
