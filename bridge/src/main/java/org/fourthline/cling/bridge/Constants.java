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

package org.fourthline.cling.bridge;

/**
 * @author Christian Bauer
 */
public class Constants {

    public static final String INIT_PARAM_LOCAL_BASE_URL = "org.fourthline.cling.bridge.localBaseURL";
    public static final String INIT_PARAM_LOGGING_CONFIG = "org.fourthline.cling.bridge.loggingConfig";

    public static final String ATTR_UPNP_SERVICE = Constants.class.getName() + ".upnpService";

    public static final String PARAM_UDN = "UDN";
    public static final String PARAM_SERVICE_ID_NS = "ServiceIdNamespace";
    public static final String PARAM_SERVICE_ID = "ServiceId";
    public static final String PARAM_ACTION_NAME = "ActionName";
    public static final String PARAM_ENDPOINT_ID = "EndpointId";

    public static final int LINK_DEFAULT_TIMEOUT_SECONDS = 300;

}
