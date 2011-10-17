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

package org.fourthline.cling.binding.xml;

/**
 * Thrown if device or service descriptor metadata couldn't be read or written.
 * 
 * @author Christian Bauer
 */
public class DescriptorBindingException extends Exception {

    public DescriptorBindingException(String s) {
        super(s);
    }

    public DescriptorBindingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

