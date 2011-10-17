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

import org.seamless.xml.DOMElement;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;

/**
 * @author Christian Bauer
 */
public class MessageElement extends DOMElement<MessageElement, MessageElement> {

    public static final String XPATH_PREFIX = "m";

    public MessageElement(XPath xpath, Element element) {
        super(xpath, element);
    }

    @Override
    protected String prefix(String localName) {
        return XPATH_PREFIX + ":" + localName;
    }

    @Override
    protected Builder<MessageElement> createParentBuilder(DOMElement el) {
        return new Builder<MessageElement>(el) {
            @Override
            public MessageElement build(Element element) {
                return new MessageElement(getXpath(), element);
            }
        };
    }

    @Override
    protected ArrayBuilder<MessageElement> createChildBuilder(DOMElement el) {
        return new ArrayBuilder<MessageElement>(el) {
            @Override
            public MessageElement[] newChildrenArray(int length) {
                return new MessageElement[length];
            }

            @Override
            public MessageElement build(Element element) {
                return new MessageElement(getXpath(), element);
            }
        };
    }

}
