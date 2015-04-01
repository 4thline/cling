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

package org.fourthline.cling.model;

import org.w3c.dom.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * XML handling and printing shortcuts.
 * <p>
 * This class exists because Android 2.1 does not offer any way to print an <code>org.w3c.dom.Document</code>,
 * and it also doesn't implement the most trivial methods to build a DOM (although the API is provided, they
 * fail at runtime). We might be able to remove this class once compatibility for Android 2.1 can be
 * dropped.
 * </p>
 *
 * @author Christian Bauer
 */
public class XMLUtil {

    /* TODO: How it should be done (nice API, eh?)
    public static String documentToString(Document document) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        transFactory.setAttribute("indent-number", 4);
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(d), new StreamResult(out));
        return out.toString();
    }
    */

    // TODO: Evil methods to print XML on Android 2.1 (there is no TransformerFactory)

    public static String documentToString(Document document) throws Exception {
        return documentToString(document, true);
    }

    public static String documentToString(Document document, boolean standalone) throws Exception {
        String prol = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"" + (standalone ? "yes" : "no") + "\"?>";
        return prol + nodeToString(document.getDocumentElement(), new HashSet<String>(), document.getDocumentElement().getNamespaceURI());
    }

    public static String documentToFragmentString(Document document) throws Exception {
        return nodeToString(document.getDocumentElement(), new HashSet<String>(), document.getDocumentElement().getNamespaceURI());
    }

    protected static String nodeToString(Node node, Set<String> parentPrefixes, String namespaceURI) throws Exception {
        StringBuilder b = new StringBuilder();

        if (node == null) {
            return "";
        }

        if (node instanceof Element) {
            Element element = (Element) node;
            b.append("<");
            b.append(element.getNodeName());

            Map<String, String> thisLevelPrefixes = new HashMap<>();
            if (element.getPrefix() != null && !parentPrefixes.contains(element.getPrefix())) {
                thisLevelPrefixes.put(element.getPrefix(), element.getNamespaceURI());
            }

            if (element.hasAttributes()) {
                NamedNodeMap map = element.getAttributes();
                for (int i = 0; i < map.getLength(); i++) {
                    Node attr = map.item(i);
                    if (attr.getNodeName().startsWith("xmlns")) continue;
                    if (attr.getPrefix() != null && !parentPrefixes.contains(attr.getPrefix())) {
                        thisLevelPrefixes.put(attr.getPrefix(), element.getNamespaceURI());
                    }
                    b.append(" ");
                    b.append(attr.getNodeName());
                    b.append("=\"");
                    b.append(attr.getNodeValue());
                    b.append("\"");
                }
            }

            if (namespaceURI != null && !thisLevelPrefixes.containsValue(namespaceURI) &&
                    !namespaceURI.equals(element.getParentNode().getNamespaceURI())) {
                b.append(" xmlns=\"").append(namespaceURI).append("\"");
            }

            for (Map.Entry<String, String> entry : thisLevelPrefixes.entrySet()) {
                b.append(" xmlns:").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                parentPrefixes.add(entry.getKey());
            }

            NodeList children = element.getChildNodes();
            boolean hasOnlyAttributes = true;
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() != Node.ATTRIBUTE_NODE) {
                    hasOnlyAttributes = false;
                    break;
                }
            }
            if (!hasOnlyAttributes) {
                b.append(">");
                for (int i = 0; i < children.getLength(); i++) {
                    b.append(nodeToString(children.item(i), parentPrefixes, children.item(i).getNamespaceURI()));
                }
                b.append("</");
                b.append(element.getNodeName());
                b.append(">");
            } else {
                b.append("/>");
            }

            for (String thisLevelPrefix : thisLevelPrefixes.keySet()) {
                parentPrefixes.remove(thisLevelPrefix);
            }

        } else if (node.getNodeValue() != null) {
            b.append(encodeText(node.getNodeValue(), node instanceof Attr));
        }

        return b.toString();
    }

    public static String encodeText(String s) {
        return encodeText(s, true);
    }

    public static String encodeText(String s, boolean encodeQuotes) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        if(encodeQuotes) {
        	s = s.replaceAll("'", "&apos;");
        	s = s.replaceAll("\"", "&quot;");
        }
        return s;
    }

    public static Element appendNewElement(Document document, Element parent, Enum el) {
        return appendNewElement(document, parent, el.toString());
    }

    public static Element appendNewElement(Document document, Element parent, String element) {
        Element child = document.createElement(element);
        parent.appendChild(child);
        return child;
    }

    public static Element appendNewElementIfNotNull(Document document, Element parent, Enum el, Object content) {
        return appendNewElementIfNotNull(document, parent, el, content, null);
    }

    public static Element appendNewElementIfNotNull(Document document, Element parent, Enum el, Object content, String namespace) {
        return appendNewElementIfNotNull(document, parent, el.toString(), content, namespace);
    }

    public static Element appendNewElementIfNotNull(Document document, Element parent, String element, Object content) {
        return appendNewElementIfNotNull(document, parent, element, content, null);
    }

    public static Element appendNewElementIfNotNull(Document document, Element parent, String element, Object content, String namespace) {
        if (content == null) return parent;
        return appendNewElement(document, parent, element, content, namespace);
    }

    public static Element appendNewElement(Document document, Element parent, String element, Object content) {
        return appendNewElement(document, parent, element, content, null);
    }

    public static Element appendNewElement(Document document, Element parent, String element, Object content, String namespace) {
        Element childElement;
        if (namespace != null) {
            childElement = document.createElementNS(namespace, element);
        } else {
            childElement = document.createElement(element);
        }

        if (content != null) {
            // TODO: We'll have that on Android 2.2:
            // childElement.setTextContent(content.toString());
            // Meanwhile:
            childElement.appendChild(document.createTextNode(content.toString()));
        }

        parent.appendChild(childElement);
        return childElement;
    }

    // TODO: Of course, there is no Element.getTextContent() either...
    public static String getTextContent(Node node) {
        StringBuffer buffer = new StringBuffer();
        NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node child = childList.item(i);
            if (child.getNodeType() != Node.TEXT_NODE)
                continue; // skip non-text nodes
            buffer.append(child.getNodeValue());
        }
        return buffer.toString();
    }

}