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

package org.fourthline.cling.osgi.upnp.test.device.simple.model;

import java.util.Observable;
import java.util.logging.Logger;

public class TestVariable extends Observable {
    private static Logger logger = Logger.getLogger(TestVariable.class.getName());
	private Object value;

	public TestVariable(Object value) {
		setValue(value);
	}
	
	public void setValue(Object value) {
		logger.entering(this.getClass().getName(), "setValue", new Object[] { value });
		if (this.value == null || !this.value.equals(value)) {
			logger.finer(String.format("old: %s  new: %s", this.value, value));
			
			this.value = value;
			setChanged();
			notifyObservers(this);
		}
	}

	public Object getValue() {
		return value;
	}
	
}
