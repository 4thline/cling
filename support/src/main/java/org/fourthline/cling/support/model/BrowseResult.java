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

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public class BrowseResult {

    protected String result;
    protected UnsignedIntegerFourBytes count;
    protected UnsignedIntegerFourBytes totalMatches;
    protected UnsignedIntegerFourBytes containerUpdateID;

    public BrowseResult(String result, UnsignedIntegerFourBytes count,
                        UnsignedIntegerFourBytes totalMatches,
                        UnsignedIntegerFourBytes containerUpdateID) {
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
                new UnsignedIntegerFourBytes(count),
                new UnsignedIntegerFourBytes(totalMatches),
                new UnsignedIntegerFourBytes(updatedId)
        );
    }

    public String getResult() {
        return result;
    }

    public UnsignedIntegerFourBytes getCount() {
        return count;
    }

    public long getCountLong() {
        return count.getValue();
    }

    public UnsignedIntegerFourBytes getTotalMatches() {
        return totalMatches;
    }

    public long getTotalMatchesLong() {
        return totalMatches.getValue();
    }

    public UnsignedIntegerFourBytes getContainerUpdateID() {
        return containerUpdateID;
    }

    public long getContainerUpdateIDLong() {
        return containerUpdateID.getValue();
    }


}
