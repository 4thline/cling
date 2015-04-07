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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.fourthline.cling.model.ModelUtil;

import java.util.logging.Logger;

/**
 * Android network helpers.
 *
 * @author Michael Pujos
 */
public class NetworkUtils {

    final private static Logger log = Logger.getLogger(NetworkUtils.class.getName());

    static public NetworkInfo getConnectedNetworkInfo(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        log.info("Could not find any connected network...");

        return null;
    }

    static public boolean isEthernet(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_ETHERNET);
    }

    static public boolean isWifi(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_WIFI) || ModelUtil.ANDROID_EMULATOR;
    }

    static public boolean isMobile(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_MOBILE) || isNetworkType(networkInfo, ConnectivityManager.TYPE_WIMAX);
    }

    static public boolean isNetworkType(NetworkInfo networkInfo, int type) {
        return networkInfo != null && networkInfo.getType() == type;
    }

    static public boolean isSSDPAwareNetwork(NetworkInfo networkInfo) {
        return isWifi(networkInfo) || isEthernet(networkInfo);
    }

}