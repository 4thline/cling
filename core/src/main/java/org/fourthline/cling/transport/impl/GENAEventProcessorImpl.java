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

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.UnsupportedDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
public class GENAEventProcessorImpl implements GENAEventProcessor {

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
                log.finer("-===================================== GENA BODY END ============================================");
            }

        } catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex);
        }
    }

    public void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {

        log.fine("Reading body of: " + requestMessage);
        if (log.isLoggable(Level.FINER)) {
            log.finer("===================================== GENA BODY BEGIN ============================================");
            log.finer(requestMessage.getBody().toString());
            log.finer("-===================================== GENA BODY END ============================================");
        }

        if (requestMessage.getBody() == null || !requestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
            throw new UnsupportedDataException("Can't transform null or non-string body of: " + requestMessage);
        }

        try {

            DocumentBuilderFactory factory = createDocumentBuilderFactory();
            factory.setNamespaceAware(true);

            // TODO: UPNP VIOLATION: Netgear 834DG DSL Router sends trailing spaces/newlines after last XML element, need to trim()
            Document d = factory.newDocumentBuilder().parse(
                    new InputSource(
                            new StringReader(requestMessage.getBodyString().trim())
                    )
            );

            Element propertysetElement = readPropertysetElement(d);

            readProperties(propertysetElement, requestMessage);

        } catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex);
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
}

