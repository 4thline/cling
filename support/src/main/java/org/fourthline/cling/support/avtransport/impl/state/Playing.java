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

package org.fourthline.cling.support.avtransport.impl.state;

import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.model.AVTransport;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;

import java.net.URI;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public abstract class Playing<T extends AVTransport> extends AbstractState {

    final private static Logger log = Logger.getLogger(Playing.class.getName());

    public Playing(T transport) {
        super(transport);
    }

    public void onEntry() {
        log.fine("Setting transport state to PLAYING");
        getTransport().setTransportInfo(
                new TransportInfo(
                        TransportState.PLAYING,
                        getTransport().getTransportInfo().getCurrentTransportStatus(),
                        getTransport().getTransportInfo().getCurrentSpeed()
                )
        );
        getTransport().getLastChange().setEventedValue(
                getTransport().getInstanceId(),
                new AVTransportVariable.TransportState(TransportState.PLAYING),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    public abstract Class<? extends AbstractState> setTransportURI(URI uri, String metaData);
    public abstract Class<? extends AbstractState> stop();
    public abstract Class<? extends AbstractState> play(String speed);
    public abstract Class<? extends AbstractState> pause();
    public abstract Class<? extends AbstractState> next();
    public abstract Class<? extends AbstractState> previous();
    public abstract Class<? extends AbstractState> seek(SeekMode unit, String target);

    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[] {
                TransportAction.Stop,
                TransportAction.Play,
                TransportAction.Pause,
                TransportAction.Next,
                TransportAction.Previous,
                TransportAction.Seek
        };
    }
}
