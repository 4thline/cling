/*
 * Copyright (C) 2012 4th Line GmbH, Switzerland
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

package org.fourthline.cling.model;

/**
 * Thrown by processors/converters when errors occurred.
 * <p>
 * This exception indicates that received data was in an invalid format and/or could
 * not be parsed or converted. You typically can recover from this failure after
 * catching (and logging?) the exception.
 * </p>
 *
 * @author Christian Bauer
 */
public class UnsupportedDataException extends RuntimeException {

    private static final long serialVersionUID = 661795454401413339L;

    protected Object data;
	
    public UnsupportedDataException(String s) {
        super(s);
    }

    public UnsupportedDataException(String s, Throwable throwable) {
        super(s, throwable);
    }
    
    public UnsupportedDataException(String s, Throwable throwable, Object data) {
        super(s, throwable);
        this.data = data;
    }
    
    public Object getData() {
    	return data;
    }

}
