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

package org.fourthline.cling.osgi.upnp.test.device.simple.actions;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.osgi.upnp.test.device.simple.variables.TestStateVariable;

public class SetAction implements UPnPAction {
	private String name;
	private String[] argumentNames;
	private Map<String, UPnPStateVariable> variables = new HashMap<String, UPnPStateVariable>();
	
	public SetAction(String name, TestStateVariable[] variables) {
		this.name = name;
		
		this.argumentNames = new String[variables.length];
		for (int i = 0; i < variables.length; i++) {
			this.argumentNames[i] = String.format("%s", variables[i].getName());
			this.variables.put(argumentNames[i], variables[i]);
		}
	}

	public SetAction(TestStateVariable variable) {
		this(String.format("Set%s", variable.getName()), new TestStateVariable[] { variable });
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getReturnArgumentName() {
		return null;
	}

	@Override
	public String[] getInputArgumentNames() {
		return argumentNames;
	}

	@Override
	public String[] getOutputArgumentNames() {
		return null;
	}

	@Override
	public UPnPStateVariable getStateVariable(String argumentName) {
		return variables.get(argumentName);
	}

	@Override
	public Dictionary invoke(Dictionary args) throws Exception {
		for (Object key : Collections.list(args.keys())) {
			String name = (String) key;
			Object value = (Object) args.get(key);

			TestStateVariable variable = (TestStateVariable) variables.get(name);
			variable.setCurrentValue(value);
		}
		
		return null;
	}

}
