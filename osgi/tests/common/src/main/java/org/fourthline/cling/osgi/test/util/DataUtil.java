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

public class DataUtil {
	static public boolean compareBytes(byte[] arg1, byte[] arg2) {
		boolean result = true;
		
		if (arg1.length != arg2.length) {
			result = false;
		}
		else {
			for (int i = 0; i < arg1.length; i++) {
				if (arg1[i] != arg2[i]) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	static public boolean compareBytes(Byte[] arg1, byte[] arg2) {
		byte[] bytes = new byte[arg1.length];
		for (int i = 0; i < arg1.length; i++) {
			bytes[i] = arg1[i].byteValue();
		}
		
		return compareBytes(bytes, arg2);
	}

}
