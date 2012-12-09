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

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.ModelUtil;


import java.util.UUID;
import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * A unique device name.
 * <p>
 * UDA 1.0 does not specify a UUID format, however, UDA 1.1 specifies a format that is compatible
 * with <tt>java.util.UUID</tt> variant 4. You can use any identifier string you like.
 * </p>
 * <p>
 * You'll most likely need the {@link #uniqueSystemIdentifier(String)} method sooner or later.
 * </p>
 *
 * @author Christian Bauer
 */
public class UDN {

    public static final String PREFIX = "uuid:";

    private String identifierString;

    /**
     * @param identifierString The identifier string without the "uuid:" prefix.
     */
    public UDN(String identifierString) {
        this.identifierString = identifierString;
    }

    public UDN(UUID uuid) {
        this.identifierString = uuid.toString();
    }

    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(identifierString);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public String getIdentifierString() {
        return identifierString;
    }

    public static UDN valueOf(String udnString) {
        return new UDN(udnString.startsWith(PREFIX) ? udnString.substring(PREFIX.length()) : udnString);
    }

    /**
     * Generates a global unique identifier that is the same every time this method is invoked on the same machine.
     * <p>
     * This method discovers various pieces of information about the local system such
     * as hostname, MAC address, OS name and version. It then combines this information with the
     * given salt to generate a globally unique identifier. In other words, every time you
     * call this method with the same salt on the same machine, you get the same identifier.
     * If you use the same salt on a different machine, a different identifier will be generated.
     * </p>
     * <p>
     * Note for Android users: This method does not generate unique identifiers on Android devices with
     * the same OS name and version, because the hostname is always "localhost". Instead provide a unique
     * salt on each device, for example, a <code>UUID.randomUUID()</code>. When your application is first
     * started, generate all UUIDs needed for your UPnP devices and store them in your Android
     * preferences. Then, use the stored UUID to seed this function every time your application starts.
     * </p>
     * <p>
     * Control points can remember your device's identifier, it will be the same every time
     * your device is powered up.
     * </p>
     *
     * @param salt An arbitrary string that uniquely identifies the devices on the current system, e.g. "MyMediaServer".
     * @return A global unique identifier, stable for the current system and salt.
     */
    public static UDN uniqueSystemIdentifier(String salt) {
        StringBuilder systemSalt = new StringBuilder();

        try {
            java.net.InetAddress i = java.net.InetAddress.getLocalHost();
            systemSalt.append(i.getHostName()).append(i.getHostAddress());
        } catch (Exception ex) {
            // Could not find local host name, try to get the MAC address of loopback interface
            try {
                systemSalt.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress()));
            } catch (Throwable ex1) {
                // Ignore, we did everything we can
            	// catch Throwable so we catch  java.lang.NoSuchMethodError on Android because NetworkInterface.isLoopback() is'nt implemented
            }

        }
        systemSalt.append(System.getProperty("os.name"));
        systemSalt.append(System.getProperty("os.version"));
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(
                    new UUID(
                            new BigInteger(-1, hash).longValue(),
                            salt.hashCode()
                    )
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return PREFIX + getIdentifierString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UDN)) return false;
        UDN udn = (UDN) o;
        return identifierString.equals(udn.identifierString);
    }

    @Override
    public int hashCode() {
        return identifierString.hashCode();
    }

}
