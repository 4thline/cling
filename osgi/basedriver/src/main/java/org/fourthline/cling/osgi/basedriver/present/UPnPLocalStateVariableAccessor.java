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

package org.fourthline.cling.osgi.basedriver.present;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.osgi.basedriver.util.OSGiDataConverter;

class UPnPLocalStateVariableAccessor extends StateVariableAccessor {
    private static final Logger logger = Logger.getLogger(UPnPLocalStateVariableAccessor.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat dateTimeTZFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat timeTZFormat = new SimpleDateFormat("HH:mm:ssZ");
	private UPnPLocalStateVariable variable;
	
	public UPnPLocalStateVariableAccessor(UPnPLocalStateVariable variable) {
		this.variable = variable;
	}
	
	@Override
	public Class<?> getReturnType() {
		logger.entering(this.getClass().getName(), "getReturnType", new Object[] { });
		return variable.getJavaDataType();
	}

	@Override
	public Object read(Object serviceImpl) throws Exception {
		logger.entering(this.getClass().getName(), "read", new Object[] { serviceImpl });

		Object value = variable.getCurrentValue();
		if (value != null) {
			value = OSGiDataConverter.toClingValue(variable.getUPnPDataType(), value);
			
			try {
			} catch (InvalidValueException e) {
				logger.severe(String.format("Error accessing variable %s.", variable.getName()));
				logger.severe(e.getMessage());
			}
		}
		
		return value;
	}
}
