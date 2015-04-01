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
package org.fourthline.cling.support.model.dlna;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.seamless.util.MimeType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Encaspulates a MIME type (content format) and transport, protocol, additional information.
 * <p/>
 * Parses DLNA attributes in the additional information.
 *
 * @author Mario Franco
 */
public class DLNAProtocolInfo extends ProtocolInfo {

    protected final Map<DLNAAttribute.Type, DLNAAttribute> attributes = new EnumMap<>(DLNAAttribute.Type.class);

    public DLNAProtocolInfo(String s) throws InvalidValueException {
        super(s);
        parseAdditionalInfo();
    }

    public DLNAProtocolInfo(MimeType contentFormatMimeType) {
        super(contentFormatMimeType);
    }

    public DLNAProtocolInfo(DLNAProfiles profile) {
        super(MimeType.valueOf(profile.getContentFormat()));
        this.attributes.put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }

    public DLNAProtocolInfo(DLNAProfiles profile, EnumMap<DLNAAttribute.Type, DLNAAttribute> attributes) {
        super(MimeType.valueOf(profile.getContentFormat()));
        this.attributes.putAll(attributes);
        this.attributes.put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }
    
    public DLNAProtocolInfo(Protocol protocol, String network, String contentFormat, String additionalInfo) {
        super(protocol, network, contentFormat, additionalInfo);
        parseAdditionalInfo();
    }

    public DLNAProtocolInfo(Protocol protocol, String network, String contentFormat, EnumMap<DLNAAttribute.Type, DLNAAttribute> attributes) {
        super(protocol, network, contentFormat, "");
        this.attributes.putAll(attributes);
        this.additionalInfo = this.getAttributesString();
    }

    public DLNAProtocolInfo(ProtocolInfo template) {
        this(template.getProtocol(),
             template.getNetwork(),
             template.getContentFormat(),
             template.getAdditionalInfo()
        );
    }

    public boolean contains(DLNAAttribute.Type type) {
        return attributes.containsKey(type);
    }

    public DLNAAttribute getAttribute(DLNAAttribute.Type type) {
        return attributes.get(type);
    }

    public Map<DLNAAttribute.Type, DLNAAttribute> getAttributes() {
        return attributes;
    }

    protected String getAttributesString() {
        String s = "";
        for (DLNAAttribute.Type type : DLNAAttribute.Type.values() ) {
            String value = attributes.containsKey(type)?attributes.get(type).getString():null;
            if (value!=null && value.length() != 0)
                s += (s.length() == 0 ? "" : ";") + type.getAttributeName() + "=" + value;
        }
        return s;
    }

    protected void parseAdditionalInfo() {
        if (additionalInfo != null) {
            String[] atts = additionalInfo.split(";");
            for (String att : atts) {
                String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    DLNAAttribute.Type type =
                            DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        DLNAAttribute dlnaAttrinute =
                                DLNAAttribute.newInstance(type, attNameValue[1], this.getContentFormat());
                        attributes.put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }

}
