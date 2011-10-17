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

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

/**
 * @author TK Kocheran &lt;rfkrocktk@gmail.com&gt;
 */
public class SearchResult {

    protected String result;

    protected UnsignedIntegerFourBytes count;

    protected UnsignedIntegerFourBytes totalMatches;

    protected UnsignedIntegerFourBytes containerUpdateID;

    public SearchResult(String result, UnsignedIntegerFourBytes count,
                        UnsignedIntegerFourBytes totalMatches,
                        UnsignedIntegerFourBytes containerUpdateID) {
        this.result = result;
        this.count = count;
        this.totalMatches = totalMatches;
        this.containerUpdateID = containerUpdateID;
    }

    public SearchResult(String result, long count, long totalMatches) {
        this(result, count, totalMatches, 0);
    }

    public SearchResult(String result, long count, long totalMatches, long updateID) {
        this(
                result,
                new UnsignedIntegerFourBytes(count),
                new UnsignedIntegerFourBytes(totalMatches),
                new UnsignedIntegerFourBytes(updateID)
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
