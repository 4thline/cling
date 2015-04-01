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

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.binding.staging.MutableAction;
import org.fourthline.cling.binding.staging.MutableActionArgument;
import org.fourthline.cling.binding.staging.MutableAllowedValueRange;
import org.fourthline.cling.binding.staging.MutableService;
import org.fourthline.cling.binding.staging.MutableStateVariable;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import org.fourthline.cling.model.types.CustomDatatype;
import org.fourthline.cling.model.types.Datatype;
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static org.fourthline.cling.binding.xml.Descriptor.Service.ATTRIBUTE;
import static org.fourthline.cling.binding.xml.Descriptor.Service.ELEMENT;
import static org.fourthline.cling.model.XMLUtil.appendNewElement;
import static org.fourthline.cling.model.XMLUtil.appendNewElementIfNotNull;

/**
 * Implementation based on JAXP DOM.
 *
 * @author Christian Bauer
 */
public class UDA10ServiceDescriptorBinderImpl implements ServiceDescriptorBinder, ErrorHandler {

    private static Logger log = Logger.getLogger(ServiceDescriptorBinder.class.getName());

    public <S extends Service> S describe(S undescribedService, String descriptorXml) throws DescriptorBindingException, ValidationException {
        if (descriptorXml == null || descriptorXml.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }

        try {
            log.fine("Populating service from XML descriptor: " + undescribedService);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);

            Document d = documentBuilder.parse(
                new InputSource(
                    // TODO: UPNP VIOLATION: Virgin Media Superhub sends trailing spaces/newlines after last XML element, need to trim()
                    new StringReader(descriptorXml.trim())
                )
            );

            return describe(undescribedService, d);

        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not parse service descriptor: " + ex.toString(), ex);
        }
    }

    public <S extends Service> S describe(S undescribedService, Document dom) throws DescriptorBindingException, ValidationException {
        try {
            log.fine("Populating service from DOM: " + undescribedService);

            // Read the XML into a mutable descriptor graph
            MutableService descriptor = new MutableService();

            hydrateBasic(descriptor, undescribedService);

            Element rootElement = dom.getDocumentElement();
            hydrateRoot(descriptor, rootElement);

            // Build the immutable descriptor graph
            return buildInstance(undescribedService, descriptor);

        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not parse service DOM: " + ex.toString(), ex);
        }
    }

    protected <S extends Service> S buildInstance(S undescribedService, MutableService descriptor) throws ValidationException {
        return (S)descriptor.build(undescribedService.getDevice());
    }

    protected void hydrateBasic(MutableService descriptor, Service undescribedService) {
        descriptor.serviceId = undescribedService.getServiceId();
        descriptor.serviceType = undescribedService.getServiceType();
        if (undescribedService instanceof RemoteService) {
            RemoteService rs = (RemoteService) undescribedService;
            descriptor.controlURI = rs.getControlURI();
            descriptor.eventSubscriptionURI = rs.getEventSubscriptionURI();
            descriptor.descriptorURI = rs.getDescriptorURI();
        }
    }

    protected void hydrateRoot(MutableService descriptor, Element rootElement)
            throws DescriptorBindingException {

        // We don't check the XMLNS, nobody bothers anyway...

        if (!ELEMENT.scpd.equals(rootElement)) {
            throw new DescriptorBindingException("Root element name is not <scpd>: " + rootElement.getNodeName());
        }

        NodeList rootChildren = rootElement.getChildNodes();

        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node rootChild = rootChildren.item(i);

            if (rootChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.specVersion.equals(rootChild)) {
                // We don't care about UDA major/minor specVersion anymore - whoever had the brilliant idea that
                // the spec versions can be declared on devices _AND_ on their services should have their fingers
                // broken so they never touch a keyboard again.
                // hydrateSpecVersion(descriptor, rootChild);
            } else if (ELEMENT.actionList.equals(rootChild)) {
                hydrateActionList(descriptor, rootChild);
            } else if (ELEMENT.serviceStateTable.equals(rootChild)) {
                hydrateServiceStateTableList(descriptor, rootChild);
            } else {
                log.finer("Ignoring unknown element: " + rootChild.getNodeName());
            }
        }

    }

    /*
    public void hydrateSpecVersion(MutableService descriptor, Node specVersionNode)
            throws DescriptorBindingException {

        NodeList specVersionChildren = specVersionNode.getChildNodes();
        for (int i = 0; i < specVersionChildren.getLength(); i++) {
            Node specVersionChild = specVersionChildren.item(i);

            if (specVersionChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            MutableUDAVersion version = new MutableUDAVersion();
            if (ELEMENT.major.equals(specVersionChild)) {
                version.major = Integer.valueOf(XMLUtil.getTextContent(specVersionChild));
            } else if (ELEMENT.minor.equals(specVersionChild)) {
                version.minor = Integer.valueOf(XMLUtil.getTextContent(specVersionChild));
            }
        }
    }
    */

    public void hydrateActionList(MutableService descriptor, Node actionListNode) throws DescriptorBindingException {

        NodeList actionListChildren = actionListNode.getChildNodes();
        for (int i = 0; i < actionListChildren.getLength(); i++) {
            Node actionListChild = actionListChildren.item(i);

            if (actionListChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.action.equals(actionListChild)) {
                MutableAction action = new MutableAction();
                hydrateAction(action, actionListChild);
                descriptor.actions.add(action);
            }
        }
    }

    public void hydrateAction(MutableAction action, Node actionNode) {

        NodeList actionNodeChildren = actionNode.getChildNodes();
        for (int i = 0; i < actionNodeChildren.getLength(); i++) {
            Node actionNodeChild = actionNodeChildren.item(i);

            if (actionNodeChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.name.equals(actionNodeChild)) {
                action.name = XMLUtil.getTextContent(actionNodeChild);
            } else if (ELEMENT.argumentList.equals(actionNodeChild)) {


                NodeList argumentChildren = actionNodeChild.getChildNodes();
                for (int j = 0; j < argumentChildren.getLength(); j++) {
                    Node argumentChild = argumentChildren.item(j);

                    if (argumentChild.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    MutableActionArgument actionArgument = new MutableActionArgument();
                    hydrateActionArgument(actionArgument, argumentChild);
                    action.arguments.add(actionArgument);
                }
            }
        }

    }

    public void hydrateActionArgument(MutableActionArgument actionArgument, Node actionArgumentNode) {

        NodeList argumentNodeChildren = actionArgumentNode.getChildNodes();
        for (int i = 0; i < argumentNodeChildren.getLength(); i++) {
            Node argumentNodeChild = argumentNodeChildren.item(i);

            if (argumentNodeChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.name.equals(argumentNodeChild)) {
                actionArgument.name = XMLUtil.getTextContent(argumentNodeChild);
            } else if (ELEMENT.direction.equals(argumentNodeChild)) {
                String directionString = XMLUtil.getTextContent(argumentNodeChild);
                try {
                    actionArgument.direction = ActionArgument.Direction.valueOf(directionString.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ex) {
                    // TODO: UPNP VIOLATION: Pelco SpectraIV-IP uses illegal value INOUT
                    log.warning("UPnP specification violation: Invalid action argument direction, assuming 'IN': " + directionString);
                    actionArgument.direction = ActionArgument.Direction.IN;
                }
            } else if (ELEMENT.relatedStateVariable.equals(argumentNodeChild)) {
                actionArgument.relatedStateVariable = XMLUtil.getTextContent(argumentNodeChild);
            } else if (ELEMENT.retval.equals(argumentNodeChild)) {
                actionArgument.retval = true;
            }
        }
    }

    public void hydrateServiceStateTableList(MutableService descriptor, Node serviceStateTableNode) {

        NodeList serviceStateTableChildren = serviceStateTableNode.getChildNodes();
        for (int i = 0; i < serviceStateTableChildren.getLength(); i++) {
            Node serviceStateTableChild = serviceStateTableChildren.item(i);

            if (serviceStateTableChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.stateVariable.equals(serviceStateTableChild)) {
                MutableStateVariable stateVariable = new MutableStateVariable();
                hydrateStateVariable(stateVariable, (Element) serviceStateTableChild);
                descriptor.stateVariables.add(stateVariable);
            }
        }
    }

    public void hydrateStateVariable(MutableStateVariable stateVariable, Element stateVariableElement) {

        stateVariable.eventDetails = new StateVariableEventDetails(
                stateVariableElement.getAttribute("sendEvents") != null &&
                        stateVariableElement.getAttribute(ATTRIBUTE.sendEvents.toString()).toUpperCase(Locale.ROOT).equals("YES")
        );

        NodeList stateVariableChildren = stateVariableElement.getChildNodes();
        for (int i = 0; i < stateVariableChildren.getLength(); i++) {
            Node stateVariableChild = stateVariableChildren.item(i);

            if (stateVariableChild.getNodeType() != Node.ELEMENT_NODE)
                continue;

            if (ELEMENT.name.equals(stateVariableChild)) {
                stateVariable.name = XMLUtil.getTextContent(stateVariableChild);
            } else if (ELEMENT.dataType.equals(stateVariableChild)) {
                String dtName = XMLUtil.getTextContent(stateVariableChild);
                Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(dtName);
                stateVariable.dataType = builtin != null ? builtin.getDatatype() : new CustomDatatype(dtName);
            } else if (ELEMENT.defaultValue.equals(stateVariableChild)) {
                stateVariable.defaultValue = XMLUtil.getTextContent(stateVariableChild);
            } else if (ELEMENT.allowedValueList.equals(stateVariableChild)) {

                List<String> allowedValues = new ArrayList<>();

                NodeList allowedValueListChildren = stateVariableChild.getChildNodes();
                for (int j = 0; j < allowedValueListChildren.getLength(); j++) {
                    Node allowedValueListChild = allowedValueListChildren.item(j);

                    if (allowedValueListChild.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    if (ELEMENT.allowedValue.equals(allowedValueListChild))
                        allowedValues.add(XMLUtil.getTextContent(allowedValueListChild));
                }

                stateVariable.allowedValues = allowedValues;

            } else if (ELEMENT.allowedValueRange.equals(stateVariableChild)) {

                MutableAllowedValueRange range = new MutableAllowedValueRange();

                NodeList allowedValueRangeChildren = stateVariableChild.getChildNodes();
                for (int j = 0; j < allowedValueRangeChildren.getLength(); j++) {
                    Node allowedValueRangeChild = allowedValueRangeChildren.item(j);

                    if (allowedValueRangeChild.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    if (ELEMENT.minimum.equals(allowedValueRangeChild)) {
                        try {
                            range.minimum = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                        } catch (Exception ex) {
                        }
                    } else if (ELEMENT.maximum.equals(allowedValueRangeChild)) {
                        try {
                            range.maximum = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                        } catch (Exception ex) {
                        }
                    } else if (ELEMENT.step.equals(allowedValueRangeChild)) {
                        try {
                            range.step = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                        } catch (Exception ex) {
                        }
                    }
                }

                stateVariable.allowedValueRange = range;
            }
        }
    }

    public String generate(Service service) throws DescriptorBindingException {
        try {
            log.fine("Generating XML descriptor from service model: " + service);

            return XMLUtil.documentToString(buildDOM(service));

        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not build DOM: " + ex.getMessage(), ex);
        }
    }

    public Document buildDOM(Service service) throws DescriptorBindingException {

        try {
            log.fine("Generating XML descriptor from service model: " + service);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document d = factory.newDocumentBuilder().newDocument();
            generateScpd(service, d);

            return d;

        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not generate service descriptor: " + ex.getMessage(), ex);
        }
    }

    private void generateScpd(Service serviceModel, Document descriptor) {

        Element scpdElement = descriptor.createElementNS(Descriptor.Service.NAMESPACE_URI, ELEMENT.scpd.toString());
        descriptor.appendChild(scpdElement);

        generateSpecVersion(serviceModel, descriptor, scpdElement);
        if (serviceModel.hasActions()) {
            generateActionList(serviceModel, descriptor, scpdElement);
        }
        generateServiceStateTable(serviceModel, descriptor, scpdElement);
    }

    private void generateSpecVersion(Service serviceModel, Document descriptor, Element rootElement) {
        Element specVersionElement = appendNewElement(descriptor, rootElement, ELEMENT.specVersion);
        appendNewElementIfNotNull(descriptor, specVersionElement, ELEMENT.major, serviceModel.getDevice().getVersion().getMajor());
        appendNewElementIfNotNull(descriptor, specVersionElement, ELEMENT.minor, serviceModel.getDevice().getVersion().getMinor());
    }

    private void generateActionList(Service serviceModel, Document descriptor, Element scpdElement) {

        Element actionListElement = appendNewElement(descriptor, scpdElement, ELEMENT.actionList);

        for (Action action : serviceModel.getActions()) {
            if (!action.getName().equals(QueryStateVariableAction.ACTION_NAME))
                generateAction(action, descriptor, actionListElement);
        }
    }

    private void generateAction(Action action, Document descriptor, Element actionListElement) {

        Element actionElement = appendNewElement(descriptor, actionListElement, ELEMENT.action);

        appendNewElementIfNotNull(descriptor, actionElement, ELEMENT.name, action.getName());

        if (action.hasArguments()) {
            Element argumentListElement = appendNewElement(descriptor, actionElement, ELEMENT.argumentList);
            for (ActionArgument actionArgument : action.getArguments()) {
                generateActionArgument(actionArgument, descriptor, argumentListElement);
            }
        }
    }

    private void generateActionArgument(ActionArgument actionArgument, Document descriptor, Element actionElement) {

        Element actionArgumentElement = appendNewElement(descriptor, actionElement, ELEMENT.argument);

        appendNewElementIfNotNull(descriptor, actionArgumentElement, ELEMENT.name, actionArgument.getName());
        appendNewElementIfNotNull(descriptor, actionArgumentElement, ELEMENT.direction, actionArgument.getDirection().toString().toLowerCase(Locale.ROOT));
        if (actionArgument.isReturnValue()) {
            // TODO: UPNP VIOLATION: WMP12 will discard RenderingControl service if it contains <retval> tags
            log.warning("UPnP specification violation: Not producing <retval> element to be compatible with WMP12: " + actionArgument);
            // appendNewElement(descriptor, actionArgumentElement, ELEMENT.retval);
        }
        appendNewElementIfNotNull(descriptor, actionArgumentElement, ELEMENT.relatedStateVariable, actionArgument.getRelatedStateVariableName());
    }

    private void generateServiceStateTable(Service serviceModel, Document descriptor, Element scpdElement) {

        Element serviceStateTableElement = appendNewElement(descriptor, scpdElement, ELEMENT.serviceStateTable);

        for (StateVariable stateVariable : serviceModel.getStateVariables()) {
            generateStateVariable(stateVariable, descriptor, serviceStateTableElement);
        }
    }

    private void generateStateVariable(StateVariable stateVariable, Document descriptor, Element serviveStateTableElement) {

        Element stateVariableElement = appendNewElement(descriptor, serviveStateTableElement, ELEMENT.stateVariable);

        appendNewElementIfNotNull(descriptor, stateVariableElement, ELEMENT.name, stateVariable.getName());

        if (stateVariable.getTypeDetails().getDatatype() instanceof CustomDatatype) {
            appendNewElementIfNotNull(descriptor, stateVariableElement, ELEMENT.dataType,
                    ((CustomDatatype)stateVariable.getTypeDetails().getDatatype()).getName());
        } else {
            appendNewElementIfNotNull(descriptor, stateVariableElement, ELEMENT.dataType,
                    stateVariable.getTypeDetails().getDatatype().getBuiltin().getDescriptorName());
        }

        appendNewElementIfNotNull(descriptor, stateVariableElement, ELEMENT.defaultValue,
                stateVariable.getTypeDetails().getDefaultValue());

        // The default is 'yes' but we generate it anyway just to be sure
        if (stateVariable.getEventDetails().isSendEvents()) {
            stateVariableElement.setAttribute(ATTRIBUTE.sendEvents.toString(), "yes");
        } else {
            stateVariableElement.setAttribute(ATTRIBUTE.sendEvents.toString(), "no");
        }

        if (stateVariable.getTypeDetails().getAllowedValues() != null) {
            Element allowedValueListElement = appendNewElement(descriptor, stateVariableElement, ELEMENT.allowedValueList);
            for (String allowedValue : stateVariable.getTypeDetails().getAllowedValues()) {
                appendNewElementIfNotNull(descriptor, allowedValueListElement, ELEMENT.allowedValue, allowedValue);
            }
        }

        if (stateVariable.getTypeDetails().getAllowedValueRange() != null) {
            Element allowedValueRangeElement = appendNewElement(descriptor, stateVariableElement, ELEMENT.allowedValueRange);
            appendNewElementIfNotNull(
                    descriptor, allowedValueRangeElement, ELEMENT.minimum, stateVariable.getTypeDetails().getAllowedValueRange().getMinimum()
            );
            appendNewElementIfNotNull(
                    descriptor, allowedValueRangeElement, ELEMENT.maximum, stateVariable.getTypeDetails().getAllowedValueRange().getMaximum()
            );
            if (stateVariable.getTypeDetails().getAllowedValueRange().getStep() >= 1l) {
                appendNewElementIfNotNull(
                        descriptor, allowedValueRangeElement, ELEMENT.step, stateVariable.getTypeDetails().getAllowedValueRange().getStep()
                );
            }
        }

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

