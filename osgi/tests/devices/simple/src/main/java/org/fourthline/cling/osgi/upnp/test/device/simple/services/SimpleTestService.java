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

package org.fourthline.cling.osgi.upnp.test.device.simple.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPDevice;
import org.osgi.service.upnp.UPnPLocalStateVariable;
import org.osgi.service.upnp.UPnPService;
import org.osgi.service.upnp.UPnPStateVariable;
import org.fourthline.cling.osgi.test.data.TestData;
import org.fourthline.cling.osgi.test.data.TestDataFactory;
import org.fourthline.cling.osgi.upnp.test.device.simple.actions.GetAction;
import org.fourthline.cling.osgi.upnp.test.device.simple.actions.SetAction;
import org.fourthline.cling.osgi.upnp.test.device.simple.model.TestVariable;
import org.fourthline.cling.osgi.upnp.test.device.simple.model.Simple;
import org.fourthline.cling.osgi.upnp.test.device.simple.variables.TestStateVariable;

public class SimpleTestService implements UPnPService {
	static private final String SERVICE_ID = "urn:4thline-com:serviceId:SimpleTest";
	static private final String SERVICE_TYPE = "urn:schemas-4thline-com:service:SimpleTest:1";
	static private final int FIELD_UPNP_TYPE		= 0;
	static private final int FIELD_JAVA_TYPE		= 1;
	static private final int FIELD_DEFAULT_VALUE	= 2;
	static private final int FIELD_VALUE			= 3;
	static private final String TEST_DATA_ID	= "initial";

	static private final long time = System.currentTimeMillis();
	
	static private Object[][] records = {
		// Integer              ui1, ui2, i1, i2, i4, int
		{ UPnPLocalStateVariable.TYPE_UI1,			Integer.class,		new Integer(0),		new Integer(1)		},
		{ UPnPLocalStateVariable.TYPE_UI2,			Integer.class,		new Integer(0),		new Integer(2)		},
		{ UPnPLocalStateVariable.TYPE_I1,			Integer.class,		new Integer(0),		new Integer(3)		},
		{ UPnPLocalStateVariable.TYPE_I2,			Integer.class,		new Integer(0),		new Integer(4)		},
		{ UPnPLocalStateVariable.TYPE_I4,			Integer.class,		new Integer(0),		new Integer(5)		},
		{ UPnPLocalStateVariable.TYPE_INT,			Integer.class,		new Integer(0),		new Integer(6)		},
		// Long                 ui4, time, time.tz
		//		time		Time in a subset of ISO 8601 format with no date and no time zone.
		//		time.tz		Time in a subset of ISO 8601 format with optional time zone but no date.
		{ UPnPLocalStateVariable.TYPE_UI4,			Long.class,			new Long(0),		new Long(7)			},
		{ UPnPLocalStateVariable.TYPE_TIME,			Long.class,			new Long(0),		new Long(time)		},
		{ UPnPLocalStateVariable.TYPE_TIME_TZ,		Long.class,			new Long(0),		new Long(time)		},
		// Float                r4, float
		{ UPnPLocalStateVariable.TYPE_R4,			Float.class,		new Float(0),		new Float(10.09)	},
		{ UPnPLocalStateVariable.TYPE_FLOAT,		Float.class,		new Float(0),		new Float(11.2)		},
		// Double               r8, number, fixed.14.4
		{ UPnPLocalStateVariable.TYPE_R8,			Double.class,		new Double(0),		new Double(12.3)	},
		{ UPnPLocalStateVariable.TYPE_NUMBER,		Double.class,		new Double(0),		new Double(13.3)	},
		{ UPnPLocalStateVariable.TYPE_FIXED_14_4,	Double.class,		new Double(0),		new Double(14.4)	},
		// Character            char
		{ UPnPLocalStateVariable.TYPE_CHAR,			Character.class,	new Character('A'),	new Character('A')	},
		// String               string, uri, uuid
		{ UPnPLocalStateVariable.TYPE_STRING,		String.class,		new String(),		new String("string")},
		{ UPnPLocalStateVariable.TYPE_URI,			String.class,		new String(),		new String("uri")	},
		{ UPnPLocalStateVariable.TYPE_UUID,			String.class,		new String(),		new String("uuid")	},
		// Date                 date, dateTime, dateTime.tz
		//		date		Date in a subset of ISO 8601 format without time data.
		//		dateTime	Date in ISO 8601 format with optional time but no time zone.
		//		dateTime.tz	Date in ISO 8601 format with optional time and optional time zone.
		{ UPnPLocalStateVariable.TYPE_DATE,			Date.class,			new Date(),			new Date(time)		},
		{ UPnPLocalStateVariable.TYPE_DATETIME,		Date.class,			new Date(),			new Date(time)		},
		{ UPnPLocalStateVariable.TYPE_DATETIME_TZ,	Date.class,			new Date(),			new Date(time)		},
		// Boolean
		{ UPnPLocalStateVariable.TYPE_BOOLEAN,		Boolean.class,		new Boolean(false),	new Boolean(true)	},
		// byte[]               bin.base64, bin.hex
		{ UPnPLocalStateVariable.TYPE_BIN_BASE64,	byte[].class,		new byte[] {},		new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03 }	},
		{ UPnPLocalStateVariable.TYPE_BIN_HEX,		byte[].class,		new byte[] {},		new byte[] { (byte) 0x10, (byte) 0x01, (byte) 0xF0, (byte) 0x0F }				},
	};
	
	static private TestData data = TestDataFactory.getInstance().getTestData(TEST_DATA_ID);
	
	private TestStateVariable[] variables;
	private Map<String, UPnPStateVariable> variablesIndex = new HashMap<String, UPnPStateVariable>();
	private UPnPAction[] actions;
	private Map<String, UPnPAction> actionsIndex = new HashMap<String, UPnPAction>();

	public SimpleTestService(UPnPDevice device, Simple simple) {
		List<UPnPStateVariable> variableList = new ArrayList<UPnPStateVariable>();
		
		for (Object[] record : records) {
			Object value;
			
			if (data == null) {
				value = record[FIELD_VALUE];
			}
			else {
				String name = (String) record[FIELD_UPNP_TYPE];
				String type = (String) record[FIELD_UPNP_TYPE];
				value = data.getOSGiUPnPValue(name, type, record[FIELD_VALUE]);
			}
			
			TestStateVariable variable = new TestStateVariable(
				device, this, 
				(Class<?>) record[FIELD_JAVA_TYPE], 
				(String) record[FIELD_UPNP_TYPE], 
				record[FIELD_DEFAULT_VALUE],
				true,
				new TestVariable(value));
			variableList.add(variable);
		}

		List<UPnPAction> actionList = new ArrayList<UPnPAction>();
		variables = variableList.toArray(new TestStateVariable[variableList.size()]);
		for (TestStateVariable variable : variables) {
			variablesIndex.put(variable.getName(), variable);
			
			actionList.add(new GetAction(variable));
			actionList.add(new SetAction(variable));
		}
		
		actionList.add(new GetAction("GetAllVariables", variables));
		actionList.add(new SetAction("SetAllVariables", variables));
		
		actions = actionList.toArray(new UPnPAction[actionList.size()]);
		for (UPnPAction action : actions) {
			actionsIndex.put(action.getName(), action);
		}
	}

	@Override
	public String getId() {
		return SERVICE_ID;
	}

	@Override
	public String getType() {
		return SERVICE_TYPE;
	}

	@Override
	public String getVersion() {
		return "1";
	}

	@Override
	public UPnPAction getAction(String name) {
		return actionsIndex.get(name);
	}

	@Override
	public UPnPAction[] getActions() {
		return actions;
	}

	@Override
	public UPnPStateVariable[] getStateVariables() {
		return variables;
	}

	@Override
	public UPnPStateVariable getStateVariable(String name) {
		return variablesIndex.get(name);
	}

}
