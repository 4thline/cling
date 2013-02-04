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

package org.fourthline.cling.demo.android.browser;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

/**
 * @author Christian Bauer
 */
// DOC:CLASS
public class BrowserUpnpService extends AndroidUpnpServiceImpl {

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {

            // DOC:REGISTRY
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 7000;
            }
            // DOC:REGISTRY

            // DOC:SERVICE_TYPE
            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{
                    new UDAServiceType("SwitchPower")
                };
            }
            // DOC:SERVICE_TYPE
        };
    }
}
// DOC:CLASS
