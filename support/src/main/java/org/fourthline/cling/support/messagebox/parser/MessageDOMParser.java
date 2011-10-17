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

package org.fourthline.cling.support.messagebox.parser;

import org.seamless.xml.DOMParser;
import org.seamless.xml.NamespaceContextMap;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;

/**
 * @author Christian Bauer
 */
public class MessageDOMParser extends DOMParser<MessageDOM> {

    @Override
    protected MessageDOM createDOM(Document document) {
        return new MessageDOM(document);
    }

    public NamespaceContextMap createDefaultNamespaceContext(String... optionalPrefixes) {
        NamespaceContextMap ctx = new NamespaceContextMap() {
            @Override
            protected String getDefaultNamespaceURI() {
                return MessageDOM.NAMESPACE_URI;
            }
        };
        for (String optionalPrefix : optionalPrefixes) {
            ctx.put(optionalPrefix, MessageDOM.NAMESPACE_URI);
        }
        return ctx;
    }

    public XPath createXPath() {
        return super.createXPath(createDefaultNamespaceContext(MessageElement.XPATH_PREFIX));
    }

}
