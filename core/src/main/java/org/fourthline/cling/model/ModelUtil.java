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

package org.fourthline.cling.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

/**
 * Shared trivial procedures.
 *
 * @author Christian Bauer
 */
public class ModelUtil {

    /**
     * True if this class is executing on an Android runtime
     */
    final public static boolean ANDROID_RUNTIME;
    static {
        boolean foundAndroid = false;
        try {
            Class androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            foundAndroid = androidBuild.getField("ID").get(null) != null;
        } catch (Exception ex) {
            // Ignore
        }
        ANDROID_RUNTIME = foundAndroid;
    }

    /**
     * True if this class is executing on an Android emulator runtime.
     */
    final public static boolean ANDROID_EMULATOR;
    static {
        boolean foundEmulator = false;
        try {
            Class androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            String product = (String)androidBuild.getField("PRODUCT").get(null);
            if ("google_sdk".equals(product) || ("sdk".equals(product)))
                foundEmulator = true;
        } catch (Exception ex) {
            // Ignore
        }
        ANDROID_EMULATOR = foundEmulator;
    }

    /**
     * @param stringConvertibleTypes A collection of interfaces.
     * @param clazz An interface to test.
     * @return <code>true</code> if the given interface is an Enum, or if the collection contains a super-interface.
     */
    public static boolean isStringConvertibleType(Set<Class> stringConvertibleTypes, Class clazz) {
        if (clazz.isEnum()) return true;
        for (Class toStringOutputType : stringConvertibleTypes) {
            if (toStringOutputType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param name A UPnP device architecture "name" string.
     * @return <code>true</code> if the name is not empty, doesn't start with "xml", and
     *         matches {@link org.fourthline.cling.model.Constants#REGEX_UDA_NAME}.
     */
    public static boolean isValidUDAName(String name) {
        if (ANDROID_RUNTIME) {
            return name != null && name.length() != 0;
        }
        return name != null && name.length() != 0 && !name.toLowerCase().startsWith("xml") && name.matches(Constants.REGEX_UDA_NAME);
    }

    /**
     * Wraps the checked exception in a runtime exception.
     */
    public static InetAddress getInetAddressByName(String name) {
        try {
            return InetAddress.getByName(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts the given instances into comma-separated elements of a string,
     * escaping commas with backslashes.
     */
    public static String toCommaSeparatedList(Object[] o) {
        return toCommaSeparatedList(o, true, false);
    }

    /**
     * Converts the given instances into comma-separated elements of a string,
     * optionally escapes commas and double quotes with backslahses.
     */
    public static String toCommaSeparatedList(Object[] o, boolean escapeCommas, boolean escapeDoubleQuotes) {
        if (o == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : o) {
            String objString = obj.toString();
            objString = objString.replaceAll("\\\\", "\\\\\\\\"); // Replace one backslash with two (nice, eh?)
            if (escapeCommas) {
                objString = objString.replaceAll(",", "\\\\,");
            }
            if (escapeDoubleQuotes) {
                objString = objString.replaceAll("\"", "\\\"");
            }
            sb.append(objString).append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    /**
     * Converts the comma-separated elements of a string into an array of strings,
     * unescaping backslashed commas.
     */
    public static String[] fromCommaSeparatedList(String s) {
        return fromCommaSeparatedList(s, true);
    }

    /**
     * Converts the comma-separated elements of a string into an array of strings,
     * optionally unescaping backslashed commas.
     */
    public static String[] fromCommaSeparatedList(String s, boolean unescapeCommas) {
        if (s == null || s.length() == 0) {
            return null;
        }

        final String QUOTED_COMMA_PLACEHOLDER = "XXX1122334455XXX";
        if (unescapeCommas) {
            s = s.replaceAll("\\\\,", QUOTED_COMMA_PLACEHOLDER);
        }

        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll(QUOTED_COMMA_PLACEHOLDER, ",");
            split[i] = split[i].replaceAll("\\\\\\\\", "\\\\");
        }
        return split;
    }

    /**
     * @param seconds The number of seconds to convert.
     * @return A string representing hours, minutes, seconds, e.g. <code>11:23:44</code>
     */
    public static String toTimeString(long seconds) {
        long hours = seconds / 3600,
                remainder = seconds % 3600,
                minutes = remainder / 60,
                secs = remainder % 60;

        return ((hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (secs < 10 ? "0" : "") + secs);
    }

    /**
     * @param s A string representing hours, minutes, seconds, e.g. <code>11:23:44</code>
     * @return The converted number of seconds.
     */
    public static long fromTimeString(String s) {
        String[] split = s.split(":");
        return (Long.parseLong(split[0]) * 3600) +
                (Long.parseLong(split[1]) * 60) +
                (Long.parseLong(split[2]));
    }

    /**
     * @param s A string with commas.
     * @return The same string, a newline appended after every comma.
     */
    public static String commaToNewline(String s) {
        StringBuilder sb = new StringBuilder();
        String[] split = s.split(",");
        for (String splitString : split) {
            sb.append(splitString).append(",").append("\n");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * DNS reverse name lookup.
     *
     * @param includeDomain <code>true</code> if the whole FQDN should be returned, instead of just the first (host) part.
     * @return The resolved host (and domain-) name, or "UNKNOWN HOST" if resolution failed.
     */
    public static String getLocalHostName(boolean includeDomain) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return includeDomain
                    ? hostname
                    : hostname.indexOf(".") != -1 ? hostname.substring(0, hostname.indexOf(".")) : hostname;

        } catch (Exception ex) {
            // Return a dummy String
            return "UNKNOWN HOST";
        }
    }

    /**
     * @return The MAC hardware address of the first network interface of this host.
     */
    public static byte[] getFirstNetworkInterfaceHardwareAddress() {
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaceEnumeration)) {
                if (!iface.isLoopback() && iface.isUp() && iface.getHardwareAddress() != null) {
                    return iface.getHardwareAddress();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not discover first network interface hardware address");
        }
        throw new RuntimeException("Could not discover first network interface hardware address");
    }

}