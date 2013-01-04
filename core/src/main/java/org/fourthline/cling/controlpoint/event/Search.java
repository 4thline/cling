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

package org.fourthline.cling.controlpoint.event;

import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;

/**
 * @author Christian Bauer
 */
public class Search {

    protected UpnpHeader searchType = new STAllHeader();
    protected int mxSeconds = MXHeader.DEFAULT_VALUE;

    public Search() {
    }

    public Search(UpnpHeader searchType) {
        this.searchType = searchType;
    }

    public Search(UpnpHeader searchType, int mxSeconds) {
        this.searchType = searchType;
        this.mxSeconds = mxSeconds;
    }

    public Search(int mxSeconds) {
        this.mxSeconds = mxSeconds;
    }

    public UpnpHeader getSearchType() {
        return searchType;
    }

    public int getMxSeconds() {
        return mxSeconds;
    }
}
