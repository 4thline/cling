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

package org.fourthline.cling.model.meta;

import java.net.URI;

/**
 * Encpasulates optional metadata about a device's manufacturer.
 *
 * @author Christian Bauer
 */
public class ManufacturerDetails {

    private String manufacturer;
    private URI manufacturerURI;

    ManufacturerDetails() {
    }

    public ManufacturerDetails(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ManufacturerDetails(URI manufacturerURI) {
        this.manufacturerURI = manufacturerURI;
    }

    public ManufacturerDetails(String manufacturer, URI manufacturerURI) {
        this.manufacturer = manufacturer;
        this.manufacturerURI = manufacturerURI;
    }

    public ManufacturerDetails(String manufacturer, String manufacturerURI) throws IllegalArgumentException {
        this.manufacturer = manufacturer;
        this.manufacturerURI = URI.create(manufacturerURI);
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public URI getManufacturerURI() {
        return manufacturerURI;
    }
}
