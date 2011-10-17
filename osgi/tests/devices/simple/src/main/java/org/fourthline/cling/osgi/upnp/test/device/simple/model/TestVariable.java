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
