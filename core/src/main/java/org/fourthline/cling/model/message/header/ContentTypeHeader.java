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

import org.seamless.util.MimeType;

/**
 * @author Christian Bauer
 */
public class ContentTypeHeader extends UpnpHeader<MimeType> {

    public static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/xml");
    public static final MimeType DEFAULT_CONTENT_TYPE_UTF8 = MimeType.valueOf("text/xml;charset=\"utf-8\"");

    public ContentTypeHeader() {
        setValue(DEFAULT_CONTENT_TYPE);
    }

    public ContentTypeHeader(MimeType contentType) {
        setValue(contentType);
    }

    public ContentTypeHeader(String s) throws InvalidHeaderException{
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        setValue(MimeType.valueOf(s));
    }

    public String getString() {
        return getValue().toString();
    }

    public boolean isUDACompliantXML() {
        // UDA spec says "must be text/xml", however, sometimes you get a charset token as well...
        return isText() && getValue().getSubtype().equals(DEFAULT_CONTENT_TYPE.getSubtype());
    }

    public boolean isText() {
        return getValue() != null && getValue().getType().equals(DEFAULT_CONTENT_TYPE.getType());
    }
}
