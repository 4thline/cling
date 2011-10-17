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
