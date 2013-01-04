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

package org.fourthline.cling.osgi.upnp.test.device.simple.actions;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.osgi.upnp.test.device.simple.variables.TestStateVariable;

public class GetAction implements UPnPAction {
	private String name;
	private String[] argumentNames;
	private Map<String, UPnPStateVariable> variables = new HashMap<String, UPnPStateVariable>();
	
	public GetAction(String name, TestStateVariable[] variables) {
		this.name = name;
		
		this.argumentNames = new String[variables.length];
		for (int i = 0; i < variables.length; i++) {
			this.argumentNames[i] = String.format("%s", variables[i].getName());
			this.variables.put(argumentNames[i], variables[i]);
		}
	}

	public GetAction(TestStateVariable variable) {
		this(String.format("Get%s", variable.getName()), new TestStateVariable[] { variable });
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
		return null;
	}

	@Override
	public String[] getOutputArgumentNames() {
		return argumentNames;
	}

	@Override
	public UPnPStateVariable getStateVariable(String argumentName) {
		return variables.get(argumentName);
	}

	@Override
	public Dictionary invoke(Dictionary args) throws Exception {
		args = new Hashtable();
		for (String name : argumentNames) {
			TestStateVariable variable = (TestStateVariable) variables.get(name);
		
			args.put(name, variable.getCurrentValue());
		}
		
		return args;
	}

}
