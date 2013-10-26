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

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.ModelUtil;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Header in SOAP action messages, naturally declaring the same thing as the body of the SOAP message.
 *
 * @author Christian Bauer
 */
public class SoapActionType {

    public static final String MAGIC_CONTROL_NS = "schemas-upnp-org";
    public static final String MAGIC_CONTROL_TYPE = "control-1-0";

    public static final Pattern PATTERN_MAGIC_CONTROL =
            Pattern.compile(Constants.NS_UPNP_CONTROL_10 +"#("+Constants.REGEX_UDA_NAME+")");

    public static final Pattern PATTERN =
            Pattern.compile("urn:(" + Constants.REGEX_NAMESPACE + "):service:(" + Constants.REGEX_TYPE + "):([0-9]+)#("+Constants.REGEX_UDA_NAME+")");

    private String namespace;
    private String type;
    private String actionName;
    private Integer version;

    public SoapActionType(ServiceType serviceType, String actionName) {
        this(serviceType.getNamespace(), serviceType.getType(), serviceType.getVersion(), actionName);
    }

    public SoapActionType(String namespace, String type, Integer version, String actionName) {
        this.namespace = namespace;
        this.type = type;
        this.version = version;
        this.actionName = actionName;

        if (actionName != null && !ModelUtil.isValidUDAName(actionName)) {
            throw new IllegalArgumentException("Action name contains illegal characters: " + actionName);
        }
    }

    public String getActionName() {
        return actionName;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getType() {
        return type;
    }

    public Integer getVersion() {
        return version;
    }

    public static SoapActionType valueOf(String s) throws InvalidValueException {
        Matcher magicControlMatcher = SoapActionType.PATTERN_MAGIC_CONTROL.matcher(s);
        
        try {
        	if (magicControlMatcher.matches()) {
        		return new SoapActionType(MAGIC_CONTROL_NS, MAGIC_CONTROL_TYPE, null, magicControlMatcher.group(1)); // throws IllegalArgumentException
        	}

        	Matcher matcher = SoapActionType.PATTERN.matcher(s);
        	if (matcher.matches())
        		return new SoapActionType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)), matcher.group(4));

        } catch(RuntimeException e) {
        	throw new InvalidValueException(String.format(
                "Can't parse action type string (namespace/type/version#actionName) '%s': %s", s, e.toString()
            ));
        }
        throw new InvalidValueException("Can't parse action type string (namespace/type/version#actionName): " + s);
    }

    public ServiceType getServiceType() {
        if (version == null) return null;
        return new ServiceType(namespace, type, version);
    }

    @Override
    public String toString() {
        return getTypeString() + "#" + getActionName();
    }

    public String getTypeString() {
        if (version == null) {
            return "urn:" + getNamespace() + ":" + getType();
        } else {
            return "urn:" + getNamespace() + ":service:" + getType() + ":" + getVersion();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof SoapActionType)) return false;

        SoapActionType that = (SoapActionType) o;

        if (!actionName.equals(that.actionName)) return false;
        if (!namespace.equals(that.namespace)) return false;
        if (!type.equals(that.type)) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + actionName.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

}