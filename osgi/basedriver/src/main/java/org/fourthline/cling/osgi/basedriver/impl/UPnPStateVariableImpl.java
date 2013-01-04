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

package org.fourthline.cling.osgi.basedriver.impl;

import java.util.logging.Logger;

import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.osgi.basedriver.util.UPnPTypeUtil;

public class UPnPStateVariableImpl implements UPnPStateVariable {
    private static Logger logger = Logger.getLogger(UPnPStateVariableImpl.class.getName());
	
	private StateVariable<?> variable;
	
	public UPnPStateVariableImpl(StateVariable<?> variable) {
		this.variable = variable;
	}

	@Override
	public String getName() {
		return variable.getName();
	}

	@Override
	public Class getJavaDataType() {
		String type = variable.getTypeDetails().getDatatype().getBuiltin().getDescriptorName();
		Class<?> clazz = UPnPTypeUtil.getUPnPClass(type);
		
		if (clazz == null) {
			logger.warning(String.format("Cannot covert UPnP type %s to UPnP Java type", type));
		}
		return clazz != null ? clazz : variable.getTypeDetails().getDatatype().getClass();
	}

	@Override
	public String getUPnPDataType() {
		return variable.getTypeDetails().getDatatype().getDisplayString();
	}

	@Override
	public Object getDefaultValue() {
		return variable.getTypeDetails().getDefaultValue();
	}

	@Override
	public String[] getAllowedValues() {
		return variable.getTypeDetails().getAllowedValues();
	}

	@Override
	public Number getMinimum() {
		return (variable.getTypeDetails().getAllowedValueRange() != null) ? variable.getTypeDetails().getAllowedValueRange().getMinimum() : null;
	}

	@Override
	public Number getMaximum() {
		return (variable.getTypeDetails().getAllowedValueRange() != null) ?variable.getTypeDetails().getAllowedValueRange().getMaximum() : null;
	}

	@Override
	public Number getStep() {
		return (variable.getTypeDetails().getAllowedValueRange() != null) ? variable.getTypeDetails().getAllowedValueRange().getStep() : null;
	}

	@Override
	public boolean sendsEvents() {
		return variable.getEventDetails().isSendEvents();
	}

}
