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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Christian Bauer
 */
public class MANHeader extends UpnpHeader<String> {

    public static final Pattern PATTERN = Pattern.compile("\"(.+?)\"(;.+?)??");
    public static final Pattern NAMESPACE_PATTERN = Pattern.compile(";\\s?ns\\s?=\\s?([0-9]{2})");

    public String namespace;

    public MANHeader() {
    }

    public MANHeader(String value) {
        setValue(value);
    }

    public MANHeader(String value, String namespace) {
        this(value);
        this.namespace = namespace;
    }

    public void setString(String s) throws InvalidHeaderException {

        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches()) {
            setValue(matcher.group(1));

            if (matcher.group(2) != null) {
                Matcher nsMatcher = NAMESPACE_PATTERN.matcher(matcher.group(2));
                if (nsMatcher.matches()) {
                    setNamespace(nsMatcher.group(1));
                } else {
                    throw new InvalidHeaderException("Invalid namespace in MAN header value: " + s);
                }
            }

        } else {
            throw new InvalidHeaderException("Invalid MAN header value: " + s);
        }
    }

    public String getString() {
        if (getValue() == null) return null;
        StringBuilder s = new StringBuilder();
        s.append("\"").append(getValue()).append("\"");
        if (getNamespace() != null) s.append("; ns=").append(getNamespace());
        return s.toString();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
