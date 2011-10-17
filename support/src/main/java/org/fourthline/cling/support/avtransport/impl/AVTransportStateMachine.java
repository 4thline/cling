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
