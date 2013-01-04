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

/**
 *
 * @author Mario Franco
 */
public class PragmaType {

    private String token;
    private boolean quote;
    private String value;

    public PragmaType(String token, String value, boolean quote) {
        this.token = token;
        this.value = value;
        this.quote = quote;
    }
    
    public PragmaType(String token, String value) {
        this.token = token;
        this.value = value;
    }

    public PragmaType(String value) {
        this.token = null;
        this.value = value;
    }

    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @return String format of Bytes Range for response message header 
     */
    public String getString() {
        String s ="";
        if (token!=null)
            s += token + "=";

        s += quote? "\""+value+"\"" : value;
        return s;
    }

    public static PragmaType valueOf(String s) throws InvalidValueException {
        if (s.length() != 0) {
            String token=null, value = null;
            boolean quote = false;
            String[] params = s.split("=");
            if (params.length > 1) {
                token = params[0];
                value = params[1];
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    quote = true;
                    value = value.substring(1, value.length()-1);
                }
            }
            else {
                value = s;
            }
            return new PragmaType(token, value, quote);
        }
        throw new InvalidValueException("Can't parse Bytes Range: " + s);
    }

}
