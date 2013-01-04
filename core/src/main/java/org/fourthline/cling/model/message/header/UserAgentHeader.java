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

package org.fourthline.cling.model.message.header;

/**
 * A simple string, not validated (everyone gets this wrong)
 *
 * @author Christian Bauer
 * @author Michael Pujos
 */
public class UserAgentHeader extends UpnpHeader<String> {
	
	public UserAgentHeader() {
    }
	
	public UserAgentHeader(String s) {
        setValue(s);
    }

	@Override
	public void setString(String s) throws InvalidHeaderException {
		setValue(s);
	}

	@Override
	public String getString() {
		return getValue();
	}
}
