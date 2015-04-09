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

package org.fourthline.cling.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.transport.RouterImpl;
import org.fourthline.cling.transport.spi.InitializationException;
import org.seamless.util.Exceptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitors all network connectivity changes, switching the router accordingly.
 *
 * @author Michael Pujos
 * @author Christian Bauer
 */
public class AndroidRouter extends RouterImpl {

    final private static Logger log = Logger.getLogger(Router.class.getName());

    final private Context context;

    private final WifiManager wifiManager;
    private final WifiP2pManager wifiP2pManager;
    private MyHandlerThread handlerThread;
    private final int[] allowedNetworkTypes;
    protected WifiManager.MulticastLock multicastLock;
    protected WifiManager.WifiLock wifiLock;
    protected BroadcastReceiver broadcastReceiver;
    protected BroadcastReceiver wifiP2pBroadcastReceiver;

    public AndroidRouter(AndroidUpnpServiceConfiguration configuration,
                         ProtocolFactory protocolFactory,
                         Context context) throws InitializationException {
        super(configuration, protocolFactory);

        this.context = context;
        this.wifiManager = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
        this.wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.allowedNetworkTypes = configuration.getAllowedNetworkTypes();
        Arrays.sort(allowedNetworkTypes);

        // Only register for network connectivity changes if we are not running on emulator
        if (!ModelUtil.ANDROID_EMULATOR) {
            this.broadcastReceiver = createConnectivityBroadcastReceiver();
            context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            this.wifiP2pBroadcastReceiver = createWifiP2pBroadcastReceiver();
            context.registerReceiver(wifiP2pBroadcastReceiver, new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION));
        }
    }

    protected BroadcastReceiver createConnectivityBroadcastReceiver() {
        return new ConnectivityBroadcastReceiver();
    }

    protected BroadcastReceiver createWifiP2pBroadcastReceiver() {
        return new WifiP2pBroadcastReceiver();
    }

    @Override
    protected int getLockTimeoutMillis() {
        return 15000;
    }

    @Override
    public void shutdown() throws RouterException {
        super.shutdown();

        this.handlerThread.quit();
        unregisterBroadcastReceiver();
    }

    @Override
    public boolean enable() throws RouterException {

        this.handlerThread = new MyHandlerThread(this);
        this.handlerThread.start();

        return true;
    }

    private boolean blockedEnable() throws RouterException {
        lock(writeLock);
        try {
            boolean enabled;
            if ((enabled = super.enable())) {
                // Enable multicast on the WiFi network interface,
                // requires android.permission.CHANGE_WIFI_MULTICAST_STATE
                if (hasWifiNetwork()) {
                    setWiFiMulticastLock(true);
                    setWifiLock(true);
                }
            }
            return enabled;
        } finally {
            unlock(writeLock);
        }
    }

    @Override
    public boolean disable() throws RouterException {

//        handlerThread.command.sendEmptyMessage(MSG_DISABLE);
        lock(writeLock);
        try {
            // Disable multicast on WiFi network interface,
            // requires android.permission.CHANGE_WIFI_MULTICAST_STATE
            setWiFiMulticastLock(false);
            setWifiLock(false);
            return super.disable();
        } finally {
            unlock(writeLock);
        }

//        return true;
    }

    private boolean blockedDisable() throws RouterException {

        lock(writeLock);
        try {
            // Disable multicast on WiFi network interface,
            // requires android.permission.CHANGE_WIFI_MULTICAST_STATE
            setWiFiMulticastLock(false);
            setWifiLock(false);
            return super.disable();
        } finally {
            unlock(writeLock);
        }

    }

    public boolean hasWifiNetwork() {
        for (NetworkInfo info : handlerThread.networkInfo) {
            if (NetworkUtils.isWifi(info)) {
                return true;
            }
        }

        return false;
    }

    public boolean enableWiFi() {
        log.info("Enabling WiFi...");
        try {
            return wifiManager.setWifiEnabled(true);
        } catch (Throwable t) {
            // workaround (HTC One X, 4.0.3)
            //java.lang.SecurityException: Permission Denial: writing com.android.providers.settings.SettingsProvider
            // uri content://settings/system from pid=4691, uid=10226 requires android.permission.WRITE_SETTINGS
            //	at android.os.Parcel.readException(Parcel.java:1332)
            //	at android.os.Parcel.readException(Parcel.java:1286)
            //	at android.net.wifi.IWifiManager$Stub$Proxy.setWifiEnabled(IWifiManager.java:1115)
            //	at android.net.wifi.WifiManager.setWifiEnabled(WifiManager.java:946)
            log.log(Level.WARNING, "SetWifiEnabled failed", t);
            return false;
        }
    }

    public void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }

        if (wifiP2pBroadcastReceiver != null) {
            context.unregisterReceiver(wifiP2pBroadcastReceiver);
            wifiP2pBroadcastReceiver = null;
        }
    }

    protected void setWiFiMulticastLock(boolean enable) {
        if (enable) {
            if (multicastLock == null) {
                multicastLock = wifiManager.createMulticastLock(getClass().getSimpleName());
            }

            if (multicastLock.isHeld()) {
                log.warning("WiFi multicast lock already acquired");
            } else {
                log.info("WiFi multicast lock acquired");
                multicastLock.acquire();
            }
        } else if (multicastLock != null) {
            if (multicastLock.isHeld()) {
                log.info("WiFi multicast lock released");
                multicastLock.release();
                multicastLock = null;
            } else {
                log.warning("WiFi multicast lock already released");
            }
        }
    }

    protected void setWifiLock(boolean enable) {
        if (enable) {
            if (wifiLock == null) {
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, getClass().getSimpleName());
            }

            if (wifiLock.isHeld()) {
                log.warning("WiFi lock already acquired");
            } else {
                log.info("WiFi lock acquired");
                wifiLock.acquire();
            }
        } else if (wifiLock != null) {
            if (wifiLock.isHeld()) {
                log.info("WiFi lock released");
                wifiLock.release();
                wifiLock = null;
            } else {
                log.warning("WiFi lock already released");
            }
        }
    }

    /**
     * Handles errors when network has been switched, during reception of
     * network switch broadcast. Logs a warning by default, override to
     * change this behavior.
     */
    protected void handleRouterExceptionOnNetworkTypeChange(RouterException ex) {
        Throwable cause = Exceptions.unwrap(ex);
        if (cause instanceof InterruptedException) {
            log.log(Level.INFO, "Router was interrupted: " + ex, cause);
        } else {
            log.log(Level.WARNING, "Router error on network change: " + ex, ex);
        }
    }

    class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
                return;

            Collection<NetworkInfo> infos = NetworkUtils.getConnectedNetworks(context);

            infos = filterAllowedNetworkTypes(infos);

            handlerThread.command.sendMessage(handlerThread.command.obtainMessage(MSG_NETWORK_INFO_CHANGED, infos));
        }
    }

    class WifiP2pBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!intent.getAction().equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION))
                return;

            final NetworkInfo newNetworkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            Collection<NetworkInfo> infos = NetworkUtils.getConnectedNetworks(context);
            if (newNetworkInfo.isConnected()) {
                infos.add(newNetworkInfo);
            }

            infos = filterAllowedNetworkTypes(infos);

            handlerThread.command.sendMessage(handlerThread.command.obtainMessage(MSG_NETWORK_INFO_CHANGED, infos));
        }
    }

    private Collection<NetworkInfo> filterAllowedNetworkTypes(final Collection<NetworkInfo> infos) {
        List<NetworkInfo> filtered = new ArrayList<>();

        for (final NetworkInfo info : infos) {
            if (Arrays.binarySearch(allowedNetworkTypes, info.getType()) >= 0) {
                filtered.add(info);
            }
        }

        return filtered;
    }

    private static final int MSG_ENABLE = 1;
    private static final int MSG_DISABLE = 2;
    private static final int MSG_NETWORK_INFO_CHANGED = 3;

    static class MyHandlerThread extends HandlerThread {

        boolean enabled = false;
//        boolean enableWhenReady = false;

        // K
        protected final Collection<NetworkInfo> networkInfo = Collections.synchronizedCollection(new ArrayList<NetworkInfo>());

        private final WeakReference<AndroidRouter> router;

        public MyHandlerThread(AndroidRouter router) {
            super("ClingAndroidRouter");

            this.router = new WeakReference<>(router);
        }

        Handler command;

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();

            command = new CommandHandler(this);
            if (router.get() != null && ModelUtil.ANDROID_EMULATOR) {
                this.networkInfo.addAll(NetworkUtils.getConnectedNetworks(router.get().context));
            }

            command.sendEmptyMessage(MSG_ENABLE);
        }

        static class CommandHandler extends Handler {
            private MyHandlerThread thread;

            // This signal will _really_ enable the Router, once a grace period is over and we have determined Androids
            // definite network state.
            private static final int MSG_INTERNAL_ENABLE = 11;
            private static final int INTERNAL_ENABLE_DELAY = 1500;

            // This signal will _really_ disable the router. HOWEVER, the disable call will be sent after a 3 second
            // delay, so that, if there are network changes, no resources will be wasted.
            private static final int MSG_INTERNAL_DISABLE = 12;
            private static final int INTERNAL_DISABLE_DELAY = 3000;

            private static final int MSG_INTERNAL_RESTART = 13;

            private static final int MSG_INTERNAL_HANDLE_NETWORK_CHANGE = 14;

            public CommandHandler(MyHandlerThread thread) {
                this.thread = thread;
            }

            @Override
            public void handleMessage(final Message msg) {
                if (thread.router.get() == null) {
                    log.log(Level.WARNING, "Router moved away, but still commands pending.");
                    return;
                }

                log.info("message: " + msg.what);

                if (msg.what == MSG_ENABLE) {
//                    thread.enableWhenReady = true;
                } else if (msg.what == MSG_DISABLE) {
                    sendEmptyMessage(MSG_INTERNAL_DISABLE);
                } else if (msg.what == MSG_NETWORK_INFO_CHANGED) {
//                    synchronized (thread.networkInfo) {
//
                    final Collection<NetworkInfo> previousNetworkList = thread.networkInfo;
                    final Collection<? extends NetworkInfo> newNetworkList = (Collection<? extends NetworkInfo>) msg.obj;

                    removeMessages(MSG_INTERNAL_HANDLE_NETWORK_CHANGE);
                    sendMessageDelayed(obtainMessage(MSG_INTERNAL_HANDLE_NETWORK_CHANGE, newNetworkList), 1000);

//                        thread.networkInfo.clear();
//                        thread.networkInfo.addAll(newNetworkList);
//
//                        for (NetworkInfo i : thread.networkInfo) {
//                            log.info("Will activate on network: " + i.getTypeName() + " (" + i.getType() + ")");
//                        }
//
//                        if (newNetworkList.size() == 0 && thread.enableWhenReady) {
//                            sendEmptyMessageDelayed(MSG_INTERNAL_DISABLE, INTERNAL_DISABLE_DELAY);
//                            return;
//                        }

                    // So the new network list is available plus we have not received anything, yet.
//                        if (newNetworkList.size() > 0 && thread.enableWhenReady) {
//                            sendEmptyMessage(MSG_INTERNAL_RESTART);
//                            return;
//                        }

//                        if (didNetworkChange(previousNetworkList, newNetworkList) && thread.enableWhenReady) {
//                            sendEmptyMessage(MSG_INTERNAL_RESTART);
//                        }
//                    }
                } else if (msg.what == MSG_INTERNAL_HANDLE_NETWORK_CHANGE) {
                    synchronized (thread.networkInfo) {
                        final Collection<NetworkInfo> prev = thread.networkInfo;
                        final Collection<? extends NetworkInfo> current = (Collection<? extends NetworkInfo>) msg.obj;

                        if (didNetworkChange(prev, current)) {
                            log.info("Network connection has changed, send restart signal.");

                            if (thread.enabled) {
                                sendEmptyMessage(MSG_INTERNAL_RESTART);
                            } else {
                                sendEmptyMessage(MSG_INTERNAL_ENABLE);
                            }

                            thread.networkInfo.clear();
                            thread.networkInfo.addAll(current);
                        } else {
                            log.info("Ignored network change.");
                        }
                    }
                } else if (msg.what == MSG_INTERNAL_ENABLE) {
                    try {
                        thread.enabled = thread.router.get().blockedEnable();
                    } catch (RouterException e) {
                        log.log(Level.SEVERE, "Not able to enable the router.");
                    }
                } else if (msg.what == MSG_INTERNAL_DISABLE) {
                    try {
                        thread.enabled = !thread.router.get().blockedDisable();
                    } catch (RouterException e) {
                        log.log(Level.SEVERE, "Not able to disable the router.");
                    }
                } else if (msg.what == MSG_INTERNAL_RESTART) {
                    log.info("Network connection has changed, restart the router.");
                    try {
                        thread.router.get().blockedDisable();

                        thread.router.get().blockedEnable();
                    } catch (RouterException e) {
                        log.log(Level.SEVERE, "Not able to restart the router.");
                    }
                }
            }

            private boolean didNetworkChange(final Collection<NetworkInfo> previousNetworkList, final Collection<? extends NetworkInfo> newNetworkList) {
                if (previousNetworkList.size() != newNetworkList.size()) {
                    return true;
                }

                SortedSet<Integer> previousTypes = new TreeSet<>();
                for (NetworkInfo entry : previousNetworkList) {
                    previousTypes.add(entry.getType());
                }

                SortedSet<Integer> newTypes = new TreeSet<>();
                for (NetworkInfo entry : newNetworkList) {
                    newTypes.add(entry.getType());
                }

                return !previousTypes.equals(newTypes);
            }
        }
    }
}