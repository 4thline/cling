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

package org.fourthline.cling.support.model;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public enum BrowseFlag {
	
	METADATA("BrowseMetadata"),
	DIRECT_CHILDREN("BrowseDirectChildren");
	
	private String protocolString;
	
	BrowseFlag(String protocolString) {
		this.protocolString = protocolString;
	}

	@Override
	public String toString() {
		return protocolString;
	}
	
	public static BrowseFlag valueOrNullOf(String s) {
		for (BrowseFlag browseFlag : values()) {
			if (browseFlag.toString().equals(s))
				return browseFlag;
		}
		return null;
	}

}
