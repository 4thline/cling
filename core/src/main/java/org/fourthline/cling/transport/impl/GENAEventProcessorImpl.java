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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.model.UnsupportedDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation based on the <em>W3C DOM</em> XML processing API.
 *
 * @author Christian Bauer
 */
public class GENAEventProcessorImpl implements GENAEventProcessor, ErrorHandler {

    private static Logger log = Logger.getLogger(GENAEventProcessor.class.getName());

    protected DocumentBuilderFactory createDocumentBuilderFactory() throws FactoryConfigurationError {
    	return DocumentBuilderFactory.newInstance();
    }

    public void writeBody(OutgoingEventRequestMessage requestMessage) throws UnsupportedDataException {
        log.fine("Writing body of: " + requestMessage);

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document d = factory.newDocumentBuilder().newDocument();
            Element propertysetElement = writePropertysetElement(d);

            writeProperties(d, propertysetElement, requestMessage);

            requestMessage.setBody(UpnpMessage.BodyType.STRING, toString(d));

            if (log.isLoggable(Level.FINER)) {
                log.finer("===================================== GENA BODY BEGIN ============================================");
                log.finer(requestMessage.getBody().toString());
                log.finer("====================================== GENA BODY END =============================================");
            }

        } catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex);
        }
    }

    public void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {

        log.fine("Reading body of: " + requestMessage);
        if (log.isLoggable(Level.FINER)) {
            log.finer("===================================== GENA BODY BEGIN ============================================");
            log.finer(requestMessage.getBody() != null ? requestMessage.getBody().toString() : "null");
            log.finer("-===================================== GENA BODY END ============================================");
        }

        String body = getMessageBody(requestMessage);
        try {

            DocumentBuilderFactory factory = createDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);

            Document d = documentBuilder.parse(
                new InputSource(new StringReader(body))
            );

            Element propertysetElement = readPropertysetElement(d);

            readProperties(propertysetElement, requestMessage);

        } catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex, body);
        }
    }

    /* ##################################################################################################### */

    protected Element writePropertysetElement(Document d) {
        Element propertysetElement = d.createElementNS(Constants.NS_UPNP_EVENT_10, "e:propertyset");
        d.appendChild(propertysetElement);
        return propertysetElement;
    }

    protected Element readPropertysetElement(Document d) {

        Element propertysetElement = d.getDocumentElement();
        if (propertysetElement == null || !getUnprefixedNodeName(propertysetElement).equals("propertyset")) {
            throw new RuntimeException("Root element was not 'propertyset'");
        }
        return propertysetElement;
    }

    /* ##################################################################################################### */

    protected void writeProperties(Document d, Element propertysetElement, OutgoingEventRequestMessage message) {
        for (StateVariableValue stateVariableValue : message.getStateVariableValues()) {
            Element propertyElement = d.createElementNS(Constants.NS_UPNP_EVENT_10, "e:property");
            propertysetElement.appendChild(propertyElement);
            XMLUtil.appendNewElement(
                    d,
                    propertyElement,
                    stateVariableValue.getStateVariable().getName(),
                    stateVariableValue.toString()
            );
        }
    }

    protected void readProperties(Element propertysetElement, IncomingEventRequestMessage message) {
        NodeList propertysetElementChildren = propertysetElement.getChildNodes();

        StateVariable[] stateVariables = message.getService().getStateVariables();

        for (int i = 0; i < propertysetElementChildren.getLength(); i++) {
            Node propertysetChild = propertysetElementChildren.item(i);

            if (propertysetChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (getUnprefixedNodeName(propertysetChild).equals("property")) {

                NodeList propertyChildren = propertysetChild.getChildNodes();

                for (int j = 0; j < propertyChildren.getLength(); j++) {
                    Node propertyChild = propertyChildren.item(j);

                    if (propertyChild.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    String stateVariableName = getUnprefixedNodeName(propertyChild);
                    for (StateVariable stateVariable : stateVariables) {
                        if (stateVariable.getName().equals(stateVariableName)) {
                            log.fine("Reading state variable value: " + stateVariableName);
                            String value = XMLUtil.getTextContent(propertyChild);
                            message.getStateVariableValues().add(
                                    new StateVariableValue(stateVariable, value)
                            );
                            break;
                        }
                    }

                }
            }
        }
    }

    /* ##################################################################################################### */

    protected String getMessageBody(UpnpMessage message) throws UnsupportedDataException {
        if (!message.isBodyNonEmptyString())
            throw new UnsupportedDataException(
                "Can't transform null or non-string/zero-length body of: " + message
            );
        return message.getBodyString().trim();
    }

    protected String toString(Document d) throws Exception {
        // Just to be safe, no newline at the end
        String output = XMLUtil.documentToString(d);
        while (output.endsWith("\n") || output.endsWith("\r")) {
            output = output.substring(0, output.length() - 1);
        }

        return output;
    }

    protected String getUnprefixedNodeName(Node node) {
        return node.getPrefix() != null
                ? node.getNodeName().substring(node.getPrefix().length() + 1)
                : node.getNodeName();
    }

    public void warning(SAXParseException e) throws SAXException {
        log.warning(e.toString());
    }

    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }
}

