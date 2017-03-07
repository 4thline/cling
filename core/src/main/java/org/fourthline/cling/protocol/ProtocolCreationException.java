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

package org.fourthline.cling.protocol;

/**
 * Recoverable error, thrown when no protocol is available to handle a UPnP message.
 *
 * @author Christian Bauer
 */
public class ProtocolCreationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3716311492738810440L;

	public ProtocolCreationException(String s) {
        super(s);
    }

    public ProtocolCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
