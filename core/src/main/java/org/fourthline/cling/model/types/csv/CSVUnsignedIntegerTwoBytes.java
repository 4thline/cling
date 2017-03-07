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

package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

/**
 * @author Christian Bauer
 */
public class CSVUnsignedIntegerTwoBytes extends CSV<UnsignedIntegerTwoBytes> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4333427434256114058L;

	public CSVUnsignedIntegerTwoBytes() {
    }

    public CSVUnsignedIntegerTwoBytes(String s) throws InvalidValueException {
        super(s);
    }
}