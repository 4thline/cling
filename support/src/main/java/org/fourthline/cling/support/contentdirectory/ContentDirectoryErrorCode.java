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

package org.fourthline.cling.support.contentdirectory;

/**
 * @author Alessio Gaeta
 */
public enum ContentDirectoryErrorCode {
	
	NO_SUCH_OBJECT(701, "The specified ObjectID is invalid"),
	UNSUPPORTED_SORT_CRITERIA(709, "Unsupported or invalid sort criteria"),
    CANNOT_PROCESS(720, "Cannot process the request");

	private int code;
	private String description;
	
	ContentDirectoryErrorCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
	
	public static ContentDirectoryErrorCode getByCode(int code) {
		for (ContentDirectoryErrorCode errorCode : values()) {
			if (errorCode.getCode() == code)
				return errorCode;
		}
		return null;
	}

}
