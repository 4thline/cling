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

package org.fourthline.cling.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;

/**
 * Provides a UPnP stack with Android configuration as an application service component.
 * <p>
 * Sends a search for all UPnP devices on instantiation. See the
 * {@link org.fourthline.cling.android.AndroidUpnpService} interface for a usage example.
 * </p>
 * <p/>
 * Override the {@link #createRouter(org.fourthline.cling.UpnpServiceConfiguration, org.fourthline.cling.protocol.ProtocolFactory, android.content.Context)}
 * and {@link #createConfiguration()} methods to customize the service.
 *
 * @author Christian Bauer
 */
public class AndroidUpnpServiceImpl extends Service {

    protected UpnpService upnpService;
    protected Binder binder = new Binder();

    @Override
    public void onCreate() {
        super.onCreate();

        upnpService = new UpnpServiceImpl(createConfiguration()) {
            @Override
            protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
                return AndroidUpnpServiceImpl.this.createRouter(
                    getConfiguration(),
                    protocolFactory,
                    AndroidUpnpServiceImpl.this
                );
            }
        };
    }

    protected AndroidUpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration();
    }

    protected AndroidSwitchableRouter createRouter(UpnpServiceConfiguration configuration,
                                                   ProtocolFactory protocolFactory,
                                                   Context context) {
        return new AndroidSwitchableRouter(configuration, protocolFactory, context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected class Binder extends android.os.Binder implements AndroidUpnpService {

        public UpnpService get() {
            return upnpService;
        }

        public UpnpServiceConfiguration getConfiguration() {
            return upnpService.getConfiguration();
        }

        public Registry getRegistry() {
            return upnpService.getRegistry();
        }

        public ControlPoint getControlPoint() {
            return upnpService.getControlPoint();
        }
    }

}