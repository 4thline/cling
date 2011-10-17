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
