/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
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

package org.fourthline.cling.demo.android.browser;

import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;

/**
 * @author Christian Bauer
 */
public class BrowserUpnpService extends AndroidUpnpServiceImpl {

    @Override
    protected AndroidUpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {

            /* The only purpose of this class is to show you how you'd
               configure the AndroidUpnpServiceImpl in your application:

            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 7000;
            }

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{
                    new UDAServiceType("SwitchPower")
                };
            }
            */
        };
    }
}
