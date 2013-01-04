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

package org.fourthline.cling.support.shared;

/**
 * @author Christian Bauer
 */
public class AWTExceptionHandler {

    public void handle(Throwable ex) {
        System.err.println("============= The application encountered an unrecoverable error, exiting... =============");
        ex.printStackTrace(System.err);
        System.err.println("==========================================================================================");
        System.exit(1);
    }

}
