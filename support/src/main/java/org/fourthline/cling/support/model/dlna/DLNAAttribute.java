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

import org.seamless.util.Exceptions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Transforms known and standardized DLNA attributes from/to string representation.
 * <p>
 * The {@link #newInstance(org.fourthline.cling.support.model.dlna.DLNAAttribute.Type, String, String)}
 * method attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Christian Bauer
 * @author Mario Franco
 */
public abstract class DLNAAttribute<T> {

    final private static Logger log = Logger.getLogger(DLNAAttribute.class.getName());

    /**
     * Maps a standardized DLNA attribute to potential attribute subtypes.
     */
    public static enum Type {

        /**
         * Order is important for DLNAProtocolInfo
         */ 
        DLNA_ORG_PN("DLNA.ORG_PN", DLNAProfileAttribute.class),
        DLNA_ORG_OP("DLNA.ORG_OP", DLNAOperationsAttribute.class),
        DLNA_ORG_PS("DLNA.ORG_PS", DLNAPlaySpeedAttribute.class),
        DLNA_ORG_CI("DLNA.ORG_CI", DLNAConversionIndicatorAttribute.class),
        DLNA_ORG_FLAGS("DLNA.ORG_FLAGS", DLNAFlagsAttribute.class);
    
        private static Map<String, Type> byName = new HashMap<String, Type>() {
            {
                for (Type t : Type.values()) {
                    put(t.getAttributeName().toUpperCase(Locale.ROOT), t);
                }
            }
        };

        private String attributeName;
        private Class<? extends DLNAAttribute>[] attributeTypes;

        @SafeVarargs
        private Type(String attributeName, Class<? extends DLNAAttribute>... attributeClass) {
            this.attributeName = attributeName;
            this.attributeTypes = attributeClass;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public Class<? extends DLNAAttribute>[] getAttributeTypes() {
            return attributeTypes;
        }

        public static Type valueOfAttributeName(String attributeName) {
            if (attributeName == null) {
                return null;
            }
            return byName.get(attributeName.toUpperCase(Locale.ROOT));
        }
    }

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * @param s  This attribute's value as a string representation.
     * @param cf This attribute's mime type as a string representation, optional.
     * @throws InvalidDLNAProtocolAttributeException
     *          If the value is invalid for this DLNA attribute.
     */
    public abstract void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException;

    /**
     * @return A string representing this attribute's value.
     */
    public abstract String getString();

    /**
     * Create a new instance of a {@link DLNAAttribute} subtype that matches the given type and value.
     * <p>
     * This method iterates through all potential attribute subtype classes as declared in {@link Type}.
     * It creates a new instance of the subtype class and calls its {@link #setString(String, String)} method.
     * If no {@link org.fourthline.cling.support.model.dlna.InvalidDLNAProtocolAttributeException} is thrown,
     * the subtype instance is returned.
     * </p>
     *
     * @param type           The type of the attribute.
     * @param attributeValue The value of the attribute.
     * @param contentFormat  The DLNA mime type of the attribute, optional.
     * @return The best matching attribute subtype instance, or <code>null</code> if no subtype can be found.
     */
    public static DLNAAttribute newInstance(DLNAAttribute.Type type, String attributeValue, String contentFormat) {

        DLNAAttribute attr = null;
        for (int i = 0; i < type.getAttributeTypes().length && attr == null; i++) {
            Class<? extends DLNAAttribute> attributeClass = type.getAttributeTypes()[i];
            try {
                log.finest("Trying to parse DLNA '" + type + "' with class: " + attributeClass.getSimpleName());
                attr = attributeClass.newInstance();
                if (attributeValue != null) {
                    attr.setString(attributeValue, contentFormat);
                }
            } catch (InvalidDLNAProtocolAttributeException ex) {
                log.finest("Invalid DLNA attribute value for tested type: " + attributeClass.getSimpleName() + " - " + ex.getMessage());
                attr = null;
            } catch (Exception ex) {
                log.severe("Error instantiating DLNA attribute of type '" + type + "' with value: " + attributeValue);
                log.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(ex));
            }
        }
        return attr;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getValue() + "'";
    }
}
