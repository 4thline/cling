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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.UnsignedIntegerEightBytes;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public class BrowseResult {

    protected String                    result;
    protected UnsignedIntegerEightBytes count;
    protected UnsignedIntegerEightBytes totalMatches;
    protected UnsignedIntegerEightBytes containerUpdateID;

    public BrowseResult(String result, UnsignedIntegerEightBytes count,
                        UnsignedIntegerEightBytes totalMatches,
                        UnsignedIntegerEightBytes containerUpdateID) {
        this.result = result;
        this.count = count;
        this.totalMatches = totalMatches;
        this.containerUpdateID = containerUpdateID;
    }

    public BrowseResult(String result, long count, long totalMatches) {
        this(result, count, totalMatches, 0);
    }

    public BrowseResult(String result, long count, long totalMatches, long updatedId) {
        this(
                result,
                new UnsignedIntegerEightBytes(count),
                new UnsignedIntegerEightBytes(totalMatches),
                new UnsignedIntegerEightBytes(updatedId)
        );
    }

    public String getResult() {
        return result;
    }

    public UnsignedIntegerEightBytes getCount() {
        return count;
    }

    public long getCountLong() {
        return count.getValue();
    }

    public UnsignedIntegerEightBytes getTotalMatches() {
        return totalMatches;
    }

    public long getTotalMatchesLong() {
        return totalMatches.getValue();
    }

    public UnsignedIntegerEightBytes getContainerUpdateID() {
        return containerUpdateID;
    }

    public long getContainerUpdateIDLong() {
        return containerUpdateID.getValue();
    }


}
