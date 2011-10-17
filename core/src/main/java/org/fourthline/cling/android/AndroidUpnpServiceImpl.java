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
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;

/**
 * Provides a UPnP stack with Android configuration (WiFi network only) as an application service component.
 * <p>
 * Sends a search for all UPnP devices on instantiation. See the {@link org.fourthline.cling.android.AndroidUpnpService}
 * interface for a usage example.
 * </p>
 * <p/>
 * Override the {@link #createRouter(org.fourthline.cling.UpnpServiceConfiguration, org.fourthline.cling.protocol.ProtocolFactory, android.net.wifi.WifiManager, android.net.ConnectivityManager)}
 * and {@link #createConfiguration(android.net.wifi.WifiManager)} methods to customize the service.
 *
 * @author Christian Bauer
 */
public class AndroidUpnpServiceImpl extends Service {

    protected UpnpService upnpService;
    protected Binder binder = new Binder();

    @Override
    public void onCreate() {
        super.onCreate();

        final WifiManager wifiManager =
                (WifiManager) getSystemService(Context.WIFI_SERVICE);

        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        upnpService = new UpnpServiceImpl(createConfiguration(wifiManager)) {
            @Override
            protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
                AndroidWifiSwitchableRouter router =
                        AndroidUpnpServiceImpl.this.createRouter(
                                getConfiguration(),
                                protocolFactory,
                                wifiManager,
                                connectivityManager
                        );
                if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges()) {
                    // Only register for network connectivity changes if we are not running on emulator
                    registerReceiver(
                            router.getBroadcastReceiver(),
                            new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
                    );
                }
                return router;
            }
        };

    }

    protected AndroidUpnpServiceConfiguration createConfiguration(WifiManager wifiManager) {
        return new AndroidUpnpServiceConfiguration(wifiManager);
    }

    protected AndroidWifiSwitchableRouter createRouter(UpnpServiceConfiguration configuration,
                                                      ProtocolFactory protocolFactory,
                                                      WifiManager wifiManager,
                                                      ConnectivityManager connectivityManager) {
        return new AndroidWifiSwitchableRouter(configuration, protocolFactory, wifiManager, connectivityManager);
    }

    @Override
    public void onDestroy() {
        if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges())
            unregisterReceiver(((AndroidWifiSwitchableRouter) upnpService.getRouter()).getBroadcastReceiver());
        upnpService.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected boolean isListeningForConnectivityChanges() {
        return true;
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