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

import org.fourthline.cling.model.Validatable;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Encapsulates all optional metadata about a device.
 *
 * @author Christian Bauer
 */
public class DeviceDetails implements Validatable {

    final private static Logger log = Logger.getLogger(DeviceDetails.class.getName());

    final private URL baseURL;
    final private String friendlyName;
    final private ManufacturerDetails manufacturerDetails;
    final private ModelDetails modelDetails;
    final private String serialNumber;
    final private String upc;
    final private URI presentationURI;
    final private DLNADoc[] dlnaDocs;
    final private DLNACaps dlnaCaps;
    final private DLNACaps secProductCaps; 

    public DeviceDetails(String friendlyName) {
        this(null, friendlyName, null, null, null, null, null);
    }

    public DeviceDetails(String friendlyName, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
            ModelDetails modelDetails, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps, DLNACaps secProductCaps) {
    	this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps, secProductCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, URI presentationURI) {
        this(null, friendlyName, null, null, null, null, presentationURI);
    }

    public DeviceDetails(String friendlyName, URI presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, URI presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, URI presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, String presentationURI)
            throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI));
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, String presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps)
            throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI), dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(URL baseURL, String friendlyName,
                         ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc,
                         URI presentationURI) {
        this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, null, null);
    }

    public DeviceDetails(URL baseURL, String friendlyName,
            ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
            String serialNumber, String upc,
            URI presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps) {
    	 this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps, null);
    }
    
    public DeviceDetails(URL baseURL, String friendlyName,
                         ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc,
                         URI presentationURI, DLNADoc[] dlnaDocs, DLNACaps dlnaCaps, DLNACaps secProductCaps) {
        this.baseURL = baseURL;
        this.friendlyName = friendlyName;
        this.manufacturerDetails = manufacturerDetails == null ? new ManufacturerDetails() : manufacturerDetails;
        this.modelDetails = modelDetails == null ? new ModelDetails() : modelDetails;
        this.serialNumber = serialNumber;
        this.upc = upc;
        this.presentationURI = presentationURI;
        this.dlnaDocs = dlnaDocs != null ? dlnaDocs : new DLNADoc[0];
        this.dlnaCaps = dlnaCaps;
        this.secProductCaps = secProductCaps;
    }

    public URL getBaseURL() {
        return baseURL;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ManufacturerDetails getManufacturerDetails() {
        return manufacturerDetails;
    }

    public ModelDetails getModelDetails() {
        return modelDetails;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getUpc() {
        return upc;
    }

    public URI getPresentationURI() {
        return presentationURI;
    }

    public DLNADoc[] getDlnaDocs() {
        return dlnaDocs;
    }

    public DLNACaps getDlnaCaps() {
        return dlnaCaps;
    }
    
    public DLNACaps getSecProductCaps() {
        return secProductCaps;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList();

        if (getUpc() != null) {
            // This is broken in more than half of the devices I've tested, so let's not even bother with a warning
            if (getUpc().length() != 12) {
                log.fine("UPnP specification violation, UPC must be 12 digits: " + getUpc());
            } else {
                try {
                    Long.parseLong(getUpc());
                } catch (NumberFormatException ex) {
                    log.fine("UPnP specification violation, UPC must be 12 digits all-numeric: " + getUpc());
                }
            }
        }

        return errors;
    }
}
