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

package org.fourthline.cling.binding.xml;

import org.w3c.dom.Node;

/**
 * Utility interface with static declarations of all "strings".
 *
 * @author Christian Bauer
 */
public abstract class Descriptor {

    public interface Device {

        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:device-1-0";
        public static final String DLNA_NAMESPACE_URI = "urn:schemas-dlna-org:device-1-0";
        public static final String DLNA_PREFIX = "dlna";
        public static final String SEC_NAMESPACE_URI = "http://www.sec.co.kr/dlna";
        public static final String SEC_PREFIX = "sec";

        public enum ELEMENT {
            root,
            specVersion, major, minor,
            URLBase,
            device,
            UDN,
            X_DLNADOC,
            X_DLNACAP,
            ProductCap,
            X_ProductCap,
            deviceType,
            friendlyName,
            manufacturer,
            manufacturerURL,
            modelDescription,
            modelName,
            modelNumber,
            modelURL,
            presentationURL,
            UPC,
            serialNumber,
            iconList, icon, width, height, depth, url, mimetype,
            serviceList, service, serviceType, serviceId, SCPDURL, controlURL, eventSubURL,
            deviceList;

            public static ELEMENT valueOrNullOf(String s) {
                try {
                    return valueOf(s);
                } catch (IllegalArgumentException ex) {
                    return null;
                }
            }

            public boolean equals(Node node) {
                return toString().equals(node.getLocalName());
            }
        }
    }

    public interface Service {

        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:service-1-0";

        public enum ELEMENT {
            scpd,
            specVersion, major, minor,
            actionList, action, name,
            argumentList, argument, direction, relatedStateVariable, retval,
            serviceStateTable, stateVariable, dataType, defaultValue,
            allowedValueList, allowedValue, allowedValueRange, minimum, maximum, step;

            public static ELEMENT valueOrNullOf(String s) {
                try {
                    return valueOf(s);
                } catch (IllegalArgumentException ex) {
                    return null;
                }
            }

            public boolean equals(Node node) {
                return toString().equals(node.getLocalName());
            }

        }

        public enum ATTRIBUTE {
            sendEvents
        }
    }

}
