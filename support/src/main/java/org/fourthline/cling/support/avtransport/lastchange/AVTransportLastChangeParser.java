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

package org.fourthline.cling.support.avtransport.lastchange;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.lastchange.LastChangeParser;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.util.Set;

/**
 * @author Christian Bauer
 */
public class AVTransportLastChangeParser extends LastChangeParser {

    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/AVT/";
    public static final String SCHEMA_RESOURCE = "org/fourthline/cling/support/avtransport/metadata-1.0-avt.xsd";

    @Override
    protected String getNamespace() {
        return NAMESPACE_URI;
    }

    @Override
    protected Source[] getSchemaSources() {
        // TODO: Android 2.2 has a broken SchemaFactory, we can't validate
        // http://code.google.com/p/android/issues/detail?id=9491&q=schemafactory&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
        if (!ModelUtil.ANDROID_RUNTIME) {
            return new Source[]{new StreamSource(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(SCHEMA_RESOURCE)
            )};
        }
        return null;
    }

    @Override
    protected Set<Class<? extends EventedValue>> getEventedVariables() {
        return AVTransportVariable.ALL;
    }
}
