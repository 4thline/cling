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

package org.fourthline.cling.osgi.test.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class TestData {
    private static final Logger logger = Logger.getLogger(TestData.class.getName());
	private String file;
	private Properties properties;
	
	public TestData(String file) {
		this.file = file;
	}
	
	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getResourceAsStream(file);
			if (in == null) {
				logger.severe(String.format("No test data file %s.", file));
			}
			else {
				try {
					properties.load(in);
					in.close();
				} catch (IOException e) {
					logger.severe(String.format("Cannot read test data file %s.", file));
				}
			}
		}
		
		return properties;
	}

	public String getStringValue(String name) {
		String value;
		
		value = getProperties().getProperty(name);
		if (value == null) {
			logger.severe(String.format("No test data for type %s.", name));
		}
		
		return value;
	}
	
	public Object getOSGiUPnPValue(String name, String type, Object value) {
		return OSGiUPnPStringConverter.toOSGiUPnPValue(type, getStringValue(name), value);
	}
	
	public Object getOSGiUPnPValue(String name, String type) {
		return getOSGiUPnPValue(name, type, null);
	}
	
	public Object getClingUPnPValue(String type, Object value) {
		return OSGiUPnPStringConverter.toClingUPnPValue(type, value);
	}
}
