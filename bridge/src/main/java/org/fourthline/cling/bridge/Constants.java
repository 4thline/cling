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
