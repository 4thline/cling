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
package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.ErrorCode;

/**
 * @author Christian Bauer
 */
public class ActionCancelledException extends ActionException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5399403090729026586L;

	public ActionCancelledException(InterruptedException cause) {
        super(ErrorCode.ACTION_FAILED, "Action execution interrupted", cause);
    }
}
