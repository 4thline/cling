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

/**
 * The agent string of the UPnP stack in network messages, either as a server or client.
 * <p>
 * Tries to detect the operating system name and version, defaults to {@link UserConstants}
 * for product name and version.
 * </p>
 *
 * @author Christian Bauer
 */
public class ServerClientTokens {

    public static final String UNKNOWN_PLACEHOLDER = "UNKNOWN";

    // TODO: This means we default to UDA 1.0
    private int majorVersion = 1;
    private int minorVersion = 0;

    private String osName  =  System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
    private String osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
    private String productName = UserConstants.PRODUCT_TOKEN_NAME;
    private String productVersion = UserConstants.PRODUCT_TOKEN_VERSION;

    public ServerClientTokens() {
    }

    public ServerClientTokens(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public ServerClientTokens(String productName, String productVersion) {
        this.productName = productName;
        this.productVersion = productVersion;
    }

    public ServerClientTokens(int majorVersion, int minorVersion, String osName, String osVersion, String productName, String productVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.osName = osName;
        this.osVersion = osVersion;
        this.productName = productName;
        this.productVersion = productVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    @Override
    public String toString() {
        return getOsName()+"/"+getOsVersion() 
                + " UPnP/" + getMajorVersion() + "." + getMinorVersion() + " "
                + getProductName() + "/" + getProductVersion();
    }

    public String getHttpToken() {
        return getOsName().replaceAll(" ", "_")+"/"+getOsVersion().replaceAll(" ", "_")
                + " UPnP/" + getMajorVersion() + "." + getMinorVersion() + " "
                + getProductName().replaceAll(" ", "_") + "/" + getProductVersion().replaceAll(" ", "_");
    }

    public String getOsToken() {
        return getOsName().replaceAll(" ", "_")+"/"+getOsVersion().replaceAll(" ", "_");
    }

    public String getProductToken() {
        return getProductName().replaceAll(" ", "_") + "/" + getProductVersion().replaceAll(" ", "_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerClientTokens that = (ServerClientTokens) o;

        if (majorVersion != that.majorVersion) return false;
        if (minorVersion != that.minorVersion) return false;
        if (!osName.equals(that.osName)) return false;
        if (!osVersion.equals(that.osVersion)) return false;
        if (!productName.equals(that.productName)) return false;
        if (!productVersion.equals(that.productVersion)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = majorVersion;
        result = 31 * result + minorVersion;
        result = 31 * result + osName.hashCode();
        result = 31 * result + osVersion.hashCode();
        result = 31 * result + productName.hashCode();
        result = 31 * result + productVersion.hashCode();
        return result;
    }
}
