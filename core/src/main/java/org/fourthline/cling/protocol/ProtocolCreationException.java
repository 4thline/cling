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

package org.fourthline.cling.protocol;

/**
 * Recoverable error, thrown when no protocol is available to handle a UPnP message.
 *
 * @author Christian Bauer
 */
public class ProtocolCreationException extends Exception {

    public ProtocolCreationException(String s) {
        super(s);
    }

    public ProtocolCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
