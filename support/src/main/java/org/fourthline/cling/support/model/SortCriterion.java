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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class SortCriterion {

    final protected boolean ascending;
    final protected String propertyName;

    public SortCriterion(boolean ascending, String propertyName) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    public SortCriterion(String criterion) {
        this(criterion.startsWith("+"), criterion.substring(1));
        if (!(criterion.startsWith("-") || criterion.startsWith("+")))
            throw new IllegalArgumentException("Missing sort prefix +/- on criterion: " + criterion);
    }

    public boolean isAscending() {
        return ascending;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public static SortCriterion[] valueOf(String s) {
        if (s == null || s.length() == 0) return new SortCriterion[0];
        List<SortCriterion> list = new ArrayList();
        String[] criteria = s.split(",");
        for (String criterion : criteria) {
            list.add(new SortCriterion(criterion.trim()));
        }
        return list.toArray(new SortCriterion[list.size()]);
    }

    public static String toString(SortCriterion[] criteria) {
        if (criteria == null) return "";
        StringBuilder sb = new StringBuilder();
        for (SortCriterion sortCriterion : criteria) {
            sb.append(sortCriterion.toString()).append(",");
        }
        if (sb.toString().endsWith(",")) sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ascending ? "+" : "-");
        sb.append(propertyName);
        return sb.toString();
    }
}
