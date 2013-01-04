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

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.ServerClientTokens;

/**
 * @author Christian Bauer
 */
public class ServerHeader extends UpnpHeader<ServerClientTokens> {

    public ServerHeader() {
        setValue(new ServerClientTokens());
    }

    public ServerHeader(ServerClientTokens tokens) {
        setValue(tokens);
    }

    public void setString(String s) throws InvalidHeaderException {
        // TODO: This parsing is not as robust as I'd like, probably should use regexs instead

        // UDA 1.1/1.0 section 1.2.2 and RfC 2616, section 14.38
        // OSNAME/OSVERSION UPnP/1.x PRODUCTNAME/PRODUCTVERSION
        ServerClientTokens serverClientTokens = new ServerClientTokens();

        // They are all UNKNOWN at this point
        serverClientTokens.setOsName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setOsVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setProductName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setProductVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);

        // We definitely need a UPnP product token
        if (s.contains("UPnP/1.1")) {
            serverClientTokens.setMinorVersion(1);
        } else if (!s.contains("UPnP/1.")) {
            throw new InvalidHeaderException("Missing 'UPnP/1.' in server information: " + s);
        }

        // We might be lucky and the vendor has implemented other tokens correctly. so let's at least try
        try {

            int numberOfSpaces = 0;
            for( int i = 0; i < s.length(); i++ ) {
                if( s.charAt(i) == ' ' ) numberOfSpaces++;
            }

            String[] osNameVersion;
            String[] productNameVersion;

            if (s.contains(",")) {

                // Some guys think that the tokens are separated with commas, not whitespace (read section 3.8 of the HTTP spec!)
                String[] productTokens = s.split(",");
                osNameVersion = productTokens[0].split("/");
                productNameVersion = productTokens[2].split("/");

            } else if (numberOfSpaces > 2) {

                // Some guys think that whitespace in token names is OK... it's not ... but let's try...
                String beforeUpnpToken = s.substring(0, s.indexOf("UPnP/1.")).trim();
                String afterUpnpToken = s.substring(s.indexOf("UPnP/1.")+8).trim(); // Assumes minor version is 0-9!
                osNameVersion = beforeUpnpToken.split("/");
                productNameVersion = afterUpnpToken.split("/");

            } else {

                // Finally, how it is supposed to be, according to UPnP UDA 1.1 (not 1.0 and not HTTP spec!)
                String[] productTokens = s.split(" ");
                osNameVersion = productTokens[0].split("/");
                productNameVersion = productTokens[2].split("/");
            }


            serverClientTokens.setOsName(osNameVersion[0].trim());
            if (osNameVersion.length > 1) {
                serverClientTokens.setOsVersion(osNameVersion[1].trim());
            }
            serverClientTokens.setProductName(productNameVersion[0].trim());
            if (productNameVersion.length > 1) {
                serverClientTokens.setProductVersion(productNameVersion[1].trim());
            }

        } catch (Exception ex) {

            // If something goes wrong, go back to defaults
            serverClientTokens.setOsName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
            serverClientTokens.setOsVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
            serverClientTokens.setProductName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
            serverClientTokens.setProductVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);


            /* These are the rules:

              Many HTTP/1.1 header field values consist of words separated by LWS or special characters. These special
              characters MUST be in a quoted string to be used within a parameter value (as defined in section 3.6).

               token          = 1*<any CHAR except CTLs or separators>
               separators     = "(" | ")" | "<" | ">" | "@"
                              | "," | ";" | ":" | "\" | <">
                              | "/" | "[" | "]" | "?" | "="
                              | "{" | "}" | SP | HT
             */
        }

        setValue(serverClientTokens);
    }

    public String getString() {
        return getValue().getHttpToken();
    }
}
