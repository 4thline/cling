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

package org.fourthline.cling.controlpoint;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.event.ExecuteAction;
import org.fourthline.cling.controlpoint.event.Search;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Default implementation.
 * <p>
 * This implementation uses the executor returned by
 * {@link org.fourthline.cling.UpnpServiceConfiguration#getSyncProtocolExecutor()}.
 * </p>
 *
 * @author Christian Bauer
 */
@ApplicationScoped
public class ControlPointImpl implements ControlPoint {

    private static Logger log = Logger.getLogger(ControlPointImpl.class.getName());

    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    protected Registry registry;

    protected ControlPointImpl() {
    }

    @Inject
    public ControlPointImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, Registry registry) {
        log.fine("Creating ControlPoint: " + getClass().getName());
        
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        this.registry = registry;
    }

    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void search(@Observes Search search) {
        search(search.getSearchType(), search.getMxSeconds());
    }

    public void search() {
        search(new STAllHeader(), MXHeader.DEFAULT_VALUE);
    }

    public void search(UpnpHeader searchType) {
        search(searchType, MXHeader.DEFAULT_VALUE);
    }

    public void search(int mxSeconds) {
        search(new STAllHeader(), mxSeconds);
    }

    public void search(UpnpHeader searchType, int mxSeconds) {
        log.fine("Sending asynchronous search for: " + searchType.getString());
        getConfiguration().getAsyncProtocolExecutor().execute(
                getProtocolFactory().createSendingSearch(searchType, mxSeconds)
        );
    }

    public void execute(ExecuteAction executeAction) {
        execute(executeAction.getCallback());
    }

    public void execute(ActionCallback callback) {
        log.fine("Invoking action in background: " + callback);
        callback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(callback);
    }

    public void execute(SubscriptionCallback callback) {
        log.fine("Invoking subscription in background: " + callback);
        callback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutor().execute(callback);
    }
}
