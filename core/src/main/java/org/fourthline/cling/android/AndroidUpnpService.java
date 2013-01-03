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

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.registry.Registry;

/**
 * Interface of the Android UPnP application service component.
 * <p>
 * Usage example in an Android activity:
 * </p>
 * <pre>{@code
 *AndroidUpnpService upnpService;
 *
 *ServiceConnection serviceConnection = new ServiceConnection() {
 *     public void onServiceConnected(ComponentName className, IBinder service) {
 *         upnpService = (AndroidUpnpService) service;
 *     }
 *     public void onServiceDisconnected(ComponentName className) {
 *         upnpService = null;
 *     }
 *};
 *
 *public void onCreate(...) {
 * ...
 *     getApplicationContext().bindService(
 *         new Intent(this, AndroidUpnpServiceImpl.class),
 *         serviceConnection,
 *         Context.BIND_AUTO_CREATE
 *     );
 *}}</pre>
 *<p>
 * The default implementation requires permissions in <code>AndroidManifest.xml</code>:
 * </p>
 * <pre>{@code
 *<uses-permission android:name="android.permission.INTERNET"/>
 *<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 *<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
 *<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 *<uses-permission android:name="android.permission.WAKE_LOCK"/>
 *}</pre>
 * <p>
 * You also have to add the application service component:
 * </p>
 * <pre>{@code
 *<application ...>
 *  ...
 *  <service android:name="org.fourthline.cling.android.AndroidUpnpServiceImpl"/>
 *</application>
 * }</pre>
 *
 * @author Christian Bauer
 */
// DOC:CLASS
public interface AndroidUpnpService {

    /**
     * @return The actual main instance and interface of the UPnP service.
     */
    public UpnpService get();

    /**
     * @return The configuration of the UPnP service.
     */
    public UpnpServiceConfiguration getConfiguration();

    /**
     * @return The registry of the UPnP service.
     */
    public Registry getRegistry();

    /**
     * @return The client API of the UPnP service.
     */
    public ControlPoint getControlPoint();

}
// DOC:CLASS
