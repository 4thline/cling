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
