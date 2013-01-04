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

package org.fourthline.cling.osgi.test.util;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;

public class BundleUtil {
	static private final Object[][] states = {
		{ Integer.valueOf(Bundle.UNINSTALLED),	"UNINSTALLED" },
		{ Integer.valueOf(Bundle.INSTALLED),	"INSTALLED" },
		{ Integer.valueOf(Bundle.RESOLVED),		"RESOLVED" },
		{ Integer.valueOf(Bundle.STARTING),		"STARTING" },
		{ Integer.valueOf(Bundle.STOPPING),		"STOPPING" },
		{ Integer.valueOf(Bundle.ACTIVE),		"ACTIVE" }
	};
	
	static private Map<Integer, String> stateLookup;
	
	static private Map<Integer, String> getStateLookup() {
		if (stateLookup == null) {
			stateLookup = new Hashtable<Integer, String>();

			for (Object[] state : states) {
				stateLookup.put((Integer) state[0], (String) state[1]);
			}
		}
		
		return stateLookup;
	}
	
	static public String getBundleState(Bundle bundle) {
		String state = getStateLookup().get(Integer.valueOf(bundle.getState()));
		return state != null ? state : Integer.valueOf(bundle.getState()).toString();
	}
}
