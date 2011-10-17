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

package org.fourthline.cling.workbench.plugins.igd;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.workbench.Workbench;
import org.seamless.swing.logging.LogMessage;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Christian Bauer
 */
public class PortMappingPresenterImpl implements PortMappingPresenter {

    @Inject
    protected ControlPoint controlPoint;

    @Inject
    protected UpnpServiceConfiguration configuration;

    protected WANIPConnectionPresenter parentPresenter;
    protected PortMappingListView listView;
    protected PortMappingEditView editView;

    protected Service service;

    @Override
    public void init(PortMappingListView listView, PortMappingEditView editView, Service service, WANIPConnectionPresenter parentPresenter) {
        this.listView = listView;
        listView.setPresenter(this);
        this.editView = editView;
        editView.setPresenter(this);

        this.service = service;

        this.parentPresenter = parentPresenter;

        updatePortMappings(service);
    }

    @Override
    public void onAddPortMapping() {
        PortMapping mapping = editView.getPortMapping();
        if (mapping != null)
            addPortMapping(mapping);
    }

    @Override
    public void onDeletePortMapping() {
        final PortMapping mapping = editView.getPortMapping();
        if (mapping != null)
            deletePortMapping(mapping);
    }

    @Override
    public void onReload() {
        // Eventing is broken on 4 routers I've tested, so let's just skip that and use action calls
        updatePortMappings(service);
        parentPresenter.updateConnectionInfo();
    }

    @Override
    public void onPortMappingSelected(PortMapping portMapping) {
        editView.setPortMapping(portMapping);
    }

    protected void deletePortMapping(final PortMapping mapping) {
        controlPoint.execute(
                new PortMappingDelete(service, mapping) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "WANIPConnection ControlPoint",
                                "Removed port mapping " + mapping.getProtocol() + "/" + mapping.getExternalPort()
                        ));
                        onReload();
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.WARNING,
                                "WANIPConnection ControlPoint",
                                "Port mapping removal failed: " + defaultMsg
                        ));
                    }
                }
        );
    }

    protected void addPortMapping(final PortMapping mapping) {
        controlPoint.execute(
                new PortMappingAdd(service, mapping) {
                    @Override
                    public void success(ActionInvocation invocation) {
                        Workbench.log(new LogMessage(
                                Level.INFO,
                                "WANIPConnection ControlPoint",
                                "Added port mapping " + mapping.getProtocol() + "/" + mapping.getExternalPort()
                        ));
                        onReload();
                    }

                    @Override
                    public void failure(ActionInvocation invocation,
                                        UpnpResponse operation,
                                        String defaultMsg) {
                        Workbench.log(new LogMessage(
                                Level.WARNING,
                                "WANIPConnection ControlPoint",
                                "Port mapping addition failed: " + defaultMsg
                        ));
                    }
                }
        );
    }

    protected void updatePortMappings(final Service service) {
        // Don't block the EDT
        configuration.getAsyncProtocolExecutor().execute(new Runnable() {
            public void run() {
                Workbench.log(new LogMessage(
                        Level.INFO,
                        "WANIPConnection ControlPoint",
                        "Updating list of port mappings"
                ));
                final List<PortMapping> mappings = new ArrayList();
                for (int i = 0; i < 65535; i++) { // You can't have more than 65535 port mappings
                    // Synchronous execution! And we stop when we hit a 713 response code because there
                    // is no other way to retrieve all mappings. The designers of this service are morons.
                    GetGenericPortMappingCallback invocation =
                            new GetGenericPortMappingCallback(controlPoint, service, i);
                    invocation.run();

                    if (invocation.isStopRetrieval()) break;

                    if (invocation.getMapping() != null) {
                        mappings.add(invocation.getMapping());
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        listView.setPortMappings(mappings.toArray(new PortMapping[mappings.size()]));
                    }
                });
            }
        });
    }

    class GetGenericPortMappingCallback extends ActionCallback {

        int index;
        PortMapping mapping;
        boolean stopRetrieval = false;

        GetGenericPortMappingCallback(ControlPoint controlPoint, Service service, int index) {
            super(new ActionInvocation(service.getAction("GetGenericPortMappingEntry")), controlPoint);
            this.index = index;
            getActionInvocation().setInput("NewPortMappingIndex", new UnsignedIntegerTwoBytes(index));
        }

        public PortMapping getMapping() {
            return mapping;
        }

        @Override
        public void success(ActionInvocation invocation) {
            mapping = new PortMapping(invocation.getOutputMap());
        }

        @Override
        public void failure(ActionInvocation invocation,
                            UpnpResponse operation,
                            String defaultMsg) {

            stopRetrieval = true;

            if (invocation.getFailure().getErrorCode() == 713) {
                // This is the _only_ way how we can know that we have retrieved an almost-up-to-date
                // list of all port mappings! Yes, the designer of this API was and probably still is
                // a moron.
                Workbench.log(new LogMessage(
                        Level.INFO,
                        "WANIPConnection ControlPoint",
                        "Retrieved all port mappings: " + index
                ));
            } else {
                Workbench.log(new LogMessage(
                        Level.WARNING,
                        "WANIPConnection ControlPoint",
                        "Error retrieving port mapping index '" + index + "': " + defaultMsg
                ));
            }
        }

        public boolean isStopRetrieval() {
            return stopRetrieval;
        }
    }
}
