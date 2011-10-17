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
