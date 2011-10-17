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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.SwitchableRouterImpl;
import org.fourthline.cling.transport.spi.InitializationException;

import java.util.logging.Logger;

/**
 * Switches the network transport layer on/off by monitoring WiFi connectivity.
 * <p>
 * This implementation listens to connectivity changes in an Android environment. Register the
 * {@link #getBroadcastReceiver()} instance with intent <code>android.net.conn.CONNECTIVITY_CHANGE</code>.
 * </p>
 *
 * @author Christian Bauer
 */
public class AndroidWifiSwitchableRouter extends SwitchableRouterImpl {

    private static Logger log = Logger.getLogger(Router.class.getName());

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) return;
            NetworkInfo wifiInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // We can't listen to "is available" or simply "is switched on", we have to make sure it's connected
            if (!wifiInfo.isConnected()) {
                log.info("WiFi state changed, trying to disable router");
                disable();
            } else {
                log.info("WiFi state changed, trying to enable router");
                enable();
            }
        }
    };

    final private WifiManager wifiManager;
    final private ConnectivityManager connectivityManager;
    private WifiManager.MulticastLock multicastLock;

    public AndroidWifiSwitchableRouter(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory,
                                       WifiManager wifiManager, ConnectivityManager connectivityManager) {
        super(configuration, protocolFactory);
        this.wifiManager = wifiManager;
        this.connectivityManager = connectivityManager;

        // Let's not wait for the first "wifi switched on" broadcast (which might be late on
        // some real devices and will never occur on the emulator)
        NetworkInfo wifiInfo = getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo.isConnected() || ModelUtil.ANDROID_EMULATOR) {
            log.info("WiFi is enabled (or running on Android emulator), starting router immediately");
            enable();
       }
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
    }

    protected WifiManager getWifiManager() {
        return wifiManager;
    }

    protected ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    @Override
    public boolean enable() throws RouterLockAcquisitionException {
        lock(writeLock);
        try {
            boolean enabled;
            if ((enabled = super.enable())) {
                // Enable multicast on the WiFi network interface, requires android.permission.CHANGE_WIFI_MULTICAST_STATE
                multicastLock = getWifiManager().createMulticastLock(getClass().getSimpleName());
                multicastLock.acquire();
            }
            return enabled;
        } finally {
            unlock(writeLock);
        }
    }

    @Override
    public void handleStartFailure(InitializationException ex) {
        if (multicastLock != null && multicastLock.isHeld()) {
            multicastLock.release();
            multicastLock = null;
        }
        super.handleStartFailure(ex);
    }

    @Override
    public boolean disable() throws RouterLockAcquisitionException {
        lock(writeLock);
        try {
            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
                multicastLock = null;
            }
            return super.disable();
        } finally {
            unlock(writeLock);
        }
    }
    
    @Override
    protected int getLockTimeoutMillis() {
        return 10000;
    }


}
