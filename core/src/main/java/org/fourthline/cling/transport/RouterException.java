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
package org.fourthline.cling.transport;

/**
 * Thrown by the {@link Router} if a non-fatal recoverable exception occurred.
 * <p>
 * This exception is thrown if the calling thread wasn't able to obtain
 * exclusive read/write access on the router.
 * </p>
 * <p>
 * This exception is also thrown when you interrupt the thread calling the
 * router.  In such a case, the cause of this is an <code>InterruptedException</code>.
 * </p>
 *
 * @author Christian Bauer
 */
public class RouterException extends Exception {

    public RouterException() {
        super();
    }

    public RouterException(String s) {
        super(s);
    }

    public RouterException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RouterException(Throwable throwable) {
        super(throwable);
    }
}
