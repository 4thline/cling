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

package org.fourthline.cling.model.types;

import org.fourthline.cling.model.Constants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Service type with a fixed <code>schemas-upnp-org</code> namespace.
 * 
 * @author Christian Bauer
 */
public class UDAServiceType extends ServiceType {

    public static final String DEFAULT_NAMESPACE = "schemas-upnp-org";

    // This pattern also accepts decimal versions, not only integers (as would be required by UDA), but cuts off fractions
    public static final Pattern PATTERN =
            Pattern.compile("urn:" + DEFAULT_NAMESPACE + ":service:(" + Constants.REGEX_TYPE + "):([0-9]+).*");

    public UDAServiceType(String type) {
        this(type, 1);
    }

    public UDAServiceType(String type, int version) {
        super(DEFAULT_NAMESPACE, type, version);
    }

    public static UDAServiceType valueOf(String s) throws InvalidValueException {
        Matcher matcher = UDAServiceType.PATTERN.matcher(s);
        if (matcher.matches()) {
            return new UDAServiceType(matcher.group(1), Integer.valueOf(matcher.group(2)));
        } else {
            throw new InvalidValueException("Can't parse UDA service type string (namespace/type/version): " + s);
        }
    }

}
