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

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.Constants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Christian Bauer
 */
public class TimeoutHeader extends UpnpHeader<Integer> {

    // It's probably OK to assume that "infinite" means 4000 years?
    public static final Integer INFINITE_VALUE = Integer.MAX_VALUE;

    public static final Pattern PATTERN = Pattern.compile("Second-(?:([0-9]+)|infinite)");

    public TimeoutHeader() {
        setValue(Constants.DEFAULT_SUBSCRIPTION_DURATION_SECONDS);
    }

    public TimeoutHeader(int timeoutSeconds) {
        setValue(timeoutSeconds);
    }

    public TimeoutHeader(Integer timeoutSeconds) {
        setValue(timeoutSeconds);
    }

    public void setString(String s) throws InvalidHeaderException {

        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new InvalidHeaderException("Can't parse timeout seconds integer from: " + s);
        }

        if (matcher.group(1) != null) {
            setValue(Integer.parseInt(matcher.group(1)));
        } else {
            setValue(INFINITE_VALUE);
        }

    }

    public String getString() {
        return "Second-" + (getValue().equals(INFINITE_VALUE) ? "infinite" : getValue());
    }
}