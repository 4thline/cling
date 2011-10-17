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
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;

import java.net.URI;
import java.util.logging.Logger;

/**
 * @author Christian Bauer
 */
public abstract class NoMediaPresent<T extends AVTransport> extends AbstractState<T> {

    final private static Logger log = Logger.getLogger(Stopped.class.getName());

    public NoMediaPresent(T transport) {
        super(transport);
    }

    public void onEntry() {
        log.fine("Setting transport state to NO_MEDIA_PRESENT");
        getTransport().setTransportInfo(
                new TransportInfo(
                        TransportState.NO_MEDIA_PRESENT,
                        getTransport().getTransportInfo().getCurrentTransportStatus(),
                        getTransport().getTransportInfo().getCurrentSpeed()
                )
        );
        getTransport().getLastChange().setEventedValue(
                getTransport().getInstanceId(),
                new AVTransportVariable.TransportState(TransportState.NO_MEDIA_PRESENT),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    public abstract Class<? extends AbstractState> setTransportURI(URI uri, String metaData);

    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[] {
                TransportAction.Stop
        };
    }
}
