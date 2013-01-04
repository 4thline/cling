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
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class TestDataFactory {
    private static final Logger logger = Logger.getLogger(TestDataFactory.class.getName());
	static private String DEFAULT_FILE = "test-data-factory.properties";
	static private String DEFAULT_KEY = "org.fourthline.cling.osgi.test.data.factory.properties";
	static public TestDataFactory instance;
	
	private String file = DEFAULT_FILE;
	private String key = DEFAULT_KEY;
	private Properties properties;
	private Map<String, TestData> data;

	static public TestDataFactory getInstance() {
		if (instance == null) {
			instance = new TestDataFactory();
		}
		
		return instance;
	}

	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getResourceAsStream(DEFAULT_FILE);
			if (in == null) {
				logger.severe(String.format("No test data factory file %s.", file));
			}
			else {
				try {
					properties.load(in);
					in.close();
				} catch (IOException e) {
					logger.severe(String.format("Cannot read test data factory file %s.", file));
				}
			}
		}
		
		return properties;
	}

	public TestDataFactory() {
		file = System.getProperty(key, file);
	}
	
	public TestData getTestData(String id) {
		
		if (data == null) {
			data = new Hashtable<String, TestData>();
			Properties properties = getProperties();
			
			for (Object key : properties.keySet()) {
				String value = properties.getProperty((String) key);
				
				data.put((String) key, new TestData(value));
			}
		}
		
		return data.get(id);
	}
	
	public static void main(String[] args) {
		TestData data = TestDataFactory.getInstance().getTestData("initial");
		
		System.out.printf("data: %s\n", data);
		System.out.printf("int: %s\n", data.getOSGiUPnPValue("int", "float"));
		
	}
}
